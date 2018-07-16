package org.ivdnt.fcs.mapping;

import java.lang.invoke.MethodHandles;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
 * The static class is to be used to get the CGN feature name
 * corresponding to a given CGN pos tag and CGN feature value combination
 * 
 * So posTag='N' and featureValue='mv' should give featureName='getal'
 */
public class CgnFeatureDecoder {

	// logger
	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	/**
	 * Give the CGN feature name corresponding to a given CGN pos tag and feature
	 * value
	 * 
	 * @param CGN
	 *            pos tag
	 * @param CGN
	 *            pdtype feature, used to disambiguate some values for other
	 *            features
	 * @param Map
	 *            of possible values for CGN features
	 * @param Map
	 *            of possible CGN features per CGN POS tag
	 * @param CGN
	 *            feature value
	 * @return CGN feature name
	 */
	public static String getFeatureName(String posTag, String pdtype,
			ConcurrentHashMap<String, HashSet<String>> featureName2FeatureValues,
			ConcurrentHashMap<String, HashSet<String>> posTag2FeatureNames, String featureValue) {

		// Convert the data into the needed format:
		// we have a map from feature names to feature values:
		//
		// featureName1 -> [featureValue1, featureValue2, ...]
		// featureName2 -> [featureValue2, featureValue3, ...]
		//
		// but we need a mirror map too:
		//
		// featureValue1 -> [featureName1, ...]
		// featureValue2 -> [featureName1, featureName2, ...]
		// featureValue3 -> [featureName2, ...]
		//
		// (the reason why we need this become clear in the following comments)

		ConcurrentHashMap<String, HashSet<String>> featureValue2FeatureNames = new ConcurrentHashMap<String, HashSet<String>>();
		for (String oneFeatureName : featureName2FeatureValues.keySet()) {
			// first get the [feature values] attached to this [feature name]
			HashSet<String> featureValues = featureName2FeatureValues.get(oneFeatureName);

			// now each of these [feature values] must become a key in the hash we are
			// building

			for (String oneFeatureValue : featureValues) {
				// if we already added this [feature value] as a key in a previous round of the
				// current
				// loop, then get the set of [feature names] we already attached to it: we will
				// extend
				// this set
				HashSet<String> featureNamesWeAlreadyHave = featureValue2FeatureNames.get(oneFeatureValue);
				if (featureNamesWeAlreadyHave == null)
					featureNamesWeAlreadyHave = new HashSet<String>();

				// now add the [feature name] to the set of [feature names] already attached to
				// this value
				featureNamesWeAlreadyHave.add(oneFeatureName);
				featureValue2FeatureNames.put(oneFeatureValue, featureNamesWeAlreadyHave);

			}
		}

		// After the first call of this function, processing will begin from here

		// getting the right feature name, given a pos tag and a feature value,
		// is a question of getting the intersection between:
		// - the set of [feature names] associated to the given pos tag
		// - the set of [feature names] associated to the given feature value
		//
		// So, feature value "onbep" will give feature names {"lwtype", "vwtype"}
		// and pos tag "LID" will give feature names {"lwtype", "naamval", "npagr"}.
		// The intersection {"lwtype"} is the result we need.
		Set<String> intersection;
		if (featureValue2FeatureNames.containsKey(featureValue)) {
			intersection = new HashSet<String>(featureValue2FeatureNames.get(featureValue));
		} else {
			logger.error("CGN feature value not available in CGN feature-value map: " + featureValue);
			intersection = new HashSet<String>();

		}
		intersection.retainAll(posTag2FeatureNames.get(posTag));

		// If there are multiple options available, disambiguate using pdtype
		// (Actually, we should have a more complex disambiguating system, taking into
		// account
		// all other features to determine a feature:
		// http://urd.let.rug.nl/vannoord/Lassy/POS_manual.pdf #4.2)
		if (intersection.size() > 1) {
			List<String> npagrGetal = Arrays.asList("npagr", "getal");
			List<String> npagrGetalFeat = Arrays.asList("feat.npagr", "feat.getal");
			if (intersection.containsAll(npagrGetal) || intersection.containsAll(npagrGetalFeat)) {
				if (pdtype.equals("pron")) {
					intersection.clear();
					intersection.add("getal");
				} else {
					// Actually, when pdtype==det and positie=prenom, pick npagr.
					// But we do it in all other cases, which is good enough to disambiguate.
					intersection.clear();
					intersection.add("npagr");
				}
			} else {
				throw new NullPointerException("Cannot infer CGN feature from value. Feature value " + featureValue
						+ " has multiple possible features: " + intersection);
			}
		}

		// Normally, the result should contain one feature. If there is none, return
		// empty string
		Iterator<String> it = intersection.iterator();
		String returnFeature = "";
		if (it.hasNext()) {
			returnFeature = it.next();
		}
		return returnFeature;
		// return StringUtils.join(intersection, "|");
	}

	// --------------------------------------------------------------------------

	// for test only

	public static void main(String[] args) {

		System.out.println("Expected result: lwtype");
		System.out.println(getFeatureName("LID", "", CgnMaps.featureName2FeatureValuesNederlab,
				CgnMaps.posTag2FeatureNamesNederlab, "onbep"));

		System.out.println("Expected result: vwtype");
		System.out.println(getFeatureName("VNW", "", CgnMaps.featureName2FeatureValuesSonar,
				CgnMaps.posTag2FeatureNamesSonar, "onbep"));

	}

}
