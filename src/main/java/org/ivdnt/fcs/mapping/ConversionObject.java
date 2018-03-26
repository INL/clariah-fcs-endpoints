package org.ivdnt.fcs.mapping;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * This class is needed for the ObjectMapper
 * 
 * It is used when an engine configuration file is read at start up:
 * 
 * The configuration files (stored in WEB-INF) have JSON format and contain tag
 * sets conversion tables for our endpoint engines (so some tags in a query can
 * be correctly translated into the tags set of a particular corpus. Eg.
 * pos='ADJ' --> pas='bnw').
 * 
 * This class makes sure the JSON input can be converted into a JAVA object to
 * be used by the engines
 * 
 * @author fannee
 *
 */
public class ConversionObject {

	@JsonProperty("ConversionTable")
	private String name;

	@JsonProperty("Quote")
	private String quote;

	@JsonProperty("IncludeFeatureNameInRegex")
	private boolean includeFeatureNameInRegex;

	@JsonProperty("UseFeatureRegex")
	private boolean useFeatureRegex;

	@JsonProperty("GrammaticalFeatures")
	private String[] grammaticalFeatures;

	@JsonProperty("PosTagField")
	private String posTagField;

	@JsonProperty("FieldMapping")
	private ArrayList<ConcurrentHashMap<String, String>> fieldMapping;

	@JsonProperty("FeatureMapping")
	private ArrayList<ConcurrentHashMap<String, ConcurrentHashMap<String, String>>> featureMapping;

	// --------------------------------------------------------------------
	// getters and setters

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getQuote() {
		return this.quote;
	}

	public void setQuote(String quote) {
		this.quote = quote;
	}

	public boolean hasIncludedFeatureNameInRegex() {
		return includeFeatureNameInRegex;
	}

	public void setIncludeFeatureNameInRegex(boolean includeFeatureNameInRegex) {
		this.includeFeatureNameInRegex = includeFeatureNameInRegex;
	}

	public boolean usesFeatureRegex() {
		return useFeatureRegex;
	}

	public void setUseFeatureRegex(boolean useFeatureRegex) {
		this.useFeatureRegex = useFeatureRegex;
	}

	public String[] getGrammaticalFeatures() {
		return grammaticalFeatures;
	}

	public void setGrammaticalFeatures(String[] grammaticalFeatures) {
		this.grammaticalFeatures = grammaticalFeatures;
	}

	public String getPosTagField() {
		return posTagField;
	}

	public void setPosTagField(String posTagField) {
		this.posTagField = posTagField;
	}

	public ArrayList<ConcurrentHashMap<String, String>> getFieldMapping() {
		return fieldMapping;
	}

	public void setFieldMapping(ArrayList<ConcurrentHashMap<String, String>> fieldMapping) {
		this.fieldMapping = fieldMapping;
	}

	public ArrayList<ConcurrentHashMap<String, ConcurrentHashMap<String, String>>> getFeatureMapping() {
		return featureMapping;
	}

	public void setFeatureMapping(
			ArrayList<ConcurrentHashMap<String, ConcurrentHashMap<String, String>>> featureMapping) {
		this.featureMapping = featureMapping;
	}

	// --------------------------------------------------------------------
}
