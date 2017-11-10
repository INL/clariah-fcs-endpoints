package clariah.fcs;


import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Document 
{
	public String pid;
	public Map<String,String> metadata = new ConcurrentHashMap<>();
}
