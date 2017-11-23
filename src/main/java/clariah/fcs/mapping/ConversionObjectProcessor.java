package clariah.fcs.mapping;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 
 * @author jesse
 * Even houtjetouwtje in code. 
 * TODO zet in XML bestandje (Wat eigenlijk weer minder leesbaar is :))
 * 
 * @author Mathieu
 * 3 nov 2017 in JSON gegoten
 * 
 * Kijk ook naar https://raw.githubusercontent.com/proycon/folia/master/setdefinitions/frog-mbpos-cgn voor de feature namen in cgn tags
 */

public class ConversionObjectProcessor 
{
	
	private static ConcurrentHashMap<String, ConversionEngine> conversionsMap = 
			new ConcurrentHashMap<String, ConversionEngine>();
	
	
	// --------------------------------------------------------------------------
	// setters
	
	public static void processConversionTable(String conversionName, ConversionObject jsonMappingObject ) {
		
				
		// get mapping in right internal format
		
		String[][] fieldMapping = 	getfieldMapping( 	jsonMappingObject.getFieldMapping() );
		String[][] featureMapping = getFeatureMapping( 	jsonMappingObject.getFeatureMapping() );
		
		
		// generate new conversion engine, containing mapping and such
		
		ConversionEngine conversionEngine = new ConversionEngine(fieldMapping, featureMapping);
		
		conversionEngine.setUseFeatureRegex( jsonMappingObject.usesFeatureRegex() );
		conversionEngine.setPosTagField( jsonMappingObject.getPosTagField() );
		conversionEngine.setQuote( jsonMappingObject.getQuote() );
		conversionEngine.setGrammaticalFeatures( jsonMappingObject.getGrammaticalFeatures() );
		conversionEngine.setIncludeFeatureNameInRegex( jsonMappingObject.hasIncludedFeatureNameInRegex() );
		conversionEngine.setName( conversionName );
		
		
		
		// store this new conversion map
		
		conversionsMap.put(conversionName, conversionEngine);
	}
	
	
	// --------------------------------------------------------------------------
	// getters
	
	public static ConversionEngine getConversionEngine(String name) {
		
		return conversionsMap.get(name);
		
	}
	
	public static ConcurrentHashMap<String, ConversionEngine> getConversionEngines() {
		
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
	 * Remove the 'comment' key for a tagset hash
	 * 
	 * NB: the 'comment key' is needed in the JSON file to be able to add comment to some data,
	 *     but we don't want it to make it to the conversion engine as it is no part of the
	 *     tags of features to be mapped to another tag set
	 * 
	 * @param hash
	 * @return
	 */
	private static ConcurrentHashMap<String, String> removeCommentsFromHash(ConcurrentHashMap<String, String> hash){
		
		hash.remove("comment");		
		
		return hash;
	}
	
	
}
	
	