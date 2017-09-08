package clariah.fcs.mapping;

public class Conversions 
{
	static  ConversionTable UD2CHN;

	static ConversionTable UD2BaB;

	static ConversionTable UD2CGNSonar; 

	static
	{
		String[][] fieldMapping  = {{"xxword", "t"}};

		String[][] featureMapping = 
			{
					{"pos","ADJ", "pos", "AA"},
					{"pos","ADV", "pos", "ADV|AA"},
					{"pos", "INTJ", "pos", "INT"},

					{"pos", "NOUN", "pos", "NOU-C"},
					{"pos", "PROPN", "pos", "NOU-P"},
					
					{"pos", "VERB", "pos", "VRB"},

					{"pos", "ADP",  "pos", "ADP"},
					{"pos", "AUX", "pos", "VRB"},  // HM
					{"pos", "CCONJ", "pos", "CONJ"}, // HM
					{"pos", "DET", "pos", "ART|PRN"}, // HM
					{"pos", "NUM", "pos", "NUM"},
					{"pos", "PART", "pos", "VRB"}, // HM
					{"pos", "PRON", "pos", "PRN"}, // HM
					{"pos", "SCONJ", "pos", "CONJ"}, // HM
					{"pos", "PUNCT", "pos", "RES"}, // HM hebben we niet
					{"pos", "SYM", "pos", "RES"},
					{"pos", "X", "pos", "RES"}

					{"number", "Plur", "number", "pl"},
					{"number", "Sing", "number", "sg"},
					{"gender", "Fem", "gender", "f"},
					{"gender", "Masc", "gender", "m"},
					{"gender", "Neut", "gender", "n"},

					{"tense", "Past", "tense", "past"},
					{"tense", "Pres", "tense", "pres"},
					//{''tense", "pres", "tense", "pres"}
			};

		String[] grammarFeats = {"number", "tense", "mood", "type"};

		ConversionTable ct = new ConversionTable(fieldMapping, featureMapping);


		ct.useFeatureRegex = true;
		ct.posTagField = "pos";
		ct.grammaticalFeatures = grammarFeats;

		UD2CHN = ct;
	}



	static
	{
		String[][] fieldMapping  = {{"xxword", "t"}};

		String[][] featureMapping = 
			{
					{"pos","ADJ", "pos", "ADJ"},
					{"pos","ADV", "pos", "BW"},
					{"pos", "INTJ", "pos", "TSW"},
					{"pos", "NOUN", "pos", "N"},
					{"pos", "VERB", "pos", "WW"},

					{"pos", "ADP",  "pos", "VZ"},
					{"pos", "AUX", "pos", "WW"},  // HM
					{"pos", "CCONJ", "pos", "VG"}, // HM
					{"pos", "DET", "pos", "LID|VNW"}, // HM
					{"pos", "NUM", "pos", "TW"},
					{"pos", "PART", "pos", "WW"}, // HM
					{"pos", "PRON", "pos", "PRN"}, // HM
					{"pos", "SCONJ", "pos", "VG"}, // HM
					{"pos", "PUNCT", "pos", "PUNCT"}, // HM hebben we niet
					{"pos", "SYM", "pos", "SPEC"},
					{"pos", "X", "pos", "SPEC"}
			};

		String[] grammarFeats = {"number", "tense", "mood", "type"};

		ConversionTable ct = new ConversionTable(fieldMapping, featureMapping);


		ct.useFeatureRegex = true;
		ct.posTagField = "pos";
		ct.grammaticalFeatures = grammarFeats;

		UD2CGNSonar = ct;
	}
}

