package org.ivdnt.fcs.endpoint.nederlab.objectmapper;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.ivdnt.fcs.endpoint.nederlab.client.NederlabClient;
import org.ivdnt.fcs.endpoint.nederlab.results.Hit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HitIterator implements Iterable<Hit>, Iterator<Hit> {

	// logger
	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	List<Hit> currentPortion = new ArrayList<>();
	int position;
	int n = 0; // ToDo
	int portionSize = 500;
	NederlabClient nederlabClient;
	String CQL;
	boolean done = false;

	public HitIterator(NederlabClient client, String CQL) {
		this.nederlabClient = client;
		this.CQL = CQL;
	}

	@Override
	public boolean hasNext() {
		if (done) {
			return false;
		}
		if (position < currentPortion.size())
			return true;
		else {
			int k = nextPortion();
			if (k > 0)
				return true;
			else {
				done = true;
				return false;
			}
		}
	}

	@Override
	public Iterator<Hit> iterator() {
		return this;
	}

	@Override
	public Hit next() {
		if (done) {
			return null;
		}
		if (position < currentPortion.size())
			return currentPortion.get(position++);
		else {
			int k = nextPortion();
			if (k > 0)
				return currentPortion.get(position++);
			else {
				done = true;
				return null;
			}
		}
	}

	private int nextPortion() {
		position = 0;
		currentPortion = nederlabClient.doSearch(CQL, n, portionSize).getResults();

		logger.info("retrieved next portion at " + n + "  size= " + currentPortion.size());

		n += currentPortion.size();
		return currentPortion.size();
	}

}
