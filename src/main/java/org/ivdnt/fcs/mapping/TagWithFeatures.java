package org.ivdnt.fcs.mapping;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;

import org.ivdnt.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class parses parole-style tags, type NOU(number=sg), etc.
 * 
 * @author jesse
 */

public class TagWithFeatures extends FeatureConjunction {

	// logger
	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	private static final long serialVersionUID = 1L;

	public static void main(String[] args) {
		String t = "VRB(tense=past,number=sg|pl)";
		TagWithFeatures twf = TagWithFeatures.parseParoleStyleTag(t);
		logger.info(t + " " + twf.asCQL() + " " + twf.asRegexInTag());
	}

	public static TagWithFeatures parseParoleStyleTag(String tag) {
		TagWithFeatures t = new TagWithFeatures();

		// get the pos-tag (string part before the brackets, which contain the features)

		String[] a = tag.split("\\(");
		t.put("pos", a[0]);

		// get the features

		if (a.length > 1) {
			// get and split into separate features (part before the closing bracket)

			String rest = a[1].replaceAll("\\)", "");

			String[] featuresvalues = rest.split(",");
			for (String fplusv : featuresvalues) {
				// key = value

				String[] fv = fplusv.split("=");
				if (fv.length > 1) {
					String name = fv[0];
					String values = fv[1];
					for (String value : values.split(MappingConstants.MULTIVALUE_SEPARATOR)) {
						t.put(name, value);
					}
				}
			}
		}

		return t;
	}

	// --------------------------------------------------------------------
	// test only

	public String toString() {
		String pos = this.getJoinedValues("pos");

		List<String> l = new ArrayList<String>();
		for (String name : super.keySet()) {
			if (!name.equals("pos")) {
				l.add(name + "=" + getJoinedValues(name));
			}
		}
		return pos + "(" + StringUtils.join(l, ",") + ")";
	}
}