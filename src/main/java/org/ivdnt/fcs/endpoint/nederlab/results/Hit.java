package org.ivdnt.fcs.endpoint.nederlab.results;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.ivdnt.fcs.endpoint.nederlab.objectmapper.Document;
import org.ivdnt.fcs.endpoint.nederlab.objectmapper.Token;
import org.ivdnt.fcs.endpoint.nederlab.objectmapper.TokenProperty;
import org.ivdnt.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class stores the hits of a Nederlab query, as part of the Nederlab
 * results
 * 
 * @author jesse
 * 
 *         TODO: als s bij de output zit, gaan de offsets fout
 */
public class Hit {

	// logger
	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	private int startPosition;
	private int endPosition;
	private List<Token> tokens = new ArrayList<>();
	private String documentKey = null;
	private Document document;
	private Set<String> knownPrefixes = new HashSet<String>();

	// NOT IN USE
	// private List<String> properties = new ArrayList<String>();

	/**
	 * Hit constructor
	 * 
	 * This constructor takes an unordered list of tokens, sorts this list straight
	 * away, and store it in a new instance of this class.
	 * 
	 * @param unordered a list of topen properties
	 *            input parameter is an unordered list of tokens. Each of them
	 *            represents a part of the Nederlab JSON response:
	 * 
	 *            { "mtasId": 110, "prefix": "t", = this indicates what the 'value'
	 *            key is about... prefix 't' means that 'value' is a token, prefix
	 *            'pos' means that 'value' is the part-of-speech tag of the token,
	 *            prefix 'lemma' means that 'value' is the lemma of the token
	 *            "value": "en", = a word [when prefix='t'/'lemma'] or a pos-tag
	 *            [when prefix='pos'] "positionStart": 14, "positionEnd": 14,
	 *            "parentMtasId": 128 },
	 * 
	 * 
	 */
	public Hit(List<TokenProperty> unordered) {
		Map<Integer, Token> m = new ConcurrentHashMap<>();

		for (TokenProperty tp : unordered) {
			// instantiate a new Token object
			// for the current start position

			Token t = m.get(tp.getPositionStart());
			if (t == null) {
				t = new Token();
				t.setStartPosition(tp.getPositionStart());
				m.put(tp.getPositionStart(), t);
			}

			// In the Nederlab Json reponse, tokens are to be found
			// in 3 flavours within the very same list:

			// - 't' prefix, meaning we have a token (=a wordform)
			// - 'pos' prefix, meaning we have the part-of-speech of the token,
			// - 'lemma' prefix, meaning we have the lemma of the token

			// So, if the current set of tokenProperties
			// has a 't' prefix, the token is a 'content token'
			//

			if (tp.getPrefix().equals("t"))
				t.setContentToken(true);

			this.knownPrefixes.add(tp.getPrefix());
			t.getTokenProperties().add(tp);
		}

		this.addAndSortTokens(m.values());

	}

	// ------------------------------------------------------------------------
	// getters

	public void addAndSortTokens(Collection<Token> tokens) {
		// add tokens to our list of tokens
		// and sort them according to their start index (= same order as in the corpus)

		this.tokens.addAll(tokens);
		Collections.sort(this.tokens, (t1, t2) -> Integer.compare(t1.getStartPosition(), t2.getStartPosition()));
	}

	public Document getDocument() {
		return document;
	}

	public String getDocumentKey() {
		return documentKey;
	}

	public int getHitEnd() {
		return this.endPosition;
	}

	public int getHitStart() {
		return this.startPosition;
	}

	// ------------------------------------------------------------------------
	// setters

	public List<Token> getTokens() {
		return this.tokens;
	}

	public void setDocument(Document document) {
		this.document = document;
	}

	public void setDocumentKey(String documentKey) {
		this.documentKey = documentKey;
	}

	public void setHitEnd(int endPosition) {
		this.endPosition = endPosition;
	}

	public void setHitStart(int startPosition) {
		this.startPosition = startPosition;
	}

	// ------------------------------------------------------------------------

	public org.ivdnt.fcs.results.Kwic toKwic() {
		org.ivdnt.fcs.results.Kwic kwic = new org.ivdnt.fcs.results.Kwic();

		kwic.setMetadata(document.getMetadata());

		kwic.addTokenPropertyNames(this.knownPrefixes);
		kwic.setHitStart(this.getHitStart());
		kwic.setHitEnd(this.getHitEnd() + 1); // HM nog even naar kijken, oogt niet helemaal lekker zo

		// System.err.println("Current hit to Kwic: " + this.toString());
		this.knownPrefixes.forEach(pref -> {
			List<String> content = this.tokens.stream().map(t -> t.getProperty(pref)).collect(Collectors.toList());
			kwic.setTokenProperties(pref, content);
		});
		return kwic;
	}

	public String toString() {
		List<String> lines = tokens.stream().map(t -> t.toString()).collect(Collectors.toList());
		return "(" + this.startPosition + "-" + this.endPosition + ") " + this.document.getField("NLTitle_title") + "\n"
				+ StringUtils.join(lines, "; ");
	}
}
