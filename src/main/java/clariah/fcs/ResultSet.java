package clariah.fcs;
import clariah.fcs.*;
import java.util.*;

public class ResultSet 
{
	public Query query;
	public int startPosition;
	public int maximumRecords;
	public int totalNumberOfResults;
	public List<Kwic> hits;

	public String toString() 
	{
		return hits.toString();		
	}
}
