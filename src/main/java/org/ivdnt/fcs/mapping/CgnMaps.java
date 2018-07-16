package org.ivdnt.fcs.mapping;

import java.util.Arrays;
import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;

public class CgnMaps {
	public static ConcurrentHashMap<String, HashSet<String>> featureName2FeatureValuesSonar = new ConcurrentHashMap<String, HashSet<String>>() {
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

	public static ConcurrentHashMap<String, HashSet<String>> posTag2FeatureNamesSonar = new ConcurrentHashMap<String, HashSet<String>>() {
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

	public static ConcurrentHashMap<String, HashSet<String>> featureName2FeatureValuesNederlab = new ConcurrentHashMap<String, HashSet<String>>() {
		/**
		 * 
		 */
		private static final long serialVersionUID = -2253004472326058497L;

		{
			put("feat.buiging", new HashSet<String>(Arrays.asList("met-e", "met-s", "zonder")));
			put("feat.getal-n", new HashSet<String>(Arrays.asList("mv-n", "zonder-n")));
			put("feat.lwtype", new HashSet<String>(Arrays.asList("bep", "onbep")));
			put("feat.conjtype", new HashSet<String>(Arrays.asList("neven", "onder")));
			put("feat.ntype", new HashSet<String>(Arrays.asList("eigen", "soort")));
			put("feat.numtype", new HashSet<String>(Arrays.asList("hoofd", "rang")));
			put("feat.getal", new HashSet<String>(Arrays.asList("ev", "mv", "getal")));
			put("feat.getal-n", new HashSet<String>(Arrays.asList("getal-n", "mv-n", "ev-n", "zonder-n")));
			put("feat.pvagr", new HashSet<String>(Arrays.asList("met-t", "ev", "mv")));
			put("feat.pvtijd", new HashSet<String>(Arrays.asList("tgw", "verl", "conj", "imp")));
			put("feat.status", new HashSet<String>(Arrays.asList("nadr", "red", "vol")));
			put("feat.vztype", new HashSet<String>(Arrays.asList("init", "fin", "versm")));
			put("feat.graad", new HashSet<String>(Arrays.asList("basis", "comp", "dim", "sup")));
			put("feat.pdtype", new HashSet<String>(Arrays.asList("adv-pron", "det", "grad", "pron")));
			put("feat.positie", new HashSet<String>(Arrays.asList("nom", "postnom", "prenom", "vrij")));
			put("feat.genus", new HashSet<String>(Arrays.asList("fem", "genus", "masc", "onz", "zijd")));
			put("feat.naamval", new HashSet<String>(Arrays.asList("bijz", "dat", "gen", "nomin", "obl", "stan")));
			put("feat.persoon",
					new HashSet<String>(Arrays.asList("1", "2", "2b", "2v", "3", "3m", "3o", "3p", "3v", "persoon")));
			put("feat.npagr", new HashSet<String>(
					Arrays.asList("agr", "agr3", "evf", "evmo", "evon", "evz", "mv", "rest", "rest3")));
			put("feat.wvorm", new HashSet<String>(Arrays.asList("inf", "od", "pv", "vd")));
			put("feat.vwtype", new HashSet<String>(Arrays.asList("refl", "aanw", "betr", "bez", "excl", "onbep", "pers",
					"pr", "recip", "vb", "vrag")));
			put("feat.spectype", new HashSet<String>(
					Arrays.asList("deeleigen", "vreemd", "afk", "afgebr", "symb", "meta", "onverst")));
			put("feat.variatie", new HashSet<String>(Arrays.asList("dial")));
			put("feat.type", new HashSet<String>(Arrays.asList("sym")));
		}
	};

	public static ConcurrentHashMap<String, HashSet<String>> posTag2FeatureNamesNederlab = new ConcurrentHashMap<String, HashSet<String>>() {
		/**
		 * 
		 */
		private static final long serialVersionUID = -6260445263371236442L;

		{
			put("N", new HashSet<String>(
					Arrays.asList("feat.ntype", "feat.getal", "feat.graad", "feat.genus", "feat.naamval")));
			put("ADJ", new HashSet<String>(
					Arrays.asList("feat.positie", "feat.graad", "feat.buiging", "feat.getal-n", "feat.naamval")));
			put("WW", new HashSet<String>(Arrays.asList("feat.wvorm", "feat.pvtijd", "feat.pvagr", "feat.positie",
					"feat.buiging", "feat.getal-n")));
			put("NUM", new HashSet<String>(
					Arrays.asList("feat.numtype", "feat.positie", "feat.graad", "feat.getal-n", "feat.naamval")));
			put("VNW",
					new HashSet<String>(Arrays.asList("feat.vwtype", "feat.pdtype", "feat.naamval", "feat.status",
							"feat.persoon", "feat.getal", "feat.genus", "feat.positie", "feat.buiging", "feat.npagr",
							"feat.getal-n", "feat.graad")));
			put("LID", new HashSet<String>(Arrays.asList("feat.lwtype", "feat.naamval", "feat.npagr")));
			put("VZ", new HashSet<String>(Arrays.asList("feat.vztype")));
			put("VG", new HashSet<String>(Arrays.asList("feat.conjtype")));
			put("BW", new HashSet<String>());
			put("TSW", new HashSet<String>());
			put("TW", new HashSet<String>(Arrays.asList("feat.numtype", "feat.graad", "feat.positie")));
			put("SPEC", new HashSet<String>(Arrays.asList("feat.spectype")));
			put("RES", new HashSet<String>(Arrays.asList("feat.type")));
		}
	};
}
