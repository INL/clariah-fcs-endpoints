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
    String quote = "'";
	boolean includeFeatureNameInRegex = true;

	private Map<String, String> fieldMap = new HashMap<>();
	private Map<Feature, Set<FeatureConjunction>> featureMap = new HashMap<>(); // te simpel, moet naar featureConjunction of disjunction kunnen mappen

	public ConversionTable(String[][] fieldMapping, String[][] featureMapping) 
	{
		this.fieldMapping = fieldMapping;
		this.featureMapping = featureMapping;
		init();
	}

	public String getQuote()
	{
		return quote;
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
			Set<FeatureConjunction> s = featureMap.get(original);
			if (s== null) s = new HashSet<FeatureConjunction>();
			s.add(fc);
			featureMap.put(original, s);
		}
	}

	@Override
	public Set<String> translatePoS(String PoS) { // TODO dit werkt niet en is eigenlijk helemaal niet nodig ....
		Feature f = new Feature("pos",PoS);
		Feature v = this.featureMap.get(f).stream().findFirst().get().features().findFirst().get();
		if (v == null)
			v = f;
		return v.values;
	}

	@Override
	public Set<FeatureConjunction> translateFeature(String feature, String value) {
		// TODO Auto-generated method stub
		Feature f = new Feature(feature,value);
		Set<FeatureConjunction> s = this.featureMap.get(f); 

		if (s == null)
		{
			FeatureConjunction fc = new FeatureConjunction();
			fc.put(f.name, f.values); // TODO geen pass-through meer als niet gemapt
			s = new HashSet<>();		
			s.add(fc);
		}

		return s;
	}

	@Override
	public String translateQuery(String query)
	{
		AttackOfTheClones x = new AttackOfTheClones(this);
		QueryNode rw = x.rewrite(query);
		String rws = rw.toString();
		WriteAsCQP wasqp = new WriteAsCQP();
		wasqp.setQuote(this.getQuote());
		if (this.useFeatureRegex)
		 wasqp.setRegexHack(this.posTagField, this.grammaticalFeatures, this.includeFeatureNameInRegex);
		return wasqp.writeAsCQP(rw);
	}
	
	public static void main(String[] args)
	{


		ConversionTable ct = Conversions.UD2CGNSonar;

		String q = "[pos='PROPN'][lemma='aap' & pos='NOUN' & Number='Plur'] [pos='DET'][pos='CCONJ'][lemma='niet.*']";
		q = "[pos=\"NOUN\" & Number=\"Plur\"][pos=\"VERB\"]";
		q = "[pos=\"VERB\" & Tense=\"Past\" & VerbForm=\"Fin\"]";
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
