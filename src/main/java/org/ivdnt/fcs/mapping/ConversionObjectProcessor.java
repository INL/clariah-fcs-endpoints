package org.ivdnt.fcs.mapping;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 
 * @author Mathieu 3 nov 2017 in JSON gegoten
 * 
 *         Kijk ook naar
 *         https://raw.githubusercontent.com/proycon/folia/master/setdefinitions/frog-mbpos-cgn
 *         voor de feature namen in cgn tags
 */

public class ConversionObjectProcessor {

	private static ConcurrentHashMap<String, ConversionEngine> conversionsMap = new ConcurrentHashMap<String, ConversionEngine>();

	// --------------------------------------------------------------------------
	// setters

	public static void processConversionTable(String conversionName, ConversionObject jsonConfig) {

		// generate new conversion engine, containing mapping and such

		ConversionEngine conversionEngine = new ConversionEngine(jsonConfig.getFieldMapping(),
				jsonConfig.getFeatureMapping());

		conversionEngine.setUseFeatureRegex(jsonConfig.usesFeatureRegex());
		conversionEngine.setPosTagField(jsonConfig.getPosTagField());
		conversionEngine.setQuote(jsonConfig.getQuote());
		conversionEngine.setGrammaticalFeatures(jsonConfig.getGrammaticalFeatures());
		conversionEngine.setIncludeFeatureNameInRegex(jsonConfig.hasIncludedFeatureNameInRegex());
		conversionEngine.setName(conversionName);

		// store this new conversion map

		conversionsMap.put(conversionName, conversionEngine);
	}

	// --------------------------------------------------------------------------
	// getters

	public static ConversionEngine getConversionEngine(String name) {

		return conversionsMap.get(name);

	}

	public static ConcurrentHashMap<String, ConversionEngine> getConversionEngines() {

		return conversionsMap;

	}

}
