package org.ivdnt.fcs.results;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This class is used to store FCS documents as part the the ResultSet
 * 
 * @author jesse
 *
 */
public class Document {
	private String pid;
	private Map<String, String> metadata = new ConcurrentHashMap<>();

	// ---------------------------------------------------------------------------
	// getters

	public void addMetadata(String a, String b) {
		this.metadata.put(a, b);
	}

	public Map<String, String> getMetadata() {
		return metadata;
	}

	// ---------------------------------------------------------------------------
	// setters

	public String getPid() {
		return pid;
	}

	public void setMetadata(Map<String, String> metadata) {
		this.metadata = metadata;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

}
