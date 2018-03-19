package org.ivdnt.fcs.mapping;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.ivdnt.util.StringUtils;

/**
 * This class is an extension of the ConcurrentHashMap class
 * with some extra get / toString-like methods
 * 
 * @author jesse
 *
 */
public class FeatureConjunction extends ConcurrentHashMap<String, Set<String>> 
{
	

	// ------------------------------------------------
	// setter
	
	/**
	 * Store a feature as a hash mapping a feature name to a feature value
	 * 
	 * @param name
	 * @param newValue
	 * 
	 */
	public void put(String name, String newValue)
	{
		Set<String> values = this.get(name);
		
		if (values == null || values.isEmpty()) 
			super.put(name, values = new HashSet<String>());
		
		values.add(newValue);
	}
	
	
	// ------------------------------------------------
	// getters

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
	 * Join the values of a feature, given its name
	 * 
	 * @param name
	 * @return
	 */
	public String getJoinedValues(String name)
	{
		return StringUtils.join( get(name), MappingConstants.MULTIVALUE_JOINER );
	}
	
	/**
	 * Get all the features 
	 * of the conjunction of features stored in this class
	 * 
	 * @return
	 */
	public Stream<Feature> getFeatures() 
	{
		return this.keySet().stream().map(
				
				k -> new Feature(k, get(k))
				
		);
	}

	/**
	 * Check if the conjunction stored in this class contains a given feature
	 * 
	 * @param featureName
	 * @return
	 */
	public boolean hasFeature(String featureName)
	{
		return this.containsKey(featureName) || !this.get(featureName).isEmpty();
	}

	/**
	 * Check if the conjunction stored in this class 
	 * contains a feature with a given value
	 *  
	 * @param featureName
	 * @param featureValue
	 * @return
	 */
	public boolean hasFeatureWithValue(String featureName, String featureValue) 
	{

		return 	this.hasFeature(featureName) && 
				this.get(featureName).contains(featureValue);
	}

	
	// ------------------------------------------------
	// kind of toString() methods
	
	/**
	 * Write the conjunction of features stored in this class
	 * as a string with an AND operator
	 * 
	 *   like '[ ... & ... ]'
	 * 
	 * and join the feature values into Regexes
	 * 
	 * @return
	 */
	public String asCQL()
	{
		Set<String> clauses = 
				this.keySet().stream().map(
						
						k -> new Feature(k, get(k)).asCQL()
						
				).collect(Collectors.toSet());
		
		return "[" + StringUtils.join(clauses, " & ") + "]";
	}
	
	
	
	/**
	 * Write the conjunction of features stored in this class
	 * as a string with an AND operator, like
	 * 
	 *   '[ ... & ... ]'
	 * 
	 * mapping the 'pos' tag field 
	 * to a feature and all its values joined into a Regex
	 * 
	 * So each clause is like
	 * 
	 *   field = '... featureName = a|b|c ...'
	 * 
	 * @return
	 */
	public String asRegexInTag()
	{
		Set<String> clauses = 
				this.keySet().stream().map(
						
						// this will give an equality expression string
						// for the 'pos' field and the feature values					
						k -> new Feature(k, get(k)).asRegexInTag("pos")
						
				).collect(Collectors.toSet());
		
		return "[" + StringUtils.join(clauses, " & ") + "]";
	}
	
	
	
	
	// ---------------------------------
	// NOT IN USE
	
	
	
	public static void main(String[] args)
	{
	
		FeatureConjunction fc = new FeatureConjunction();
		fc.put("pos", "RES");
		fc.put("type", "sym");
		System.out.println(fc.asCQL());
		
	}
	
	
	
	// ---------------------------------

	
	public Set<String> keySetX()
	{
		Set<String> Z = new HashSet<String>();
		for (String s: super.keySet())
		{
			Z.add(s);
		}
		return Z;
	}
}