package clariah.fcs;
import clariah.fcs.*;
import java.util.*;

public class ResultSet 
{
	public Query query;
	public int startPosition;
	public int maximumRecords;
	public int totalNumberOfResults;
	public List<Kwic> hits = new ArrayList<>();
    public List<Document> documents = new ArrayList<>();
	
    public String toString() 
	{
		String s = String.format("Resultset(query=%s, size=%d)", query.toString(), hits.size());
		return s;	
	}
}
