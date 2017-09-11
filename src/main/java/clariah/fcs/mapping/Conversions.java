package clariah.fcs.mapping;

/**
 * 
 * @author jesse
 * Even houtjetouwtje in code. TODO zet in XML bestandje (Wat eigenlijk weer minder leesbaar is :))
 * 
 * 
 * Kijk ook naar https://raw.githubusercontent.com/proycon/folia/master/setdefinitions/frog-mbpos-cgn voor de feature namen in cgn tags
 */

public class Conversions 
{
	public static  ConversionTable UD2CHN;

	public static ConversionTable UD2BaB;

	public static ConversionTable UD2CGNSonar; 
 
	public static ConversionTable UD2CGNNederlab;
	
	static
	{
		String[][] fieldMapping0  = {{"xxword", "t_lc"}}; // not implemented yet

		String[][] featureMapping0 = 
			{
					{"pos","ADJ", "pos", "AA"},
					{"pos","ADJ", "pos", "ADJ"},
					
					{"pos","ADV", "pos", "ADV"},
					// {"pos","ADV", "pos", "AA", "position", "oth|pred"},
					
					{"pos", "INTJ", "pos", "INT"},

					{"pos", "NOUN", "pos", "NOU"},
					{"pos", "PROPN", "pos", "NEPER|NELOC|NEOTHER|NEORG"},
					
					{"pos", "VERB", "pos", "VRB"},

					{"pos", "ADP",  "pos", "ADP"},
					{"pos", "AUX", "pos", "VRB", "lemma", "zijn|hebben|willen|kunnen|mogen"},  // HM
				
					{"pos", "DET", "pos", "ART"}, // HM; alleen bij historische corpora
					{"pos", "DET", "pos", "PRN"}, //
					
					{"pos", "PRON", "pos", "PRN"} , // HM, zo krijg je ook de determiners
					
					
					{"pos", "NUM", "pos", "NUM"},
					
					{"pos", "CCONJ", "pos", "CON"}, // HM
					{"pos", "SCONJ", "pos", "CON"}, // HM
					{"pos", "PUNCT", "pos", "RES"}, // HM hebben we niet
					{"pos", "SYM", "pos", "RES"},
					{"pos", "X", "pos", "RES"},
			};
		
		String[] grammarFeats = {"number", "tense", "mood", "type", "person", "gender", "subtype", "finiteness", "position", "degree", "case"};

		ConversionTable ct0 = new ConversionTable(fieldMapping0, featureMapping0);


		ct0.useFeatureRegex = true;
		ct0.posTagField = "pos";
		ct0.grammaticalFeatures = grammarFeats;
		ct0.name = "UD2BaB";
		UD2BaB = ct0;
	}
	static
	{
		String[][] fieldMapping  = {{"xxword", "t_lc"}}; // not implemented yet

		String[][] featureMapping = 
			{
					{"pos","ADJ", "pos", "AA"},
					{"pos","ADJ", "pos", "ADJ"},
					
					{"pos","ADV", "pos", "ADV"},
					{"pos","ADV", "pos", "AA", "position", "oth|pred"},
					
					{"pos", "INTJ", "pos", "INT"},

					{"pos", "NOUN", "pos", "NOU-C"},
					{"pos", "PROPN", "pos", "NOU-P"},
					
					{"pos", "VERB", "pos", "VRB"},

					{"pos", "ADP",  "pos", "ADP"},
					{"pos", "AUX", "pos", "VRB"},  // HM
				
					{"pos", "DET", "pos", "ART"}, // HM; alleen bij historische corpora
					{"pos", "DET", "pos", "PD", "position", "prenom"}, //
					{"pos", "PRON", "pos", "PD", "position", "pron"}, // HM, zo krijg je ook de determiners
					
					
					{"pos", "NUM", "pos", "NUM"},
					
					{"pos", "CCONJ", "pos", "CONJ", "type", "coor"}, // HM
					{"pos", "SCONJ", "pos", "CONJ", "type", "sub"}, // HM
					{"pos", "PUNCT", "pos", "RES"}, // HM hebben we niet
					{"pos", "SYM", "pos", "RES"},
					{"pos", "X", "pos", "RES"},

					
					// nominal features
					
					{"Number", "Plur", "number", "pl"},
					{"Number", "Sing", "number", "sg"},
					{"Gender", "Fem", "gender", "f"},
					{"Gender", "Masc", "gender", "m"},
					{"Gender", "Neut", "gender", "n"},
					{"Gender",  "Com", "gender", "f", "gender", "m"},  // HM, not implemented in regex or otherwise

					// adjective (formal=infl-e)? Hoe doe je dat in UD??)
					
					{"Degree", "Pos", "degree", "pos"},
					{"Degree", "Cmp", "degree", "comp"},
					{"Degree", "Sup", "degree", "sup"},
					
					{"Position", "Postnom", "position", "postnom"},
					{"Position", "Prenom", "position", "prenom"},
					{"Position", "Free", "position", "oth|pred"},
					{"Position", "Nom", "position", "oth|pred"},
					{"Position", "Nom", "position", "pron"},
					
					{"Case", "Gen", "case", "gen"}, 
					
					// {"Definiteness", "Def", "formal", "infl-e"}, ?? For instance german adjectives in UD?
					// numeral 
					
			
					{"NumType", "Card", "type", "card"},
					{"NumType", "Ord", "type", "ord"},
					
					// verbal features
					
					{"Mood", "Ind", "finiteness", "fin"},
					{"Mood", "Imp", "finiteness", "fin"},
					{"Mood", "Sub", "finiteness", "fin"},
					
					{"VerbForm", "Fin", "finiteness", "fin"},
					{"VerbForm", "Inf", "finiteness", "inf"},
					{"VerbForm", "Inf", "finiteness", "ger|inf"},
					{"VerbForm", "Part", "finiteness", "part"},
					
					{"Person", "1", "person", "1"},
					{"Person", "2", "person", "2"},
					{"Person", "3", "person", "3"},
					
					{"Tense", "Past", "tense", "past"},
					{"Tense", "Pres", "tense", "pres"},
					
					// pronoun / determiner / article
					// UD heeft: Art (ldiwoord)	Dem	(aanwijzend) Emp (nadruk)	Exc (uitroepend)	Ind	(onbepaald) Int	Neg	Prs (persoonlijk)	Rcp (reciprocal)	Rel (betrekkelijk)	Tot (collectief: iedereen enzo)

					{"PronType", "Art", "subtype", "art-def"},
					{"PronType", "Art", "subtype", "art-indef"},
					
					{"PronType", "Dem", "type", "dem"},
					{"PronType", "Dem", "type", "d-p"},
					{"PronType", "Prs", "type", "pers"},
					{"PronType", "Rel", "type", "rel"},
					{"PronType", "Rel", "type", "w-p"}, // is dat zo?
					{"PronType", "Rel", "type", "d-p"},
					{"PronType", "Rcp", "type", "recip"},
					{"PronType", "Ind", "type", "indef"},
					{"PronType", "Int", "type", "w-p"},
					{"PronType", "Tot", "type", "indef", "lemma", "iedereen|ieder|al|alles"},
					{"Poss", "Yes", "type", "poss"},
					{"Reflex", "Yes", "type", "refl"},
					 // hoe zit het nou ook alweer precies met de w-p's en d-p's. Bleuh... 
					
			};

		String[] grammarFeats = {"number", "tense", "mood", "type", "person", "gender", "subtype", "finiteness", "position", "degree", "case"};

		ConversionTable ct = new ConversionTable(fieldMapping, featureMapping);


		ct.useFeatureRegex = true;
		ct.posTagField = "pos";
		ct.grammaticalFeatures = grammarFeats;
		ct.name = "UD2CHN";
		UD2CHN = ct;
	}



	static
	{
		String[][] fieldMapping1  = {{"xxword", "t"}};

		String[][] featureMapping1 = 
			{
					{"pos","ADJ", "pos", "ADJ"},
					{"pos","ADV", "pos", "BW"},
					{"pos", "INTJ", "pos", "TSW"},
					
					{"pos", "NOUN", "pos", "N", "feat.ntype", "soort"},
					{"pos", "PROPN", "pos", "N", "feat.ntype", "eigen"}, // spec(deeleigen nooit te vinden zo....
					{"pos", "PROPN", "pos", "SPEC", "feat.spectype", "deeleigen"},
					
					{"pos", "VERB", "pos", "WW"},

					{"pos", "ADP",  "pos", "VZ"},
					{"pos", "AUX", "pos", "WW"},  // HM
					
					{"pos", "DET", "pos", "LID"}, // HM
					
					{"pos", "DET", "pos", "VNW", "feat.pdtype", "det"}, // HM
					{"pos", "PRON", "pos", "VNW", "feat.pdtype", "pron"}, // HM
					
					
					{"pos", "NUM", "pos", "TW"},
					// {"pos", "PART", "", ""}, // HM
					
					
					
					{"pos", "CCONJ", "pos", "VG", "feat.conjtype", "neven"}, // HM
					{"pos", "SCONJ", "pos", "VG", "feat.conjtype", "onder"}, // HM
					
					{"pos", "PUNCT", "pos", "LET"}, // HM 
					{"pos", "SYM", "pos", "SPEC"}, // opzoeken
					{"pos", "X", "pos", "SPEC"},
					
					// nominal features
					
					{"Number", "Plur", "feat.getal", "mv"},
					{"Number", "Sing", "feat.getal", "ev"},
					
					{"Gender", "Fem", "feat.genus", "zijd"},
					{"Gender", "Masc", "feat.genus", "zijd"},
					{"Gender", "Neut", "feat.genus", "onz"},
					{"Gender",  "Com", "feat.genus", "zijd"},  // HM, not implemented
					
				   // adjective
					
					{"Degree", "Pos", "feat.graad", "basis"},
					{"Degree", "Cmp", "feat.graad", "comp"},
					{"Degree", "Sup", "feat.graad", "sup"},
					
					{"Position", "Nom", "feat.positie", "nom"},
					{"Position", "Postnom", "feat.positie", "postnom"},
					{"Position", "Prenom", "feat.positie", "prenom"},
					{"Position", "Free", "feat.positie", "vrij"},
				    // 
					{"??", "??", "buiging", "met-e"}, // en wat doen we het de -s? (Ook in CHN tagging een probleem)
					{"Case", "Gen", "buiging", "met-s"}, 
					// numeral 
					
					{"NumType", "Card", "feat.numtype", "card"},
					{"NumType", "Ord", "feat.numtype", "ord"},
					
					// verbal features
					
					{"Mood", "Ind", "feat.wvorm", "pv"},
					{"Mood", "Imp", "feat.wvorm", "pv"},
					{"Mood", "Sub", "feat.wvorm", "pv", "feat", "conj"},
					
					{"VerbForm", "Fin", "feat.wvorm", "pv"},
					{"VerbForm", "Inf", "feat.wvorm", "inf"},
					{"VerbForm", "Part", "feat.wvorm", "od"},
					{"VerbForm", "Part", "feat.wvorm", "vd"},
					
					{"Person", "1", "feat.persoon", "1"},
					//{"Person", "1", "person", "1"},
					//{"Person", "2", "person", "2"},
					//{"Person", "3", "feat.pvagr", "met-t"},
					
					{"Tense", "Past", "feat.pvtijd", "verl"}, // Maar: NIET als het een deelwoord is Dus deze manier van converteren werkt niet; je hebt ook nog condities nodig
					{"Tense", "Pres", "feat.pvtijd", "tgw"},
					{"Tense", "Past", "feat.wvorm", "vd"},  // eigenlijk niet goed? in UD geen past maar perf?
					{"Tense", "Pres", "feat.wvorm", "od"},
					
					// pronoun / determiner / article
					// UD heeft: Art (ldiwoord)	Dem	(aanwijzend) Emp (nadruk)	Exc (uitroepend)	Ind	(onbepaald) Int	Neg	Prs (persoonlijk)	Rcp (reciprocal)	Rel (betrekkelijk)	Tot (collectief: iedereen enzo)

					{"PronType", "Art", "pos", "LID"},
				
					
					{"PronType", "Exc", "feat.vwtype", "excl"}, // misnomer: zou vnwtype moeten zijn?
					{"PronType", "Dem", "feat.vwtype", "aanw"}, // in welk feat zit PronType????
					{"PronType", "Prs", "feat.vwtype", "pers"},
					{"PronType", "Rel", "feat.vwtype", "betr"},
					{"PronType", "Int", "feat.vwtype", "vrag"}, // wanneer precies vb? Alleen bij wie/wat enz?
					{"PronType", "Rel", "feat.vwtype", "betr"},
					
					{"PronType", "Rcp", "feat.vwtype", "recip"},
					{"PronType", "Ind", "feat.lwtype", "onbep"},
				
					{"PronType", "Tot", "feat.lwtype", "onbep", "lemma", "iedereen|ieder|al|alles|elk|elke"},
					{"Poss", "Yes", "feat.vwtype", "bez"},
					{"Reflex", "Yes", "feat.vwtype", "refl"},
			};

		String[] grammarFeats1 = {
				  "feat",
				  "feat.buiging",
		          "feat.conjtype",
		          "feat.dial",
		          "feat.genus",
		          "feat.getal",
		          "feat.getal-n",
		          "feat.graad",
		          "feat.lwtype",
		          "feat.naamval",
		          "feat.npagr",
		          "feat.ntype",
		          "feat.numtype",
		          "feat.pdtype",
		          "feat.persoon",
		          "feat.positie",
		          "feat.pvagr",
		          "feat.pvtijd",
		          "feat.spectype",
		          "feat.status",
		          "feat.vwtype",
		          "feat.vztype",
		          "feat.wvorm",
		};

		ConversionTable ct1 = new ConversionTable(fieldMapping1, featureMapping1);
		
		ct1.quote = "\"";
		ct1.useFeatureRegex = true;
		ct1.includeFeatureNameInRegex = false;
		ct1.posTagField = "pos";
		ct1.grammaticalFeatures = grammarFeats1;
		ct1.name = "UD2CGNSonar";
		UD2CGNSonar = ct1;
	}
	
	static
	{
		ConversionTable ct2 = new ConversionTable(UD2CGNSonar.fieldMapping, UD2CGNSonar.featureMapping);
		ct2.useFeatureRegex = false;
		ct2.posTagField = "pos";
		ct2.name = "UD2CGNNederlab";
		UD2CGNNederlab = ct2;
	}
}

/*
{
	  "status": "ok",
	  "mtas": {
	    "prefix": [
	      {
	        "key": "list of prefixes NLContent_mtasSource",
	        "singlePosition": [
	          "t",
	          "t.original",
	          "t.suggestion",
	          "t_lc",
	          "t_lc.original",
	          "t_lc.suggestion"
	        ],
	        "multiplePosition": [
	          "p"
	        ],
	        "setPosition": [],
	        "intersecting": [
	          "t.suggestion",
	          "t_lc.suggestion"
	        ]
	      },
	      {
	        "key": "list of prefixes NLContent_mtas",
	        "singlePosition": [
	          "feat.buiging",
	          "feat.conjtype",
	          "feat.dial",
	          "feat.genus",
	          "feat.getal",
	          "feat.getal-n",
	          "feat.graad",
	          "feat.lwtype",
	          "feat.naamval",
	          "feat.npagr",
	          "feat.ntype",
	          "feat.numtype",
	          "feat.pdtype",
	          "feat.persoon",
	          "feat.positie",
	          "feat.pvagr",
	          "feat.pvtijd",
	          "feat.spectype",
	          "feat.status",
	          "feat.vwtype",
	          "feat.vztype",
	          "feat.wvorm",
	          "lemma",
	          "pos",
	          "t",
	          "t_lc",
	          "w"
	        ],
	        "multiplePosition": [
	          "entity",
	          "p",
	          "s"
	        ],
	        "setPosition": [],
	        "intersecting": []
	      }
	    ]
	  }
	}

*/