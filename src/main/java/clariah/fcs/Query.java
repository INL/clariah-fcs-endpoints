package clariah.fcs;

public abstract class Query 
{
	public static final String defaultServer = "http://localhost:8080/blacklab-server-1.6.0/";
	public String server = defaultServer;
	
	public String cqp = "[word='paard']";
	public String corpus = "ezel";
	
	public int startPosition;
	public int maximumResults;
	
	public Query(String server, String corpus, String cqp)
	{
		this.server = server;
		this.corpus= corpus;
		this.cqp = cqp;
	}
	
	
	public abstract  ResultSet execute() throws Exception;
}