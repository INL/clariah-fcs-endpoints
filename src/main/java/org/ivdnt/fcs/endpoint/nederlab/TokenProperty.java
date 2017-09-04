package org.ivdnt.fcs.endpoint.nederlab;

import java.util.Map;

public class TokenProperty 
{
	public int mtasId;
	public String prefix;
	public String value;
	public String valueposition;
	public  int positionStart;
	public int positionEnd;
	public int parentMtasId;
	
	public void setValuesFromMap(Map m)
	{
		//this.mtasId = m.get("mtasId");
	}
};

