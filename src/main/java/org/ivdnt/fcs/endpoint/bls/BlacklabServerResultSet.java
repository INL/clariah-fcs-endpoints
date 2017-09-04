package org.ivdnt.fcs.endpoint.bls;
import clariah.fcs.*;
import java.util.*;

public class BlacklabServerResultSet extends clariah.fcs.ResultSet
{
	BlacklabServerQuery query;
	int startPosition;
	int maximumRecords;
	
	List<Kwic> hits;
}
