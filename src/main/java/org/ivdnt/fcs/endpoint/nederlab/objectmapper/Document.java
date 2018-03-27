package org.ivdnt.fcs.endpoint.nederlab.objectmapper;

import java.util.HashMap;
import java.util.Map;

/**
 * This class is used by the Json ObjectMapper so as deserialize the JSON string
 * response from Nederlab into Java objects (here: Document, Token,
 * TokenPropery, ...)
 * 
 * @author does { "NLProfile_name": "nederlabTitleProfile",
 *         "NLCore_NLIdentification_nederlabID":
 *         "c277847b-1264-4980-9b94-4350f5c43056", "NLTitle_title":
 *         "Notificatie." },
 */
public class Document {
	/*public String NLCore_NLIdentification_nederlabID;
	public String NLTitle_title;
	public String NLTitle_yearOfPublicationMin;
	public String NLTitle_yearOfPublicationMax;
	public String NLProfile_name;
	public String NLCore_NLAdministrative_sourceCollection; // of NLCore_NLExternalReference_collectionName
*/
	
	private Map<String,String> data =new HashMap<String,String>();
	
	public Document(Object obj) {
		@SuppressWarnings("unchecked")
		Map<String,Object> obj_map = (Map<String,Object>) obj;
		// Map values are converted from Object (text or number) to String
		for (Map.Entry<String, Object> entry : obj_map.entrySet()) {
			data.put(entry.getKey(), entry.getValue().toString());
		 }
	}
	
	public String getField(String fieldName) {
		if (!data.containsKey(fieldName)) {
			throw new NullPointerException("Key " + fieldName + " does not exist in Document object.");
		}
		return data.get(fieldName);
	}
	
	public Map<String, String> getMetadata() {
		return data;
	}
}
