package org.ivdnt.fcs.endpoint.nederlab.objectmapper;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is used by the Json ObjectMapper so as deserialize the JSON string
 * response from Nederlab into Java objects (here: Document, Token,
 * TokenPropery, ...)
 * 
 * @author fannee
 *
 */
public class Token {
	private int startPosition;
	private int endPosition;
	private List<TokenProperty> tokenProperties = new ArrayList<>();
	private boolean contentToken = false;

	// -----------------------------------------------------------------
	// getters

	public int getEndPosition() {
		return this.endPosition;
	}

	public String getProperty(String propertyName) {
		for (TokenProperty tp : this.tokenProperties) {
			if (tp.getPrefix().equals(propertyName))
				return tp.getValue();
		}
		return "";
	}

	public int getStartPosition() {
		return this.startPosition;
	}

	public List<TokenProperty> getTokenProperties() {
		return this.tokenProperties;
	}

	public boolean isContentToken() {
		return this.contentToken;
	}

	// -----------------------------------------------------------------
	// setters

	public void setContentToken(boolean contentToken) {
		this.contentToken = contentToken;
	}

	public void setEndPosition(int endPosition) {
		this.endPosition = endPosition;
	}

	public void setStartPosition(int startPosition) {
		this.startPosition = startPosition;
	}

	public void setTokenProperties(List<TokenProperty> tokenProperties) {
		this.tokenProperties = tokenProperties;
	}

	// -----------------------------------------------------------------

	public String toString() {
		this.tokenProperties.sort((p1, p2) -> p1.getPrefix().compareTo(p2.getPrefix()));
		String r = this.startPosition + ":";
		for (TokenProperty tp : tokenProperties) {
			r += tp.getPrefix() + "=" + tp.getValue() + " ";
		}
		return r;
	}

	// -----------------------------------------------------------------
}
