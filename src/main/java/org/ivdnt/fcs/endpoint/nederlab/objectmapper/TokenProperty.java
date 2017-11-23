package org.ivdnt.fcs.endpoint.nederlab.objectmapper;

/**
 * This class is used by the Json ObjectMapper
 * so as deserialize the JSON string response from Nederlab 
 * into Java objects (here: Document, Token, TokenPropery, ...)
 * 
 * A hit normally looks like this:
 * 
 *   {
 *   "mtasId": 110,
 *   "prefix": "t",		= this indicates what the 'value' key is about...
 *                        prefix 't' means that 'value' is a token, 
 *                        prefix 'pos' means that 'value' is the part-of-speech tag of the token, 
 *                        prefix 'lemma' means that 'value' is the lemma of the token
 *   "value": "en",		=    a word [when prefix='t'/'lemma'] 
 *                        or a pos-tag [when prefix='pos']
 *   "positionStart": 14,
 *   "positionEnd": 14,
 *   "parentMtasId": 128
 *   },
 *   
 *   (see full example of Nederlab response in NederlabClient.java)
 *   
 *   
 * 
 * @author jesse
 *
 */
public class TokenProperty 
{
	// see explanation above 
	
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
	public int parentMtasId;
	
	
	// -----------------------------------------------------------------------------
	// getters
	
	public String getPrefix() {
		return prefix;
	}
	
	public String getValue() {
		return value;
	}
	
	public int getPositionStart() {
		return positionStart;
	}
	
	public int getPositionEnd() {
		return positionEnd;
	}
	
	
	
	// -----------------------------------------------------------------------------
	// setters
	
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}
	
	public void setValue(String value) {
		this.value = value;
	}
	
	public void setPositionStart(int positionStart) {
		this.positionStart = positionStart;
	}
	
	public void setPositionEnd(int positionEnd) {
		this.positionEnd = positionEnd;
	}
	
	
};

