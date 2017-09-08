package clariah.fcs.mapping;
import java.util.*;

import org.ivdnt.util.StringUtils;

public class Feature
{
	public String name;
	public Set<String> values;
	public static String multiValueSeparator="|";

	public Feature(String name, Set<String> values)
	{
		this.name = name;
		this.values = values;
	}

	public String asCQL()
	{
		String vals = StringUtils.join(values, "|");
		return String.format("%s='%s'", name, vals);
	}

	public String asRegexInTag(String CQLTagField)
	{
		String vals = StringUtils.join(values, "|");
		if (name.equals(CQLTagField))
			return String.format("%s='^(%s).*'",name,vals);
		else
			return String.format("%s='.*%s\\s*=(%s).*'", CQLTagField, name, vals);
	}
}
