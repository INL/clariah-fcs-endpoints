package org.ivdnt.fcs.endpoint.nederlab.stuff;
import java.util.*;

public class HitIterator implements Iterable<Hit>, Iterator<Hit> 
{
	List<Hit> currentPortion = new ArrayList<>();
	int position;
	int n=0; // ToDo
	int portionSize = 500;
	NederlabClient client;
	String CQL;
	boolean done = false;

	public HitIterator(NederlabClient client, String CQL)
	{
		this.client = client;
		this.CQL = CQL;
	}
	
	private int nextPortion()
	{
		position = 0;
		currentPortion = client.getResults(CQL, n, portionSize);
		System.err.println("retrieved next portion at " + n + "  size= " + currentPortion.size());
		n += currentPortion.size();
		return currentPortion.size();
	}

	@Override
	public Iterator<Hit> iterator() 
	{
		// TODO Auto-generated method stub
		return this;
	}

	@Override
	public boolean hasNext() 
	{
		// TODO Auto-generated method stub
		if (done)
		{
			return false;
		}
		if (position < currentPortion.size())
			return true;
		else
		{
			int k = nextPortion();
			if (k > 0)
				return true;
			else
			{
				done = true;
				return false;
			}
		}
	}

	@Override
	public Hit next() 
	{
		// TODO Auto-generated method stub
		if (done)
		{
			return null;
		}
		if (position < currentPortion.size())
			return currentPortion.get(position++);
		else
		{
			int k = nextPortion();
			if (k > 0)
				return currentPortion.get(position++);
			else
			{
				done = true;
				return null;
			}
		}
	}

}
