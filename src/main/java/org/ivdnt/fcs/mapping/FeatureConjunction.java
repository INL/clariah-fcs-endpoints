package org.ivdnt.fcs.mapping;

import java.lang.invoke.MethodHandles;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.ivdnt.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is an extension of the ConcurrentHashMap class with some extra get
 * / toString-like methods
 * 
 * @author jesse
 *
 */
public class FeatureConjunction extends ConcurrentHashMap<String, Set<String>> {

	// logger
	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	// ------------------------------------------------
	// setter

	/**
	 * 
	 */
	private static final long serialVersionUID = -815788743698217044L;

	public static void main(String[] args) {

		FeatureConjunction fc = new FeatureConjunction();
		fc.put("pos", "RES");
		fc.put("type", "sym");
		logger.info(fc.asCQL());

	}

	// ------------------------------------------------
	// getters

	/**
	 * Write the conjunction of features stored in this class as a string with an
	 * AND operator
	 * 
	 * like '[ ... & ... ]'
	 * 
	 * and join the feature values into Regexes
	 * 
	 * @return CQL query representation
	 */
	public String asCQL() {
		Set<String> clauses = this.keySet().stream().map(

				k -> new Feature(k, get(k)).asCQL()

		).collect(Collectors.toSet());

		return "[" + StringUtils.join(clauses, " & ") + "]";
	}

	/**
	 * Write the conjunction of features stored in this class as a string with an
	 * AND operator, like
	 * 
	 * '[ ... & ... ]'
	 * 
	 * mapping the 'pos' tag field to a feature and all its values joined into a
	 * Regex
	 * 
	 * So each clause is like
	 * 
	 * field = '... featureName = a|b|c ...'
	 * 
	 * @return CQL representation
	 */
	public String asRegexInTag() {
		Set<String> clauses = this.keySet().stream().map(

				// this will give an equality expression string
				// for the 'pos' field and the feature values
				k -> new Feature(k, get(k)).asRegexInTag("pos")

		).collect(Collectors.toSet());

		return "[" + StringUtils.join(clauses, " & ") + "]";
	}

	/**
	 * Get the set of values of a feature, given its name
	 * 
	 */
	@Override
	public Set<String> get(Object s) // oho this is silly..
	{
		Set<String> r = super.get(s);

		if (r != null)
			return r;

		// if feature is unknown, return empty set
		else
			return new HashSet<String>();
	}

	/**
	 * Get all the features of the conjunction of features stored in this class
	 * 
	 * @return feature set as stream
	 */
	public Stream<Feature> getFeatures() {
		return this.keySet().stream().map(

				k -> new Feature(k, get(k))

		);
	}

	/**
	 * Join the values of a feature, given its name
	 * 
	 * @param name
	 * @return string representation
	 */
	public String getJoinedValues(String name) {
		return StringUtils.join(get(name), MappingConstants.MULTIVALUE_JOINER);
	}

	// ------------------------------------------------
	// kind of toString() methods

	/**
	 * Check if the conjunction stored in this class contains a given feature
	 * 
	 * @param featureName
	 * @return do we have this feature?
	 */
	public boolean hasFeature(String featureName) {
		return this.containsKey(featureName) || !this.get(featureName).isEmpty();
	}

	/**
	 * Check if the conjunction stored in this class contains a feature with a given
	 * value
	 * 
	 * @param featureName
	 * @param featureValue
	 * @return do we have this feature and value?
	 */
	public boolean hasFeatureWithValue(String featureName, String featureValue) {

		return this.hasFeature(featureName) && this.get(featureName).contains(featureValue);
	}

	// ---------------------------------
	// NOT IN USE

	public Set<String> keySetX() {
		Set<String> Z = new HashSet<String>();
		for (String s : super.keySet()) {
			Z.add(s);
		}
		return Z;
	}

	// ---------------------------------

	/**
	 * Store a feature as a hash mapping a feature name to a feature value
	 * 
	 * @param name
	 * @param newValue
	 * 
	 */
	public void put(String name, String newValue) {
		Set<String> values = this.get(name);

		if (values == null || values.isEmpty())
			super.put(name, values = new HashSet<String>());

		values.add(newValue);
	}
}
