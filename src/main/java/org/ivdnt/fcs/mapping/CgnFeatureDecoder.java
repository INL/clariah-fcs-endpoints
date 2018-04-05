package org.ivdnt.fcs.mapping;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.ivdnt.util.StringUtils;

/*
 * The static class is to be used to get the CGN feature name
 * corresponding to a given CGN pos tag and CGN feature value combination
 * 
 * So posTag='N' and featureValue='mv' should give featureName='getal'
 */
public class CgnFeatureDecoder {

	private static ConcurrentHashMap<String, HashSet<String>> featureName2FeatureValues = new ConcurrentHashMap<String, HashSet<String>>() {
		/**
		 * 
		 */
		private static final long serialVersionUID = -2253004472326058497L;

		{
			put("buiging", new HashSet<String>(Arrays.asList("met-e", "met-s", "zonder")));
			put("getal-n", new HashSet<String>(Arrays.asList("mv-n", "zonder-n")));
			put("lwtype", new HashSet<String>(Arrays.asList("bep", "onbep")));
			put("conjtype", new HashSet<String>(Arrays.asList("neven", "onder")));
			put("ntype", new HashSet<String>(Arrays.asList("eigen", "soort")));
			put("numtype", new HashSet<String>(Arrays.asList("hoofd", "rang")));
			put("getal", new HashSet<String>(Arrays.asList("ev", "mv", "getal")));
			put("getal-n", new HashSet<String>(Arrays.asList("getal-n", "mv-n", "ev-n", "zonder-n")));
			put("pvagr", new HashSet<String>(Arrays.asList("met-t", "ev", "mv")));
			put("pvtijd", new HashSet<String>(Arrays.asList("tgw", "verl", "conj", "imp")));
			put("status", new HashSet<String>(Arrays.asList("nadr", "red", "vol")));
			put("vztype", new HashSet<String>(Arrays.asList("init", "fin", "versm")));
			put("graad", new HashSet<String>(Arrays.asList("basis", "comp", "dim", "sup")));
			put("pdtype", new HashSet<String>(Arrays.asList("adv-pron", "det", "grad", "pron")));
			put("positie", new HashSet<String>(Arrays.asList("nom", "postnom", "prenom", "vrij")));
			put("genus", new HashSet<String>(Arrays.asList("fem", "genus", "masc", "onz", "zijd")));
			put("naamval", new HashSet<String>(Arrays.asList("bijz", "dat", "gen", "nomin", "obl", "stan")));
			put("persoon",
					new HashSet<String>(Arrays.asList("1", "2", "2b", "2v", "3", "3m", "3o", "3p", "3v", "persoon")));
			put("npagr", new HashSet<String>(
					Arrays.asList("agr", "agr3", "evf", "evmo", "evon", "evz", "mv", "rest", "rest3")));
			put("wvorm", new HashSet<String>(Arrays.asList("inf", "od", "pv", "vd")));
			put("vwtype", new HashSet<String>(Arrays.asList("refl", "aanw", "betr", "bez", "excl", "onbep", "pers",
					"pr", "recip", "vb", "vrag")));
			put("spectype", new HashSet<String>(
					Arrays.asList("deeleigen", "vreemd", "afk", "afgebr", "symb", "meta", "onverst")));
			put("variatie", new HashSet<String>(Arrays.asList("dial")));
			put("type", new HashSet<String>(Arrays.asList("sym")));
		}
	};

	private static ConcurrentHashMap<String, HashSet<String>> postag2FeatureNames = new ConcurrentHashMap<String, HashSet<String>>() {
		/**
		 * 
		 */
		private static final long serialVersionUID = -6260445263371236442L;

		{
			put("N", new HashSet<String>(Arrays.asList("ntype", "getal", "graad", "genus", "naamval")));
			put("ADJ", new HashSet<String>(Arrays.asList("positie", "graad", "buiging", "getal-n", "naamval")));
			put("WW", new HashSet<String>(Arrays.asList("wvorm", "pvtijd", "pvagr", "positie", "buiging", "getal-n")));
			put("NUM", new HashSet<String>(Arrays.asList("numtype", "positie", "graad", "getal-n", "naamval")));
			put("VNW", new HashSet<String>(Arrays.asList("vwtype", "pdtype", "naamval", "status", "persoon", "getal",
					"genus", "positie", "buiging", "npagr", "getal-n", "graad")));
			put("LID", new HashSet<String>(Arrays.asList("lwtype", "naamval", "npagr")));
			put("VZ", new HashSet<String>(Arrays.asList("vztype")));
			put("VG", new HashSet<String>(Arrays.asList("conjtype")));
			put("BW", new HashSet<String>());
			put("TSW", new HashSet<String>());
			put("TW", new HashSet<String>(Arrays.asList("numtype", "graad", "positie")));
			put("SPEC", new HashSet<String>(Arrays.asList("spectype")));
			put("RES", new HashSet<String>(Arrays.asList("type")));
		}
	};

	private static ConcurrentHashMap<String, HashSet<String>> featureValue2FeatureNames = new ConcurrentHashMap<String, HashSet<String>>();

	/**
	 * Give the CGN feature name corresponding to a given CGN pos tag and feature
	 * value
	 * 
	 * @param CGN
	 *            pos tag
	 * @param CGN
	 *            feature value
	 * @return CGN feature name
	 */
	public static String getFeatureName(String posTag, String featureValue) {

		// At the very first call, convert the data into the needed format:
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

		if (featureValue2FeatureNames.size() == 0) {
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

		Set<String> intersection = new HashSet<String>(featureValue2FeatureNames.get(featureValue));
		intersection.retainAll(postag2FeatureNames.get(posTag));

		// normally, the result should contain only one feature name (no join)

		return StringUtils.join(intersection, "|");
	}

	// --------------------------------------------------------------------------

	// for test only

	public static void main(String[] args) {

		System.out.println("Expected result: lwtype");
		System.out.println(getFeatureName("LID", "onbep"));

		System.out.println("Expected result: vwtype");
		System.out.println(getFeatureName("VNW", "onbep"));

	}

}
