package org.ivdnt.fcs.results;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.ivdnt.util.Utils;

/**
 * This class is used to store FCS keywords in context (kwic) as part of the
 * ResultSet (which is part of a FcsSearchResultSet = main result object)
 * 
 * @author jesse
 *
 */
public class Kwic {

	// A Kwic (keyword in context) is a list of words in which a match was found
	// so it consists of [a left context], [a match], and [a right context]
	//
	// The tokens of those 3 parts are stored together in lists, each of which
	// represents one property.
	// So, for a given token at position X, we can retrieve the value of a given
	// property
	// by accessing the corresponding list at the same position X
	//
	// (t.i. we have one sorted list for each single property, so there are as many
	// lists
	// as properties)
	//
	// To be able to recognize each part, the Kwic object has
	// hitStart and hitEnd, which indicate the borders of the [match part]
	// = start and end position of a keyword, within its context

	private int hitStart;
	private int hitEnd;

	// default property

	private String defaultProperty = "word";

	// the list contains the names of all the properties contained in our Kwic
	// object

	private List<String> tokenPropertyNames = new ArrayList<>();

	// this maps a property name to a sorted list of token properties
	// (sorted, as the list represents the tokens on the result string)

	private Map<String, List<String>> tokenProperties = new ConcurrentHashMap<>();

	// metadata

	private Map<String, String> metadata = new ConcurrentHashMap<>(); // considering the size of opensonar metadata,
																		// probably better to introduce separate
																		// document objects
	// private Document document = null;

	// -----------------------------------------------------------------------------------

	public Kwic translatePrefixes(Map<String, String> map) {
		map.forEach((k, v) -> {
			this.tokenProperties.put(v, this.tokenProperties.get(k));
			this.tokenProperties.remove(k);
		});

		this.tokenPropertyNames = this.tokenPropertyNames.stream().map(p -> map.containsKey(p) ? map.get(p) : p)
				.collect(Collectors.toList());
		return this;
	}

	// -----------------------------------------------------------------------------------
	// getters

	public String getDefaultProperty() {
		return this.defaultProperty;
	}

	public List<String> getLayer(String propertyName) {
		return this.tokenProperties.get(propertyName);
	}

	// synonym of getLayer
	public List<String> getPropertyValues(String propertyName) {
		return getLayer(propertyName);
	}

	public String getWord(int i) {
		return this.tokenProperties.get(defaultProperty).get(i);
	}

	public List<String> words() {
		return this.tokenProperties.get(defaultProperty);
	}

	public int size() {
		return words().size();
	}

	public String get(String pname, int i) {
		return getLayer(pname).get(i);
	}

	public URI getLayerURL(String pname) {
		try {
			return new URI("http://www.ivdnt.org/annotation-layers/" + pname);
		} catch (Exception e) {

			Utils.printStackTrace(e);
			return null;
		}
	}

	public Map<String, String> getMetadata() {
		return this.metadata;
	}

	public int getHitStart() {
		return this.hitStart;
	}

	public int getHitEnd() {
		return this.hitEnd;
	}

	public List<String> getTokenPropertyNames() {

		return this.tokenPropertyNames;
	}

	// -----------------------------------------------------------------------------------
	// setters

	public void setMetadata(Map<String, String> metadata) {
		this.metadata = metadata;
	}

	public void setHitStart(int hitStart) {
		this.hitStart = hitStart;
	}

	public void setHitEnd(int hitEnd) {
		this.hitEnd = hitEnd;
	}

	public void addTokenPropertyName(String pname) {
		this.tokenPropertyNames.add(pname);
	}

	public void addTokenPropertyNames(Set<String> pnames) {
		this.tokenPropertyNames.addAll(pnames);
	}

	// set one property for all tokens at once
	// eg. the pos-tag of all tokens of a sentence
	public void setTokenProperties(String pname, List<String> properties) {
		this.tokenProperties.put(pname, properties);
	}

	// modify the value of a property for token at position X
	public void setTokenPropertyAt(String propertyName, String property, int index) {
		List<String> propertyValues = this.getLayer(propertyName);

		if (propertyValues.size() == 0)
			propertyValues = new ArrayList<>(this.getTokenPropertyNames().size());

		propertyValues.set(index, property);
		this.setTokenProperties(propertyName, propertyValues);
	}

	// add a new property name to all tokens
	public void addTokenProperty(String propertyName) {

		// register the property name
		// and
		// add the property to all tokens
		if (!this.tokenPropertyNames.contains(propertyName)) {
			this.addTokenPropertyName(propertyName);

			List<String> propertyValues = new ArrayList<>(this.size());
			for (int i = 0; i < this.size(); i++) {
				propertyValues.add(null);
			}
			this.setTokenProperties(propertyName, propertyValues);
		}

	}

	// -----------------------------------------------------------------------------------

	public String toString() {
		List<String> tokens = new ArrayList<String>();
		List<String> words = words();
		for (int i = 0; i < words.size(); i++) {
			String p = (this.hitStart <= i && i <= this.hitEnd) ? ">>" : "";
			tokens.add(p + words.get(i));
		}
		String s = String.format("Kwic(%d,%d):", this.hitStart, this.hitEnd);
		return s + tokens.toString();
	}

	// -----------------------------------------------------------------------------------

}

/**
 * Wat gaan we met metadata doen? In een CMDI profiel stoppen? Hoe halen we dat
 * op bij nederlab? Voor blacklab server zelf maken uit de aanwezige metadata.
 * https://www.clarin.eu/sites/default/files/CE-2014-0317-CLARIN_FCS_Specification_DataViews_1_0.pdf:
 * 
 * <!-- potential @pid and @ref attributes omitted -->
 * <fcs:DataView type="application/x-cmdi+xml">
 * <cmdi:CMD xmlns:cmdi="http://www.clarin.eu/cmd/" CMDVersion="1.1"> <!--
 * content omitted --> </cmdi:CMD> </fcs:DataView> <!-- potential @pid attribute
 * omitted --> <fcs:DataView type="application/x-cmdi+xml" ref=
 * "http://repos.example.org/resources/4711/0815.cmdi" />
 */
