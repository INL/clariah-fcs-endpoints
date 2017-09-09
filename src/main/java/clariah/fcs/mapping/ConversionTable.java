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
    
    boolean includeFeatureNameInRegex = true;
    
	private Map<String, String> fieldMap = new HashMap<>();
	private Map<Feature, FeatureConjunction> featureMap = new HashMap<>(); // te simpel, moet naar featureConjunction of disjunction kunnen mappen

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
			Feature original = new Feature(x[0], x[1]);
			FeatureConjunction fc = new FeatureConjunction();
			for (int i=2; i < x.length; i+=2)
			fc.put(x[i], x[i+1]);
			featureMap.put(original, fc);
		}
	}

	@Override
	public Set<String> translatePoS(String PoS) {
		Feature f = new Feature("pos",PoS);
		Feature v = this.featureMap.get(f).features().findFirst().get();
		if (v == null)
			v = f;
		return v.values;
	}

	@Override
	public Set<FeatureConjunction> translateFeature(String feature, String value) {
		// TODO Auto-generated method stub
		Feature f = new Feature(feature,value);
		FeatureConjunction fc = this.featureMap.get(f);
		if (fc == null)
		{
			 fc = new FeatureConjunction();
			fc.put(f.name, f.values); // TODO geen pass-through meer als niet gemapt
		}
		Set<FeatureConjunction> s = new HashSet<>();		
		s.add(fc);
		return s;
	}

	public static void main(String[] args)
	{

		
		ConversionTable ct = Conversions.UD2CGNSonar;
		
		String q = "([word='aap{3}' & pos='NOUN' & Number='Plur'] [pos='CCONJ'][lemma='niet.*']){3}";
		//q = "[pos='AUX' | pos =  'SCONJ'][pos='DET'][]{0,7}[pos='INTJ']";
		// Conversion.bla(q);


		AttackOfTheClones x = new AttackOfTheClones(ct);
		
		QueryNode rw = x.rewrite(q);
		String rws = rw.toString();
		System.out.println(rws);
		WriteAsCQP wasqp = new WriteAsCQP();
		wasqp.setQuote("\"");
		wasqp.setRegexHack(ct.posTagField, ct.grammaticalFeatures, ct.includeFeatureNameInRegex);
		System.out.println(wasqp.writeAsCQP(rw));
	}
}
