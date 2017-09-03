package se.gu.spraakbanken.fcs.endpoint.korp;
import clariah.fcs.*;
import java.util.*;

public class BlacklabServerResultSet 
{
	BlacklabServerQuery query;
	int startPosition;
	int maximumRecords;
	
	List<Kwic> hits;
}
