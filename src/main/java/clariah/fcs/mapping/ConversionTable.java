package clariah.fcs.mapping;

import java.util.*;

import eu.clarin.sru.server.fcs.FCSQueryParser;
import eu.clarin.sru.server.fcs.parser.AttackOfTheClones;
import eu.clarin.sru.server.fcs.parser.QueryNode;
import eu.clarin.sru.server.fcs.parser.WriteAsCQP;

public class ConversionTable extends Conversion
{
	String[][] fieldMapping;
	String[][] featureMapping;
    boolean useFeatureRegex = false;
    String posTagField = null;
    String[] grammaticalFeatures = {};
    
	private Map<String, String> fieldMap = new HashMap<>();
	private Map<Feature, Feature> featureMap = new HashMap<>(); // te simpel, moet naar featureConjunction of disjunction kunnen mappen

	public ConversionTable(String[][] fieldMapping, String[][] featureMapping) 
	{
		this.fieldMapping = fieldMapping;
		this.featureMapping = featureMapping;
		init();
	}

	private void init() {
		for (String[] x : fieldMapping) {
			fieldMap.put(x[0], x[1]);
		}
		for (String[] x : featureMapping) 
		{
			featureMap.put(new Feature(x[0], x[1]), new Feature(x[2], x[3]));
		}
	}

	@Override
	public Set<String> translatePoS(String PoS) {
		Feature f = new Feature("pos",PoS);
		Feature v = this.featureMap.get(f);
		if (v == null)
			v = f;
		return v.values;
	}

	@Override
	public Set<FeatureConjunction> translateFeature(String feature, String value) {
		// TODO Auto-generated method stub
		Feature f = new Feature(feature,value);
		Feature v = this.featureMap.get(f);
		if (v == null)
			v = f;
		Set<FeatureConjunction> s = new HashSet<>();
		FeatureConjunction fc = new FeatureConjunction();
		fc.put(v.name, v.values);
		s.add(fc);
		return s;
	}

	public static void main(String[] args)
	{
		String[][] fieldMapping  = {{"t", "word"}};
		String[][] featureMapping = 
			{
					{"pos","ADJ", "pos", "ADJ"},
					{"pos","ADV", "pos", "ADV"},
					{"pos", "INTJ", "pos", "INT"},
					{"pos", "NOUN", "pos", "NOU-C"},
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
			};
		
		String[] grammarFeats = {"number", "tense", "mood", "type"};
		
		ConversionTable ct = new ConversionTable(fieldMapping, featureMapping);
		
		
		ct.useFeatureRegex = true;
		ct.posTagField = "pos";
		ct.grammaticalFeatures = grammarFeats;
		
		
		String q = "([word='aap{3}' & pos='VERB' & number='pl'] [lemma='niet.*']){3}";
		//q = "[pos='AUX' | pos =  'SCONJ'][pos='DET'][]{0,7}[pos='INTJ']";
		// Conversion.bla(q);


		AttackOfTheClones x = new AttackOfTheClones(ct);
		QueryNode rw = x.rewrite(q);
		String rws = rw.toString();
		System.out.println(rws);
		WriteAsCQP wasqp = new WriteAsCQP();
		wasqp.setQuote("\"");
		wasqp.setRegexHack(ct.posTagField, ct.grammaticalFeatures);
		System.out.println(wasqp.writeAsCQP(rw));
	}
}
