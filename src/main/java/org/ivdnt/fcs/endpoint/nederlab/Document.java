package org.ivdnt.fcs.endpoint.nederlab;
import java.util.HashMap;
import java.util.Map;
import java.util.*;
import java.lang.reflect.*;

/**
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
		Map<String,String>  m = new HashMap<>();
		m.put("aap", "aap");
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
