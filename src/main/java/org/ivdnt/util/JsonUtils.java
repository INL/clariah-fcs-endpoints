package org.ivdnt.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class JsonUtils {

	/**
	 * Get JSON object out of InputStream
	 * 
	 * @param is
	 * @return JSON object
	 * @throws IOException IO exception
	 * @throws ParseException parse exception
	 */
	public static JSONObject getJsonFromStream(InputStream is) throws IOException, ParseException {

		String line;
		JSONParser parser = new JSONParser();
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		StringBuilder b = new StringBuilder();
		while ((line = br.readLine()) != null) {
			b.append(line);
		}

		Object o = parser.parse(b.toString());
		return (JSONObject) o;
	}

	/**
	 * Get value of a given key out of the JSON object
	 * 
	 * @param context context object
	 * @param pname property name
	 * @return values
	 */
	public static List<String> getProperty(JSONObject context, String pname) {
		List<String> list = new ArrayList<String>();
		JSONArray words = (JSONArray) context.get(pname);
		for (int i = 0; i < words.size(); i++) {
			list.add((String) words.get(i));
		}
		return list;
	}

}
