package org.ivdnt.fcs.endpoint.nederlab.results;

import java.util.ArrayList;
import java.util.List;

/**
 * This class stores the results of a Nederlab query
 * 
 * @author jesse
 *
 */
public class NederlabResultSet {

	private List<Hit> results;
	private int totalNumberOfHits;

	// -----------------------------------------------------------------------------
	// constructors

	public NederlabResultSet() {

		this.totalNumberOfHits = 0;
		this.results = new ArrayList<>();
	}

	// -----------------------------------------------------------------------------
	// getters

	public void addResult(Hit oneHit) {
		this.results.add(oneHit);
	}

	public List<Hit> getResults() {
		return results;
	}

	// -----------------------------------------------------------------------------
	// setters

	public int getTotalNumberOfHits() {
		return totalNumberOfHits;
	}

	public void setResults(List<Hit> results) {
		this.results = results;
	}

	public void setTotalNumberOfHits(int totalNumberOfHits) {
		this.totalNumberOfHits = totalNumberOfHits;
	}

}
