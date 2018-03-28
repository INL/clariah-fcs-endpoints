package org.ivdnt.fcs.endpoint.nederlab.client;

import java.util.Arrays;
import java.util.List;

public class NederlabConstants {

	public static String NEDERLAB_URL = "http://www.nederlab.nl/testbroker/search/";

	public static String DEFAULT_SERVER = NEDERLAB_URL;
	public static List<String> NEDERLAB_EXTRA_RESPONSE_FIELDS= Arrays.asList("NLCore_NLIdentification_versionID", "NLCore_NLIdentification_editorialCode");
	// Example of field which is accepted, but not answered: NLDependentTitle_title
	// Example of field which is not accepted: NLTitle_NLLocalization_localizationProvince

}
