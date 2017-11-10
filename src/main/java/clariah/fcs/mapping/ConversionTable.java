package clariah.fcs.mapping;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import eu.clarin.sru.server.fcs.FCSQueryParser;
import eu.clarin.sru.server.fcs.parser.QueryProcessor;
import eu.clarin.sru.server.fcs.parser.QueryNode;
import eu.clarin.sru.server.fcs.parser.WriteAsCQP;



/*
 * This class stores the tag set conversion tables
 * and holds some methods for translating annotations written in a given tag set into another.
 */



public class ConversionTable extends Conversion
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
	private Map<Feature, Set<FeatureConjunction>> featureMap = new ConcurrentHashMap<>(); // te simpel, moet naar featureConjunction of disjunction kunnen mappen

	
	// ---------------------------------------------------------------------------------------
	
	
	// constructor
	
	public ConversionTable(String[][] fieldMapping, String[][] featureMapping) 
	{
		this.fieldMapping = fieldMapping;
		this.featureMapping = featureMapping;
		init();
	}
	
	
	// subroutine of constructor
	
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
		QueryProcessor x = new QueryProcessor(this);
		QueryNode rw = x.rewrite(query);
		
		WriteAsCQP wasqp = new WriteAsCQP();
		wasqp.setQuote(this.getQuote());
		
		if (this.usesFeatureRegex())
		 wasqp.setRegexHack(this.getPosTagField(), this.getGrammaticalFeatures(), this.hasIncludedFeatureNameInRegex());
		
		return wasqp.writeAsCQP(rw);
	}
	
	public static void main(String[] args)
	{


		Conversion ct = Conversions.getConversionTable("UD2CHN");

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
