package org.ivdnt.fcs.mapping;

import java.util.HashSet;
import java.util.Set;

import org.ivdnt.util.StringUtils;

public class Feature {
	// A feature has only one name
	// but it can have several values. In a string expression, those can be joined
	// as a Regex.

	private String name;
	private Set<String> values = new HashSet<String>();

	// -----------------------------------------------------------------------
	// constructors

	public Feature(String name, Set<String> values) {
		this.name = name;
		this.values = values;
	}

	// Constructor for cases in which the values are given as a string with a
	// separator

	public Feature(String name, String values) {
		this.name = name;

		Set<String> setOfValues = new HashSet<>();
		for (String oneValue : values.split(MappingConstants.MULTIVALUE_SEPARATOR)) {
			setOfValues.add(oneValue);
		}

		this.values = setOfValues;
	}

	// -----------------------------------------------------------------------
	// getters

	/**
	 * Get an equality expression string for the current feature and all its values
	 * joined into a Regex string
	 * 
	 * feature x -&gt; { a, b, c}
	 * 
	 * will give
	 * 
	 * x = 'a|b|c'
	 * 
	 * @return CQL representation
	 */
	public String asCQL() {
		String pipeSeparatedValues = StringUtils.join(this.values, "|");
		return String.format("%s='%s'", this.name, pipeSeparatedValues);
	}

	/**
	 * Build an equality expression string mapping a given tag field to the current
	 * feature name and all its values joined into a Regex string
	 * 
	 * feature x -&gt; { a, b, c}
	 * 
	 * will give
	 * 
	 * field = '... x = a|b|c ...'
	 * 
	 * @param CQLTagField the CQL tag field
	 * @return CQL representation 
	 */
	public String asRegexInTag(String CQLTagField) {
		String pipeSeparatedValues = StringUtils.join(this.values, "|");

		if (this.name.equals(CQLTagField))
			return String.format("%s='^(%s).*'", this.name, pipeSeparatedValues);
		else
			return String.format("%s='.*%s\\s*=(%s).*'", CQLTagField, this.name, pipeSeparatedValues);
	}

	// -----------------------------------------------------------------------

	/**
	 * Test equality of current feature with another
	 */
	public boolean equals(Object other) {
		try {
			Feature feature = (Feature) other;

			return (feature.getFeatureName().equals(this.name) && feature.getValues().equals(this.values));

		} catch (Exception e) {
			return false;
		}
	}

	public String getFeatureName() {
		return this.name;
	}

	public Set<String> getValues() {
		return this.values;
	}

	public int hashCode() {
		return this.name.hashCode() + this.values.hashCode();
	}
}
