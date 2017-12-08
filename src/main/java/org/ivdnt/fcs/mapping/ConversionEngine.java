package org.ivdnt.fcs.mapping;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import org.antlr.v4.runtime.misc.Utils;
import org.ivdnt.fcs.results.Kwic;
import org.ivdnt.fcs.results.ResultSet;
import org.ivdnt.util.StringUtils;

import eu.clarin.sru.server.fcs.FCSQueryParser;
import eu.clarin.sru.server.fcs.parser.CqpWriter;
import eu.clarin.sru.server.fcs.parser.QueryNode;
import eu.clarin.sru.server.fcs.parser.QueryProcessor;



/**
 * This class stores the tag set conversion tables
 * and holds methods for translating annotations written in a given tag set into another.
 * 
 * 
 * Hm.
 * Already at this level, a single feature might translate to a disjunction or a conjunction 
 * of other features, or a disjunction of conjunctions. 
 * this should also cover translation of word= to t_lc= for Nederlab, etc.
 *
 * @author jesse
 *
 */
public class ConversionEngine 
{
	private String[][] fieldMapping;
	private String[][] featureMapping;

	private String name;	
	
	private boolean useFeatureRegex = false;
	private String posTagField = null;
	private String[] grammaticalFeatures = {};
	private String quote = "'";
	private boolean includeFeatureNameInRegex = true;

	private Map<String, String> fieldMap = new ConcurrentHashMap<>();
	
	// hash mapping a feature in tagset A (universal dependencies)
	// onto a set of features in tagset B (tagset of Nederlab, BlackLab, whatever)
	
	private Map<Feature, Set<FeatureConjunction>> featureMap = 
			new ConcurrentHashMap<>(); // te simpel, moet naar featureConjunction of disjunction kunnen mappen
	
	
	// kind of the contrary of featureMap, so as to be able
	// to translate FeatureConjunctions BACK into Universal dependencies
	
	private Map<FeatureConjunction, Set<Feature>> featureBackMap = 
			new ConcurrentHashMap<>();
	
	// this one maps FeatureConjunction to their keySet
	private Map<FeatureConjunction, HashSet<String>> featureBackMap2KeySet = 
			new ConcurrentHashMap<>();
	
	// When translating FeatureConjunctions into universal dependencies,
	// we will need to be able to get the most complex FeatureConjunctions first
	// (always process most complex cases first) and if those cases don't match
	// we will try to match with the less complex FeatureConjunctions (more general cases)
	
	private ArrayList<FeatureConjunction> sortedFeatureConjunctionsAccording2Complexity =
			new ArrayList<FeatureConjunction>();

	

	
	// ---------------------------------------------------------------------------------------
	
	
	// constructor
	
	public ConversionEngine(String[][] fieldMapping, String[][] featureMapping) 
	{
		this.fieldMapping = fieldMapping;
		this.featureMapping = featureMapping;
		init();
	}
	
	
	// subroutine of constructor
	
	private void init() {
		for (String[] x : this.fieldMapping) {
			this.fieldMap.put(x[0], x[1]); // 
		}
		
		// this part is about pos tags translation
		
		for (String[] x : this.featureMapping) 
		{
			// source tag
			
			Feature sourceFeature = new Feature(x[0], x[1]); // [0 = key, 1 = value]
			
			// destination tag
			
			FeatureConjunction featureConjunction = new FeatureConjunction();
			for (int i=2; i < x.length; i+=2)
				featureConjunction.put(x[i], x[i+1]);
			
			
			
			// A set of FeatureConjunction means that we have different
			// possible translations
			//  set = { (conjunction A) OR (conjunction B) OR (...) }
			
			Set<FeatureConjunction> destinationFeatures = this.featureMap.get(sourceFeature);
			if (destinationFeatures == null) 
				destinationFeatures = new HashSet<FeatureConjunction>();
			
			destinationFeatures.add(featureConjunction);
			
			// store this in the featureMap, so we can use it for feature translation!
			
			this.featureMap.put(sourceFeature, destinationFeatures);	
			
			
			
			// HERE kind of mirrored procedure, for the translation back into universal dependencies
			
			// the map for translation back into universal dependencies doesn't contain
			// a set (OR-relation, meaning different possible translations) 
			// but only a featureConjunction (conjunctions of features)			
			
			Set<Feature> universalDependencies = this.featureBackMap.get(featureConjunction);
			
			if (universalDependencies == null) 
				universalDependencies = new HashSet<Feature>();
			
			universalDependencies.add(sourceFeature);
			
			this.featureBackMap.put(featureConjunction, universalDependencies);
		}
		
		
		// We also need a sorted version of the featureBackMap keySet.
		// We need this because when (later on!) translating the POS's of the resultSet back
		// into universal dependencies, we will first try to match the most complex
		// sets of features we know a translation of (=the special cases), and if those 
		// don't match, we will then try to match less complex sets of features (=general cases)
		
		
		for (FeatureConjunction oneConjunction : this.featureBackMap.keySet())
		{
			HashSet<String> releventKeys = new HashSet<String>();
			releventKeys.addAll(oneConjunction.keySet());			
			this.featureBackMap2KeySet.put(oneConjunction, releventKeys);
		}
		
		
		
		// build a list of the FeatureConjunction
		// decreasingly sorted according to complexity
		
		this.sortedFeatureConjunctionsAccording2Complexity.addAll( this.featureBackMap.keySet() );
		
		Collections.sort(this.sortedFeatureConjunctionsAccording2Complexity, new Comparator<FeatureConjunction>() {			
			@Override
		    public int compare(FeatureConjunction o1, FeatureConjunction o2) {
		        return Integer.compare(o2.size(), o1.size());
		    }
		});
		
		
	}
	
	
	// ---------------------------------------------------------------------------------------
	
	// getters and setters etc
	
	
	public String[][] getFieldMapping(){
		return this.fieldMapping;
	}
	public void setFieldMapping(String[][] fieldMapping){
		this.fieldMapping = fieldMapping;
	}
	
	
	public String[][] getFeatureMapping(){
		return this.featureMapping;
	}
	public void setFeatureMapping(String[][] featureMapping){
		this.featureMapping = featureMapping;
	}
	
	
	public String[] getGrammaticalFeatures(){
		return this.grammaticalFeatures;
	}
	public void setGrammaticalFeatures(String[] grammaticalFeatures){
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
	
	
	
	public String getQuote()
	{
		return this.quote;
	}
	public void setQuote(String quote) {
		if (quote == null) quote = "'";
		this.quote = quote;
	}
	
	//
	public boolean hasIncludedFeatureNameInRegex() {
		return this.includeFeatureNameInRegex;
	}
	public void setIncludeFeatureNameInRegex(boolean includeFeatureNameInRegex) {
		this.includeFeatureNameInRegex = includeFeatureNameInRegex;
	}
	
	
	
	public String toString()
	{
		return "Conversion(" + this.name + ")";
	}	

	
	
	
	
	// ---------------------------------------------------------------------------------------
	

	
	/**
	 * Translate a query, meaning: converting 
	 * universal dependencies (cross-linguistically consistent grammatical annotations)
	 * into the set of tags and features instantiated in this class
	 * 
	 * Input is the query part of FSC request, like:
	 * 
	 * 		query= [word="lopen"][pos="NOUN"] 
	 *  
	 * Output is CQP, like:
	 * 
	 * 		cqp="[word="lopen"] [pos="^(N).*" & pos=".*[\(,\|](soort)[,\)\|].*"] 
	 */
	public String translateQuery(String query)
	{
		// convert the query into nodes
		// and translate the tags and features 
		
		QueryProcessor qp = new QueryProcessor(this);
		QueryNode queryAsNode = qp.rewrite(query); // this one will translate the features
		
		
		// convert the nodes into CQP
		
		CqpWriter cqpWriter = new CqpWriter();
		cqpWriter.setQuote(this.getQuote());
		
		if (this.usesFeatureRegex())
		 cqpWriter.setRegexHack(this.getPosTagField(), this.getGrammaticalFeatures(), this.hasIncludedFeatureNameInRegex());
		
		return cqpWriter.writeAsCQP( queryAsNode );
	}
	
	
	/**
	 * Translate a feature put in a given tag set
	 * into another tag set
	 * 
	 * @param feature
	 * @param value
	 * @return
	 */
	public Set<FeatureConjunction> translateFeature(String feature, String value) {
		// TODO Auto-generated method stub
		Feature source = new Feature(feature, value);
		Set<FeatureConjunction> destination = this.featureMap.get(source); 

		if (destination == null)
		{
			FeatureConjunction fc = new FeatureConjunction();
			fc.put(source.getFeatureName(), source.getValues()); // TODO geen pass-through meer als niet gemapt
			destination = new HashSet<>();		
			destination.add(fc);
		}

		return destination;
	}
	
	
	/**
	 * Translate the parts of speech of the result set (in the Kwic objects)
	 * back into universal dependencies
	 * 
	 * @param resultSet
	 * @return modified resultSet
	 */
	public void translateIntoUniversalDependencies(ResultSet resultSet) {
			
		// get all the results (keywords in context) out of the resultSet
		
		List<Kwic> kwics = resultSet.getHits();		
		
		
		// now loop through the results
		
		for (Kwic oneKeywordAndContext : kwics)
		{
			List<String> universalDependencies = new ArrayList<String>();
			
			
			// loop through one results
			
			for (int index = 0; index < oneKeywordAndContext.size(); index++)
			{				
				// gather the properties of the current token that DO have a value
				
				HashSet<String> featureNamesOfCurrentToken = new HashSet<String>();
				
								
				for (String pname : oneKeywordAndContext.getTokenPropertyNames())
				{
					
					String propValue = oneKeywordAndContext.get( pname, index );
					
					if (propValue != null && !propValue.isEmpty())
					{
						featureNamesOfCurrentToken.add(pname);						
					}
				}
				
				
				
				// Special case:
				// In some search engines, the different grammatical features are
				// not specified in separate token properties (t.i. layers)
				// but a concatenated in the pos-tag, eg. like 
				//
				//		NOU-C(gender=n,number=sg)
				//
				// In such cases, we need to extract the features out of the brackets
				// and add each of them to our list of token properties:
				//
				//		pos = NOU-C
				//		gender= n
				//		number= sg
				
				if (featureNamesOfCurrentToken.contains("pos"))
				{
					String posTag = oneKeywordAndContext.get( "pos", index );
					if (posTag.matches("^[A-Z-]+\\(.+\\)$"))
					{
						// pos tag
						
						String realPosTag = 	posTag.replaceAll("^([A-Z-]+)\\((.+)\\)$", "$1");
						
						oneKeywordAndContext.setTokenPropertyAt("pos", realPosTag, index);
						
						// features
						
						String featuresStr = 	posTag.replaceAll("^([A-Z-]+)\\((.+)\\)$", "$2");
						String[] features = 	featuresStr.split(",");
						
						for (String oneFeature : features)
						{
							String featureName;
							String featureValue;

							
							// We normally expect a string like 'featureName = featureValue'
							// but in the case of CGN, we might have feature values only.
							// In that case, we need to add the feature name ourself.
							
							if (oneFeature.split("=").length == 1)
							{
								featureName = CgnFeatureDecoder.getFeatureName(realPosTag, oneFeature);
								featureValue = oneFeature;
							}
							// normal case: 
							// input is a string like 'featureName = featureValue'
							else
							{
								featureName =	oneFeature.split("=")[0];
								featureValue = 	oneFeature.split("=")[1];
							}


							// add the feature to the token properties 
							
							oneKeywordAndContext.addTokenProperty(featureName);
							oneKeywordAndContext.setTokenPropertyAt(featureName, featureValue, index);
							
							featureNamesOfCurrentToken.add(featureName);
							
						}
					}
					
				}
				
				// try to match the features of the current token 
				// with our list of known features translations		
				
				Set<Feature> universalDependencyOfCurrentToken = null;
				
				for (FeatureConjunction oneKnownFeatureConjunction : 
													this.sortedFeatureConjunctionsAccording2Complexity)
				{
					// does the current token contain this known feature conjunction?
					// (the contain relation supports both exact and partial match)
					
					HashSet<String> keysOfThisKnownFeatureConjunction = 
							this.featureBackMap2KeySet.get(oneKnownFeatureConjunction);
					
					
					if (featureNamesOfCurrentToken.containsAll( keysOfThisKnownFeatureConjunction ))
					{
						
						// if so, build a feature conjunction representing the token 
						// with the same set of properties as our know FeatureConjunction
						
						FeatureConjunction featureConjunctionOfToken = new FeatureConjunction();
						for (String onePropertyName : keysOfThisKnownFeatureConjunction)
						{
							String featureValue = oneKeywordAndContext.get( onePropertyName, index );
							if (featureValue != null)
								featureConjunctionOfToken.put(onePropertyName, featureValue);	
						}
						
						// than, try to find this FeatureConjunction in our list of known FeatureConjunctions
						// (t.i. match both feature keys and feature values)
						
						universalDependencyOfCurrentToken = this.featureBackMap.get(featureConjunctionOfToken);
						
						
						// if translation is found, we are done with this token!
						if (universalDependencyOfCurrentToken != null)
							{
							break;						
							}
						
					}
				}
				
				
				
				// now, if we have found a translation for the current token
				// add this to the properties we already have
			
				ArrayList<String> universalDependenciesOfCurrentToken = new ArrayList<String>();
				if (universalDependencyOfCurrentToken != null)
				{					
					for (Feature oneFeature : universalDependencyOfCurrentToken)
					{
						universalDependenciesOfCurrentToken.add( 
								StringUtils.join(oneFeature.getValues(), "|") );
					}
					
				}
								
				universalDependencies.add( StringUtils.join(universalDependenciesOfCurrentToken, " OR ") );
				
				
				
			} // end of loop through tokens
			
			// add new feature for output
			//   (we want the output to contain the original features of the engine
			//   the result comes from, but also one new feature 'universal dependency' 
			//   containing the translation of features of the results)
			
			oneKeywordAndContext.addTokenPropertyName(MappingConstants.UNIVERSAL_DEPENDENCY_FEATURENAME);
			
			oneKeywordAndContext.setTokenProperties(MappingConstants.UNIVERSAL_DEPENDENCY_FEATURENAME, 
					universalDependencies);
			
			
		} // end of loop through results
		
		
		
	}
	
	
	// --------------------------------------------------------------------------
	
	// for test only
	
	public static void main(String[] args)
	{


		ConversionEngine ct = ConversionObjectProcessor.getConversionEngine("UD2CHN");

		String q = "[pos='PROPN'][lemma='aap' & pos='NOUN' & Number='Plur'] [pos='DET'][pos='CCONJ'][lemma='niet.*']";
		q = "[pos=\"NOUN\" & Number=\"Plur\"][pos=\"VERB\"]";
		q = "[pos=\"VERB\" & Tense=\"Past\" & VerbForm=\"Fin\"][pos=\"PROPN\"] within sentence";
		//q = "[pos='AUX' | pos =  'SCONJ'][pos='DET'][]{0,7}[pos='INTJ']";
		// Conversion.bla(q);

        /*
		AttackOfTheClones x = new AttackOfTheClones(ct);

		QueryNode rw = x.rewrite(q);
		String rws = rw.toString();
		System.out.println(rws);
		WriteAsCQP wasqp = new WriteAsCQP();
		wasqp.setQuote("\"");
		wasqp.setRegexHack(ct.posTagField, ct.grammaticalFeatures, ct.includeFeatureNameInRegex);
		*/
		
		System.out.println(ct.translateQuery(q));
	}
}