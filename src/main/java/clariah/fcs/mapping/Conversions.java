package clariah.fcs.mapping;

/**
 * 
 * @author jesse
 * Even houtjetouwtje in code. TODO zet in XML bestandje (Wat eigenlijk weer minder leesbaar is :))
 */
public class Conversions 
{
	static  ConversionTable UD2CHN;

	static ConversionTable UD2BaB;

	static ConversionTable UD2CGNSonar; 
 
	static ConversionTable UD2CGNNederlab;
	
	static
	{
		String[][] fieldMapping  = {{"xxword", "t"}};

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
					{"Gender",  "Com", "gender", "f", "gender", "m"},  // HM, not implemented

					// adjective (infl-e? Hoe doe je dat in UD??)
					
					{"Degree", "Pos", "degree", "pos"},
					{"Degree", "Cmp", "degree", "comp"},
					{"Degree", "Sup", "degree", "sup"},
					
					{"Position", "Postnom", "position", "postnom"},
					{"Position", "Prenom", "position", "prenom"},
					{"Position", "Free", "position", "oth|pred"},
					{"Position", "Nom", "position", "oth|pred"},
					{"Position", "Nom", "position", "pron"},
					// numeral 
					
					{"NumType", "Card", "type", "card"},
					{"NumType", "Ord", "type", "ord"},
					
					// verbal features
					
					{"Mood", "Ind", "finiteness", "fin"},
					{"Mood", "Imp", "finiteness", "fin"},
					{"Mood", "Sub", "finiteness", "fin"},
					
					{"VerbForm", "Fin", "finiteness", "fin"},
					{"VerbForm", "Inf", "finiteness", "inf"},
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
					{"PronType", "Prs", "type", "pers"},
					{"PronType", "Rel", "type", "rel"},
					{"PronType", "Rcp", "type", "recip"},
					
					{"Poss", "Yes", "type", "poss"},
					{"Reflex", "Yes", "type", "refl"},
					{"PronType", "Int", "type", "w-p"}, // hoe zit het nou ook alweer precies met de w-p's en d-p's. Bleuh... 
					
			};

		String[] grammarFeats = {"number", "tense", "mood", "type", "person", "gender"};

		ConversionTable ct = new ConversionTable(fieldMapping, featureMapping);


		ct.useFeatureRegex = true;
		ct.posTagField = "pos";
		ct.grammaticalFeatures = grammarFeats;

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
					
					{"pos", "NOUN", "pos", "N", "feat", "soort"},
					{"pos", "PROPN", "pos", "N", "feat", "eigen"}, // spec(deeleigen nooit te vinden zo....
					{"pos", "PROPN", "pos", "SPEC", "feat", "deeleigen"},
					
					{"pos", "VERB", "pos", "WW"},

					{"pos", "ADP",  "pos", "VZ"},
					{"pos", "AUX", "pos", "WW"},  // HM
					
					{"pos", "DET", "pos", "LID"}, // HM
					
					{"pos", "DET", "pos", "VNW", "feat", "det"}, // HM
					{"pos", "PRON", "pos", "VNW", "feat", "pron"}, // HM
					
					
					{"pos", "NUM", "pos", "TW"},
					// {"pos", "PART", "", ""}, // HM
					
					
					
					{"pos", "CCONJ", "pos", "VG", "feat", "neven"}, // HM
					{"pos", "SCONJ", "pos", "VG", "feat", "onder"}, // HM
					
					{"pos", "PUNCT", "pos", "LET"}, // HM 
					{"pos", "SYM", "pos", "SPEC"}, // opzoeken
					{"pos", "X", "pos", "SPEC"},
					
					// nominal features
					
					{"Number", "Plur", "feat", "mv"},
					{"Number", "Sing", "feat", "ev"},
					
					{"Gender", "Fem", "feat", "zijd"},
					{"Gender", "Masc", "feat", "zijd"},
					{"Gender", "Neut", "feat", "onz"},
					{"Gender",  "Com", "feat", "zijd"},  // HM, not implemented
					
				   // adjective
					
					{"Degree", "Pos", "feat", "basis"},
					{"Degree", "Cmp", "feat", "comp"},
					{"Degree", "Sup", "feat", "sup"},
					
					{"Position", "Nom", "feat", "nom"},
					{"Position", "Postnom", "feat", "postnom"},
					{"Position", "Prenom", "feat", "prenom"},
					{"Position", "Free", "feat", "vrij"},
				
					// numeral 
					
					{"NumType", "Card", "feat", "card"},
					{"NumType", "Ord", "feat", "ord"},
					
					// verbal features
					
					{"Mood", "Ind", "feat", "pv"},
					{"Mood", "Imp", "feat", "pv"},
					{"Mood", "Sub", "feat", "pv", "feat", "conj"},
					
					{"VerbForm", "Fin", "feat", "pv"},
					{"VerbForm", "Inf", "feat", "inf"},
					{"VerbForm", "Part", "feat", "od"},
					{"VerbForm", "Part", "feat", "vd"},
					
					//{"Person", "1", "person", "1"},
					//{"Person", "2", "person", "2"},
					{"Person", "3", "feat", "met-t"},
					
					{"Tense", "Past", "feat", "verl"}, // Maar: NIET als het een deelwoord is Dus deze manier van converteren werkt niet; je hebt ook nog condities nodig
					{"Tense", "Pres", "feat", "tgw"},
					{"Tense", "Past", "feat", "vd"},  // eigenlijk niet goed? in UD geen past maar perf?
					{"Tense", "Pres", "feat", "od"},
			};

		String[] grammarFeats1 = {"number", "tense", "mood", "type", "feat"};

		ConversionTable ct1 = new ConversionTable(fieldMapping1, featureMapping1);

		ct1.useFeatureRegex = true;
		ct1.includeFeatureNameInRegex = false;
		ct1.posTagField = "pos";
		ct1.grammaticalFeatures = grammarFeats1;

		UD2CGNSonar = ct1;
	}
}

