package org.ivdnt.fcs.mapping;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.ivdnt.fcs.results.Kwic;
import org.ivdnt.fcs.results.ResultSet;
import org.ivdnt.util.StringUtils;

import eu.clarin.sru.server.fcs.parser.CqpWriter;
import eu.clarin.sru.server.fcs.parser.QueryNode;
import eu.clarin.sru.server.fcs.parser.QueryProcessor;

/**
 * This class stores the tag set conversion tables and holds methods for
 * translating annotations written in a given tag set into another.
 * 
 * 
 * Hm. Already at this level, a single feature might translate to a disjunction
 * or a conjunction of other features, or a disjunction of conjunctions. this
 * should also cover translation of word= to t_lc= for Nederlab, etc.
 *
 * @author jesse
 *
 */
public class ConversionEngine {

	private ArrayList<ConcurrentHashMap<String, String>> fieldMappingArr;
	private ArrayList<ConcurrentHashMap<String, ConcurrentHashMap<String, String>>> featureMappingArr;

	private String name;

	private boolean useFeatureRegex = false;
	private String posTagField = null;
	private String[] grammaticalFeatures = {};
	private String quote = "'";
	private boolean includeFeatureNameInRegex = true;

	// hash mapping a fieldname A onto another fieldname B

	private Map<String, String> fieldMap = new ConcurrentHashMap<>();

	// hash mapping a feature in tagset A (universal dependencies)
	// onto a set of features in tagset B (tagset of Nederlab, BlackLab, whatever)

	private Map<Feature, Set<FeatureConjunction>> featureMap = new ConcurrentHashMap<>(); // te simpel, moet naar
																							// featureConjunction of
																							// disjunction kunnen mappen

	// kind of the contrary of featureMap, so as to be able
	// to translate FeatureConjunctions BACK into Universal dependencies

	private Map<FeatureConjunction, Set<Feature>> featureBackMap = new ConcurrentHashMap<>();

	// this one maps FeatureConjunction to their keySet

	private Map<FeatureConjunction, HashSet<String>> featureBackMap2KeySet = new ConcurrentHashMap<>();

	// When translating FeatureConjunctions into universal dependencies,
	// we will need to be able to get the most complex FeatureConjunctions first
	// (always process most complex cases first) and if those cases don't match
	// we will try to match with the less complex FeatureConjunctions (more general
	// cases)

	private ArrayList<FeatureConjunction> sortedFeatureConjunctionsAccording2Complexity = new ArrayList<FeatureConjunction>();
	
	
	private ConcurrentHashMap<String, HashSet<String>> featureName2FeatureValues;
	private ConcurrentHashMap<String, HashSet<String>> posTag2FeatureNames;

	// ---------------------------------------------------------------------------------------

	// constructor

	public ConversionEngine(ArrayList<ConcurrentHashMap<String, String>> fieldMappingArr,
			ArrayList<ConcurrentHashMap<String, ConcurrentHashMap<String, String>>> featureMappingArr, String conversionName) {
		this.fieldMappingArr = fieldMappingArr;
		this.featureMappingArr = featureMappingArr;
		if (conversionName.equals("UD2CGNNederlab")) {
			this.featureName2FeatureValues = CgnMaps.featureName2FeatureValuesNederlab;
			this.posTag2FeatureNames = CgnMaps.posTag2FeatureNamesNederlab;
		}
		else if (conversionName.equals("UD2CGNSonar")) {
			this.featureName2FeatureValues = CgnMaps.featureName2FeatureValuesSonar;
			this.posTag2FeatureNames = CgnMaps.posTag2FeatureNamesSonar;
		}
		
		init();
	}

	// subroutine of constructor

	private void init() {

		for (int i = 0; i < this.fieldMappingArr.size(); i++) {
			ConcurrentHashMap<String, String> onePair = this.fieldMappingArr.get(i);
			this.fieldMap.put(onePair.get("from"), onePair.get("to"));
		}

		// this part is about pos tags translation

		for (int i = 0; i < this.featureMappingArr.size(); i++) {
			// get a single conversion set FROM -> TO

			ConcurrentHashMap<String, ConcurrentHashMap<String, String>> oneSet = this.featureMappingArr.get(i);
			ConcurrentHashMap<String, String> fromSet = removeCommentsFromHash(oneSet.get("from"));
			ConcurrentHashMap<String, String> toSet = removeCommentsFromHash(oneSet.get("to"));

			// Source tag
			// ----------
			// (since the source has only one feature, we extract the key and value
			// and build one single Feature object with it)

			// see:
			// https://stackoverflow.com/questions/26230225/hashmap-getting-first-key-value

			Map.Entry<String, String> entry = fromSet.entrySet().iterator().next();
			String featureFrom = entry.getKey();
			String featureTo = entry.getValue();

			Feature sourceFeature = new Feature(featureFrom, featureTo);

			// Destination tag
			// ---------------
			// (instead of source, the destination may contain different features,
			// so we'll loop through the keys and values)

			FeatureConjunction featureConjunction = new FeatureConjunction();
			
			for (String oneKey : toSet.keySet()) {
				String featureConjFrom = oneKey;
				String featureConjTo = toSet.get(oneKey);

				featureConjunction.put(featureConjFrom, featureConjTo);
			}
			
			/* TODO: remove old code
			// featureConjunctionBack is featureConjunction with omitted 'feat.'
			FeatureConjunction featureConjunctionBack = new FeatureConjunction();
			for (String oneKey : toSet.keySet()) {
				String featureConjFrom = removeFeat(oneKey);
				String featureConjTo = toSet.get(oneKey);

				featureConjunctionBack.put(featureConjFrom, featureConjTo);
			}*/
			

			// A set of FeatureConjunction means that we have different
			// possible translations
			// set = { (conjunction A) OR (conjunction B) OR (...) }

			Set<FeatureConjunction> destinationFeatures = this.featureMap.get(sourceFeature);
			if (destinationFeatures == null)
				destinationFeatures = new HashSet<FeatureConjunction>();

			destinationFeatures.add(featureConjunction);

			// store this in the featureMap, so we can use it for feature translation!

			this.featureMap.put(sourceFeature, destinationFeatures);

			// HERE kind of mirrored procedure, for the translation of result sets in their
			// own tag set
			// back into universal dependencies

			// the map for translation back into universal dependencies doesn't contain
			// a set (OR-relation, meaning different possible translations)
			// but only a featureConjunction (conjunctions of features)
			//
			Set<Feature> universalDependencies = this.featureBackMap.get(featureConjunction);

			if (universalDependencies == null)
				universalDependencies = new HashSet<Feature>();

			universalDependencies.add(sourceFeature);

			this.featureBackMap.put(featureConjunction, universalDependencies);

		}

		// We also need a sorted version of the featureBackMap keySet.
		// We need this because when (later on!) translating the POS's of the resultSet
		// back
		// into universal dependencies, we will first try to match the most complex
		// sets of features we know a translation of (=the special cases), and if those
		// don't match, we will then try to match less complex sets of features
		// (=general cases)
		// [which is the only correct way to do it]

		for (FeatureConjunction oneConjunction : this.featureBackMap.keySet()) {
			HashSet<String> releventKeys = new HashSet<String>();
			releventKeys.addAll(oneConjunction.keySet());
			this.featureBackMap2KeySet.put(oneConjunction, releventKeys);
		}

		// build a list of the FeatureConjunction
		// decreasingly sorted according to complexity

		this.sortedFeatureConjunctionsAccording2Complexity.addAll(this.featureBackMap.keySet());

		Collections.sort(this.sortedFeatureConjunctionsAccording2Complexity, new Comparator<FeatureConjunction>() {
			@Override
			public int compare(FeatureConjunction o1, FeatureConjunction o2) {
				return Integer.compare(o2.size(), o1.size());
			}
		});

	}

	private String removeFeat(String oneKey) {
		String[] oneKeySplit = oneKey.split("\\.");
		// Use last element, which removes 'feat.' if present
		String featureConjFrom = oneKeySplit[oneKeySplit.length-1];
		return featureConjFrom;
	}

	// ---------------------------------------------------------------------------------------

	// getters and setters etc

	public String[] getGrammaticalFeatures() {
		return this.grammaticalFeatures;
	}

	public void setGrammaticalFeatures(String[] grammaticalFeatures) {
		this.grammaticalFeatures = grammaticalFeatures;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean usesFeatureRegex() {
		return this.useFeatureRegex;
	}

	public void setUseFeatureRegex(boolean useFeatureRegex) {
		this.useFeatureRegex = useFeatureRegex;
	}

	public String getPosTagField() {
		return this.posTagField;
	}

	public void setPosTagField(String posTagField) {
		this.posTagField = posTagField;
	}

	public String getQuote() {
		return this.quote;
	}

	public void setQuote(String quote) {
		if (quote == null)
			quote = "'";
		this.quote = quote;
	}

	//
	public boolean hasIncludedFeatureNameInRegex() {
		return this.includeFeatureNameInRegex;
	}

	public void setIncludeFeatureNameInRegex(boolean includeFeatureNameInRegex) {
		this.includeFeatureNameInRegex = includeFeatureNameInRegex;
	}

	public String toString() {
		return "Conversion(" + this.name + ")";
	}

	// ---------------------------------------------------------------------------------------

	/**
	 * Translate a query, meaning: converting universal dependencies
	 * (cross-linguistically consistent grammatical annotations) into the set of
	 * tags and features instantiated in this class
	 * 
	 * Input is the query part of FSC request, like:
	 * 
	 * query= [word="lopen"][pos="NOUN"]
	 * 
	 * Output is CQP, like:
	 * 
	 * cqp="[word="lopen"] [pos="^(N).*" & pos=".*[\(,\|](soort)[,\)\|].*"]
	 */
	public String translateQuery(String query) {
		// convert the query into nodes
		// and translate the tags and features

		QueryProcessor qp = new QueryProcessor(this);
		QueryNode queryAsNode = qp.rewrite(query); // this one will translate the features

		// convert the nodes into CQP

		CqpWriter cqpWriter = new CqpWriter();
		cqpWriter.setQuote(this.getQuote());

		if (this.usesFeatureRegex())
			cqpWriter.setRegexHack(this.getPosTagField(), this.getGrammaticalFeatures(),
					this.hasIncludedFeatureNameInRegex());

		return cqpWriter.writeAsCQP(queryAsNode);
	}

	/**
	 * Translate a feature put in a given tag set into another tag set
	 * 
	 * @param feature
	 * @param value
	 * @return
	 */
	public Set<FeatureConjunction> translateFeature(String feature, String value) {

		Feature source = new Feature(feature, value);
		Set<FeatureConjunction> destination = this.featureMap.get(source);

		if (destination == null) {
			//throw new NullPointerException("Mapping not found for feature " + feature + " with value " + value);
			FeatureConjunction fc = new FeatureConjunction();
			fc.put(source.getFeatureName(), source.getValues()); // TODO geen pass-through meer als niet gemapt
			destination = new HashSet<>();
			destination.add(fc);
		}

		return destination;
	}

	/**
	 * Translate the parts of speech of the result set (in the Kwic objects) back
	 * into universal dependencies
	 * 
	 * @param resultSet
	 * @return modified resultSet
	 */
	public void translateIntoUniversalDependencies(ResultSet resultSet) {

		// get all the results (keywords in context) out of the resultSet

		List<Kwic> kwics = resultSet.getHits();
		
		// Compile regex pattern once, to be matched against in the loop:
		Pattern posTagPattern = Pattern.compile("^([A-Z-]+)\\((.*)\\)$");

		// now loop through the results

		for (Kwic oneKeywordAndContext : kwics) {
			List<String> universalDependencies = new ArrayList<String>();

			// loop through one results

			for (int index = 0; index < oneKeywordAndContext.size(); index++) {
				// gather the properties of the current token that DO have a value

				HashSet<String> featureNamesOfCurrentToken = new HashSet<String>();

				for (String pname : oneKeywordAndContext.getTokenPropertyNames()) {

					String propValue = oneKeywordAndContext.get(pname, index);

					if (propValue != null && !propValue.isEmpty()) {
						//TODO: remove old code
						//pname = removeFeat(pname);
						// We removed 'feat.', so add updated feature name
						//oneKeywordAndContext.addTokenProperty(pname);
						//oneKeywordAndContext.setTokenPropertyAt(pname, propValue, index);
						featureNamesOfCurrentToken.add(pname);
						
					}
				}

				// Special case:
				// In some search engines, the different grammatical features are
				// not specified in separate token properties (t.i. layers)
				// but are concatenated in the pos-tag, eg. like
				//
				// NOU-C(gender=n,number=sg)
				//
				// In such cases, we need to extract the features out of the brackets
				// and add each of them to our list of token properties:
				//
				// pos = NOU-C
				// gender= n
				// number= sg

				if (featureNamesOfCurrentToken.contains("pos")) {
					String posTag = oneKeywordAndContext.get("pos", index);
					Matcher posTagMatcher = posTagPattern.matcher(posTag);
					if (posTagMatcher.matches()) {
						// pos tag

						String realPosTag = posTagMatcher.replaceAll("$1");

						oneKeywordAndContext.setTokenPropertyAt("pos", realPosTag, index);

						// features

						String featuresStr = posTagMatcher.replaceAll("$2");
						
						
						if (!featuresStr.isEmpty()) {
							String[] features = featuresStr.split(",");
							// Before iterating over features, pick out pdtype feature.
							// We need to give this to calls of getFeatureName for all other features
							// for some disambiguation cases.
							String pdtype = "";
							for (String oneFeature : features) {
								// Get list of feature values belonging to pdtype
								// We have to do a check here, because pdtype is called feat.pdtype in nederlab
								HashSet<String> valueList;
								if (this.featureName2FeatureValues.containsKey("pdtype")) {
									valueList = this.featureName2FeatureValues.get("pdtype");
								}
								else {
									valueList = this.featureName2FeatureValues.get("feat.pdtype");
								}
								
								if (valueList.contains(oneFeature)) {
									pdtype = oneFeature;
								}
							}
	
							for (String oneFeature : features) {
								String featureName;
								String featureValue;
	
								// We normally expect a string like 'featureName = featureValue'
								// but in the case of CGN, we might have feature values only.
								// In that case, we need to add the feature name ourself.
								if (oneFeature.split("=").length == 1) {
									featureName = CgnFeatureDecoder.getFeatureName(realPosTag, pdtype, this.featureName2FeatureValues, this.posTag2FeatureNames, oneFeature);
									featureValue = oneFeature;
								}
								// normal case:
								// input is a string like 'featureName = featureValue'
								else {
									featureName = oneFeature.split("=")[0];
									featureValue = oneFeature.split("=")[1];
								}
								// TODO: remove old code
								// Remove 'feat.' from feature names when mapping back
								//featureName = removeFeat(featureName);
								// add the feature to the token properties
								oneKeywordAndContext.addTokenProperty(featureName);
								oneKeywordAndContext.setTokenPropertyAt(featureName, featureValue, index);
	
								featureNamesOfCurrentToken.add(featureName);
	
							}
						}
					}

				}
				
				// try to match the features of the current token
				// with our list of known features translations

				Set<Feature> universalDependencyOfCurrentToken = null;

				for (FeatureConjunction oneKnownFeatureConjunction : this.sortedFeatureConjunctionsAccording2Complexity) {
					// does the current token contain this known feature conjunction?
					// (the contain relation supports both exact and partial match)

					HashSet<String> keysOfThisKnownFeatureConjunction = this.featureBackMap2KeySet
							.get(oneKnownFeatureConjunction);
					
					// 'feat.' has been removed from the current features and the map, so we can compare
					if (featureNamesOfCurrentToken.containsAll(keysOfThisKnownFeatureConjunction)) {

						// if so, build a feature conjunction representing the token
						// with the same set of properties as our know FeatureConjunction

						FeatureConjunction featureConjunctionOfToken = new FeatureConjunction();
						for (String onePropertyName : keysOfThisKnownFeatureConjunction) {
							String featureValue = oneKeywordAndContext.get(onePropertyName, index);
							if (featureValue != null)
								featureConjunctionOfToken.put(onePropertyName, featureValue);
						}

						// than, try to find this FeatureConjunction in our list of known
						// FeatureConjunctions
						// (t.i. match both feature keys and feature values)
						System.out.println("before");
						universalDependencyOfCurrentToken = this.featureBackMap.get(featureConjunctionOfToken);
						System.out.println("after");
						// if translation is found, we are done with this token!
						if (universalDependencyOfCurrentToken != null) {
							break;
						}

					}
				}

				// now, if we have found a translation for the current token
				// add this to the properties we already have

				ArrayList<String> universalDependenciesOfCurrentToken = new ArrayList<String>();
				if (universalDependencyOfCurrentToken != null) {
					for (Feature oneFeature : universalDependencyOfCurrentToken) {
						universalDependenciesOfCurrentToken.add(StringUtils.join(oneFeature.getValues(), "|"));
					}

				}

				universalDependencies.add(StringUtils.join(universalDependenciesOfCurrentToken, " OR "));

			} // end of loop through tokens

			// add new feature for output
			// (we want the output to contain the original features of the engine
			// the result comes from, but also one new feature 'universal dependency'
			// containing the translation of features of the results)

			oneKeywordAndContext.addTokenPropertyName(MappingConstants.UNIVERSAL_DEPENDENCY_FEATURENAME);

			oneKeywordAndContext.setTokenProperties(MappingConstants.UNIVERSAL_DEPENDENCY_FEATURENAME,
					universalDependencies);

		} // end of loop through results

	}

	/**
	 * Remove the 'comment' key for a tagset hash
	 * 
	 * NB: the 'comment key' is needed in the JSON file to be able to add comment to
	 * some data, but we don't want it to make it to the conversion engine as it is
	 * no part of the tags of features to be mapped to another tag set
	 * 
	 * @param hash
	 * @return
	 */
	private static ConcurrentHashMap<String, String> removeCommentsFromHash(ConcurrentHashMap<String, String> hash) {

		hash.remove("comment");

		return hash;
	}

	// --------------------------------------------------------------------------

	// for test only

	public static void main(String[] args) {

		ConversionEngine ct = ConversionObjectProcessor.getConversionEngine("UD2CHN");

		String q = "[pos='PROPN'][lemma='aap' & pos='NOUN' & Number='Plur'] [pos='DET'][pos='CCONJ'][lemma='niet.*']";
		q = "[pos=\"NOUN\" & Number=\"Plur\"][pos=\"VERB\"]";
		q = "[pos=\"VERB\" & Tense=\"Past\" & VerbForm=\"Fin\"][pos=\"PROPN\"] within sentence";
		// q = "[pos='AUX' | pos = 'SCONJ'][pos='DET'][]{0,7}[pos='INTJ']";
		// Conversion.bla(q);

		/*
		 * AttackOfTheClones x = new AttackOfTheClones(ct);
		 * 
		 * QueryNode rw = x.rewrite(q); String rws = rw.toString();
		 * System.out.println(rws); WriteAsCQP wasqp = new WriteAsCQP();
		 * wasqp.setQuote("\""); wasqp.setRegexHack(ct.posTagField,
		 * ct.grammaticalFeatures, ct.includeFeatureNameInRegex);
		 */

		System.out.println(ct.translateQuery(q));
	}
}
