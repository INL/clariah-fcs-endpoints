package clariah.fcs.mapping;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 
 * @author jesse
 * Even houtjetouwtje in code. TODO zet in XML bestandje (Wat eigenlijk weer minder leesbaar is :))
 * 
 * @author Mathieu
 * 3 nov 2017 in JSON gegoten
 * 
 * Kijk ook naar https://raw.githubusercontent.com/proycon/folia/master/setdefinitions/frog-mbpos-cgn voor de feature namen in cgn tags
 */

public class Conversions 
{
	
	private static ConcurrentHashMap<String, Conversion> conversionsMap = new ConcurrentHashMap<String, Conversion>();
	
	
	// setters
	
	public static void processConversionTable(String conversionName, JsonConversionObject jsonMappingObject ) {
		
				
		// get mapping in right internal format
		
		String[][] fieldMapping = 	getfieldMapping( 	jsonMappingObject.getFieldMapping() );
		String[][] featureMapping = getFeatureMapping( 	jsonMappingObject.getFeatureMapping() );
		
		
		// generate new conversion table object, containing mapping and such
		
		ConversionTable conversionTable = new ConversionTable(fieldMapping, featureMapping);
		
		conversionTable.setUseFeatureRegex( jsonMappingObject.usesFeatureRegex() );
		conversionTable.setPosTagField( jsonMappingObject.getPosTagField() );
		conversionTable.setQuote( jsonMappingObject.getQuote() );
		conversionTable.setGrammaticalFeatures( jsonMappingObject.getGrammaticalFeatures() );
		conversionTable.setIncludeFeatureNameInRegex( jsonMappingObject.hasIncludedFeatureNameInRegex() );
		conversionTable.setName( conversionName );
		
		
		
		// store this new conversion map
		
		conversionsMap.put(conversionName, conversionTable);
	}
	
	
	
	// getters
	
	public static Conversion getConversionTable(String name) {
		
		return conversionsMap.get(name);
		
	}
	
	public static ConcurrentHashMap<String, Conversion> getConversionTables() {
		
		return conversionsMap;
		
	}


	
	
	
	
	// --------------------------------------------------------------------------
	
	// SUB-ROUTINES:
	
	
	/**
	 *  convert field mapping to needed internal format
	 * @param jsonFieldMapping
	 * @return
	 */	
	private static String[][] getfieldMapping(ArrayList<ConcurrentHashMap<String, String>> jsonFieldMapping){
		
		String[][] fieldMapping = new String[ jsonFieldMapping.size() ] [ 2 ];
		
		// loop through mapping
		
		for (int i=0; i<jsonFieldMapping.size(); i++)
		{
			// convert the hash into a string array
			
			ConcurrentHashMap<String, String> oneSet = jsonFieldMapping.get(i);
			fieldMapping[i][0] = oneSet.get("from");			
			fieldMapping[i][1] = oneSet.get("to");
		}
		
		
		return fieldMapping;
	};
	
	
	/**
	 *  convert feature mapping to needed internal format
	 * @param jsonFeatureMapping
	 * @return
	 */	
	private static String[][] getFeatureMapping(ArrayList<ConcurrentHashMap<String, ConcurrentHashMap<String, String>>> jsonFeatureMapping){
		
		String[][] featureMapping = new String[ jsonFeatureMapping.size() ] [];
		
		// loop through mapping
		
		for (int i=0; i<jsonFeatureMapping.size(); i++)
		{
			// get a single conversion set  FROM -> TO 
			
			ConcurrentHashMap<String, ConcurrentHashMap<String, String>> oneSet = jsonFeatureMapping.get(i);
			ConcurrentHashMap<String, String> fromSet =	removeCommentsFromHash( oneSet.get("from") );
			ConcurrentHashMap<String, String> toSet = 	removeCommentsFromHash( oneSet.get("to") );
			
			// convert the hash into a string array
			
			ArrayList<String> tmpArr = new ArrayList<String>();
			for (String oneKey : fromSet.keySet())
			{
				tmpArr.add(oneKey);
				tmpArr.add(fromSet.get(oneKey));
			}
			for (String oneKey : toSet.keySet())
			{
				tmpArr.add(oneKey);
				tmpArr.add(toSet.get(oneKey));
			}
			
			featureMapping[i] = new String[tmpArr.size()];
			featureMapping[i] = tmpArr.toArray( featureMapping[i] );
		}
		
		return featureMapping;
	}
	
	
	/**
	 * remove the 'comment' key for the hash
	 * 
	 * NB: the comment key is needed in the JSON file to be able to add comment to some data,
	 *     we don't want it to make it to the conversion table
	 * 
	 * @param hash
	 * @return
	 */
	private static ConcurrentHashMap<String, String> removeCommentsFromHash(ConcurrentHashMap<String, String> hash){
		
		hash.remove("comment");		
		
		return hash;
	}
	
	
}
	
	