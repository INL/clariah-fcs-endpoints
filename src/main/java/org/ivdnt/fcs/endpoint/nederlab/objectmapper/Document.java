package org.ivdnt.fcs.endpoint.nederlab.objectmapper;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This class is used by the Json ObjectMapper
 * so as deserialize the JSON string response from Nederlab 
 * into Java objects (here: Document, Token, TokenPropery, ...)
 * 
 * @author does
 *{
      "NLProfile_name": "nederlabTitleProfile",
      "NLCore_NLIdentification_nederlabID": "c277847b-1264-4980-9b94-4350f5c43056",
      "NLTitle_title": "Notificatie."
    },
 */
public class Document 
{
	public String NLCore_NLIdentification_nederlabID;
	public String NLTitle_title;
	public String NLTitle_yearOfPublicationMin;
	public String NLTitle_yearOfPublicationMax;
	public String NLProfile_name;
	public String NLCore_NLAdministrative_sourceCollection; // of NLCore_NLExternalReference_collectionName
	
	public Map<String,String> getMetadata()
	{
		Map<String,String>  m = new ConcurrentHashMap<>();
		
		List<Field> publicFields = new ArrayList<>();
		Field[] allFields = this.getClass().getDeclaredFields();
		for (Field field : allFields) {
		    if (Modifier.isPublic(field.getModifiers())) 
		    {
		    	publicFields.add(field);
		    	try {
					m.put(field.getName(), field.get(this).toString());
				} catch (IllegalArgumentException | IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		    }
		}
		return m;
	}
}
