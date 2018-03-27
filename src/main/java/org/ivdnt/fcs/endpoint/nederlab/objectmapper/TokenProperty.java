package org.ivdnt.fcs.endpoint.nederlab.objectmapper;

import java.util.Map;

/**
 * This class is used by the Json ObjectMapper so as deserialize the JSON string
 * response from Nederlab into Java objects (here: Document, Token,
 * TokenPropery, ...)
 * 
 * A hit normally looks like this:
 * 
 * { "mtasId": 110, "prefix": "t", = this indicates what the 'value' key is
 * about... prefix 't' means that 'value' is a token, prefix 'pos' means that
 * 'value' is the part-of-speech tag of the token, prefix 'lemma' means that
 * 'value' is the lemma of the token "value": "en", = a word [when
 * prefix='t'/'lemma'] or a pos-tag [when prefix='pos'] "positionStart": 14,
 * "positionEnd": 14, "parentMtasId": 128 },
 * 
 * (see full example of Nederlab response in NederlabClient.java)
 * 
 * 
 * 
 * @author jesse, peter
 *
 */
public class TokenProperty {
	/*// see explanation above

	private String prefix; // this can have value 't'=token, or 'pos', or 'lemma'
	private String value;
	private int positionStart;
	private int positionEnd;

	// these variables are not processed in the java code,
	// but they are needed for the Json ObjectMapper to work properly
	// (so it's able to map a Json property to a Java variable)
	// so don't remove these variables...

	public int mtasId;
	public String valueposition;
	public int parentMtasId;*/
	
	private Map<String,Object> data;
	
	public TokenProperty(Map<String,Object> obj) {
		// Map values stay Object (text or number): no conversion to string performed
		data = obj;
	}
	
	public Object getField(String fieldName) {
		if (!data.containsKey(fieldName)) {
			throw new NullPointerException("Key " + fieldName + " does not exist in TokenProperty object.");
		}
		return data.get(fieldName);
	}

	// -----------------------------------------------------------------------------
	// getters

	public String getPrefix() {
		return (String) getField("prefix");
	}

	public String getValue() {
		return (String) getField("value");
	}

	public int getPositionStart() {
		return (int) getField("positionStart");
	}

	public int getPositionEnd() {
		return (int) getField("positionEnd");
	}


};
