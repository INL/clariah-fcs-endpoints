package clariah.fcs.mapping;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import eu.clarin.sru.server.fcs.FCSQueryParser;
import eu.clarin.sru.server.fcs.parser.QueryProcessor;
import eu.clarin.sru.server.fcs.parser.QueryNode;
import eu.clarin.sru.server.fcs.parser.CqpWriter;



/**
 * This class stores the tag set conversion tables
 * and holds methods for translating annotations written in a given tag set into another.
 * 
 * 
 * Hm.
 * Already at this level, a single feature might translate to a disjunction or a conjunction 
 * of other features, or a disjunction of conjunctions. 
 * this should also cover translation of word= to t_lc= for Nederlab, etc.
 *
 * @author jesse
 *
 */
public class ConversionEngine 
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
	
	public ConversionEngine(String[][] fieldMapping, String[][] featureMapping) 
	{
		this.fieldMapping = fieldMapping;
		this.featureMapping = featureMapping;
		init();
	}
	
	
	// subroutine of constructor
	
	private void init() {
		for (String[] x : this.fieldMapping) {
			this.fieldMap.put(x[0], x[1]); // 
		}
		
		// this part is about pos tags translation
		
		for (String[] x : this.featureMapping) 
		{
			// source tag
			
			Feature source = new Feature(x[0], x[1]); // [0 = key, 1 = value]
			
			// destination tag
			
			FeatureConjunction fc = new FeatureConjunction();
			for (int i=2; i < x.length; i+=2)
				fc.put(x[i], x[i+1]);
			
			Set<FeatureConjunction> destination = this.featureMap.get(source);
			if (destination== null) 
				destination = new HashSet<FeatureConjunction>();
			
			destination.add(fc);
			this.featureMap.put(source, destination);
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
	

	
	/**
	 * Translate a query, meaning: converting 
	 * universal dependencies (cross-linguistically consistent grammatical annotations)
	 * into the set of tags and features instantiated in this class
	 * 
	 * Input is the query part of FSC request, like:
	 * 
	 * 		query= [word="lopen"][pos="NOUN"] 
	 *  
	 * Output is CQP, like:
	 * 
	 * 		cqp="[word="lopen"] [pos="^(N).*" & pos=".*[\(,\|](soort)[,\)\|].*"] 
	 */
	public String translateQuery(String query)
	{
		// convert the query into nodes
		// and translate the tags and features 
		
		QueryProcessor qp = new QueryProcessor(this);
		QueryNode queryAsNode = qp.rewrite(query); // this one will translate the features
		
		
		// convert the nodes into CQP
		
		CqpWriter cqpWriter = new CqpWriter();
		cqpWriter.setQuote(this.getQuote());
		
		if (this.usesFeatureRegex())
		 cqpWriter.setRegexHack(this.getPosTagField(), this.getGrammaticalFeatures(), this.hasIncludedFeatureNameInRegex());
		
		return cqpWriter.writeAsCQP( queryAsNode );
	}
	
	
	/**
	 * Translate a feature put in a given tag set
	 * into another tag set
	 * 
	 * @param feature
	 * @param value
	 * @return
	 */
	public Set<FeatureConjunction> translateFeature(String feature, String value) {
		// TODO Auto-generated method stub
		Feature source = new Feature(feature, value);
		Set<FeatureConjunction> destination = this.featureMap.get(source); 

		if (destination == null)
		{
			FeatureConjunction fc = new FeatureConjunction();
			fc.put(source.getFeatureName(), source.getValues()); // TODO geen pass-through meer als niet gemapt
			destination = new HashSet<>();		
			destination.add(fc);
		}

		return destination;
	}
	
	
	// --------------------------------------------------------------------------
	
	// for test only
	
	public static void main(String[] args)
	{


		ConversionEngine ct = ConversionObjectProcessor.getConversionEngine("UD2CHN");

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
