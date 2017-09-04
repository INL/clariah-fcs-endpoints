package org.ivdnt.fcs.endpoint.nederlab;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class IO 
{
	public static String readStringFromFile(String fileName)
	{
		String contents;
		try
		{
		  FileReader r  = new FileReader(new File(fileName));
		  BufferedReader b = new BufferedReader(r);
		  String l;
		  contents = "";
		  while ((l = b.readLine()) != null)
		  {
			  contents += l + "\n";
		  }
		  b.close();
		  return contents;
		} catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}
}
