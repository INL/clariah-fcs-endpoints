/**
 *
 * @license http://www.gnu.org/licenses/gpl-3.0.txt
 *  GNU General Public License v3
 */
package se.gu.spraakbanken.fcs.endpoint.korp.cqp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import eu.clarin.sru.server.SRUConstants;
import eu.clarin.sru.server.SRUException;

public class SUCTranslator {
    private static final Map<String, List<String>> TO_SUC = createToSuc();
    private static final Map<String, List<String>> TO_UD17 = createToUd17();

    private static Map<String, List<String>> createToSuc() {
	Map<String, List<String>> suc = new HashMap<String, List<String>>();
	suc.put("NOUN", Arrays.asList("NN"));
	suc.put("PROPN", Arrays.asList("PM"));
	suc.put("ADJ", Arrays.asList("JJ", "PC", "RO"));
	suc.put("VERB", Arrays.asList("VB", "PC"));
	suc.put("AUX", Arrays.asList("VB"));
	suc.put("NUM", Arrays.asList("RG", "RO")); // No RO?
	suc.put("PRON", Arrays.asList("PN", "PS", "HP", "HS")); // No PS, HS?
	suc.put("DET", Arrays.asList("DT", "HD", "HS", "PS"));
	suc.put("PART", Arrays.asList("IE"));
	suc.put("ADV", Arrays.asList("AB", "HA", "PL")); // No PL?
	suc.put("ADP", Arrays.asList("PL", "PP")); // No PL?
	suc.put("CCONJ", Arrays.asList("KN"));
	suc.put("SCONJ", Arrays.asList("SN"));
	suc.put("INTJ", Arrays.asList("IN"));
	suc.put("PUNCT", Arrays.asList("MAD", "MID", "PAD"));
	suc.put("X", Arrays.asList("UO"));
	return Collections.unmodifiableMap(suc);
    }

    private static Map<String, List<String>> createToUd17() {
	Map<String, List<String>> ud17 = new HashMap<String, List<String>>();
	// fixme! - check lemma/msd for toUd17
	ud17.put("NN", Arrays.asList("NOUN"));
	ud17.put("PM", Arrays.asList("PROPN"));
	ud17.put("VB", Arrays.asList("VERB", "AUX"));
	ud17.put("IE", Arrays.asList("PART"));
	ud17.put("PC", Arrays.asList("VERB")); // No ADJ?
	ud17.put("PL", Arrays.asList("PART")); // No ADV, ADP?
	ud17.put("PN", Arrays.asList("PRON"));
	ud17.put("PS", Arrays.asList("DET")); // No PRON?
	ud17.put("HP", Arrays.asList("PRON"));
	ud17.put("HS", Arrays.asList("DET")); // No PRON?
	ud17.put("DT", Arrays.asList("DET"));
	ud17.put("HD", Arrays.asList("DET"));
	ud17.put("JJ", Arrays.asList("ADJ"));
	ud17.put("AB", Arrays.asList("ADV"));
	ud17.put("HA", Arrays.asList("ADV"));
	ud17.put("KN", Arrays.asList("CCONJ"));
	ud17.put("SN", Arrays.asList("SCONJ"));
	ud17.put("PP", Arrays.asList("ADP"));
	ud17.put("RG", Arrays.asList("NUM"));
	ud17.put("RO", Arrays.asList("ADJ")); // No NUM?
	ud17.put("IN", Arrays.asList("INTJ"));
	// Could be any PoS, most probably a noun /ljo
	ud17.put("UO", Arrays.asList("X"));
	ud17.put("MAD", Arrays.asList("PUNCT"));
	ud17.put("MID", Arrays.asList("PUNCT"));
	ud17.put("PAD", Arrays.asList("PUNCT"));

	return Collections.unmodifiableMap(ud17);
    }

    /*
     * @param ud17Pos The UD-17 PoS code
     * @return A list of translated codes in SUC PoS.
     *
     */
    public static List<String> toSUC(final String ud17Pos) throws SRUException {
	List<String> res = null;
	
	res = TO_SUC.get(ud17Pos.toUpperCase());
	
	if (res == null) {
	    throw new SRUException(
				   SRUConstants.SRU_QUERY_SYNTAX_ERROR,
				   "unknown UD-17 PoS code in query: " + ud17Pos);
	}
	return res;
    }

    /*
     * @param sucPos The SUC PoS code
     * @return A list of translated codes in UD-17 PoS.
     *
     */
    public static List<String> fromSUC(final String sucPos) throws SRUException {
	List<String> res = null;
	int iop = sucPos.indexOf(".");
 
	res = TO_UD17.get((iop != -1 ? sucPos.substring(0, iop) : sucPos).toUpperCase());
	if (res == null) {
	    throw new SRUException(
				   SRUConstants.SRU_CANNOT_PROCESS_QUERY_REASON_UNKNOWN,
				   "unknown PoS code from search engine: " + (iop != -1 ? sucPos.substring(0, iop) : sucPos));
	}
	return res;
    }
}
