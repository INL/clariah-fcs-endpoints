/**
 *
 * @license http://www.gnu.org/licenses/gpl-3.0.txt
 *  GNU General Public License v3
 */
package se.gu.spraakbanken.fcs.endpoint.korp;

import java.net.URI;
import java.util.NoSuchElementException;
import java.util.List;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import eu.clarin.sru.server.SRUConstants;
import eu.clarin.sru.server.SRUDiagnostic;
import eu.clarin.sru.server.SRUDiagnosticList;
import eu.clarin.sru.server.SRUException;
import eu.clarin.sru.server.SRURequest;
import eu.clarin.sru.server.SRUResultCountPrecision;
import eu.clarin.sru.server.SRUSearchResultSet;
import eu.clarin.sru.server.SRUServerConfig;
import eu.clarin.sru.server.fcs.AdvancedDataViewWriter;
import eu.clarin.sru.server.fcs.Constants;
import eu.clarin.sru.server.fcs.XMLStreamWriterHelper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.gu.spraakbanken.fcs.endpoint.korp.cqp.SUCTranslator;
import se.gu.spraakbanken.fcs.endpoint.korp.data.json.pojo.info.CorporaInfo;
import se.gu.spraakbanken.fcs.endpoint.korp.data.json.pojo.query.Kwic;
import se.gu.spraakbanken.fcs.endpoint.korp.data.json.pojo.query.Match;
import se.gu.spraakbanken.fcs.endpoint.korp.data.json.pojo.query.Query;
import se.gu.spraakbanken.fcs.endpoint.korp.data.json.pojo.query.Token;

/**
 * A result set of a <em>searchRetrieve</em> operation. It it used to iterate
 * over the result set and provides a method to serialize the record in the
 * requested format.
 * <p>
 * A <code>SRUSearchResultSet</code> object maintains a cursor pointing to its
 * current record. Initially the cursor is positioned before the first record.
 * The <code>next</code> method moves the cursor to the next record, and because
 * it returns <code>false</code> when there are no more records in the
 * <code>SRUSearchResultSet</code> object, it can be used in a
 * <code>while</code> loop to iterate through the result set.
 * </p>
 * <p>
 * This class needs to be implemented for the target search engine.
 * </p>
 *
 * @see <a href="http://www.loc.gov/standards/sru/specs/search-retrieve.html">
 *      SRU Search Retrieve Operation</a>
 */
public class KorpSRUSearchResultSet extends SRUSearchResultSet {

    SRUServerConfig serverConfig = null;
    SRURequest request = null;

    private boolean incremental = false;
    private String query;
    private CorporaInfo corporaInfo;
    private Query resultSet;
    private String resultSetId = null;
    private int currentRecordCursor = 0;
    private int currentMaxRecords = 250;
    private int currentPageMaxRecords = 100;
    private int startRecord = 1;
    private int maximumRecords = 1000;
    private int recordCount; //startRecord + currentPageMaxRecords
    // XMLStreamWriterHelper.FCS_NS private!
    public static final String CLARIN_FCS_RECORD_SCHEMA = "http://clarin.eu/fcs/resource";
    private static Logger LOG = LoggerFactory.getLogger(KorpSRUSearchResultSet.class);

    /**
     * Constructor.
     *
     * @param serverConfig 
     *            the {@link SRUServerConfig} from the aggregator.
     * @param request 
     *            the SRU/CQL request {@link SRURequest} from the aggregator.
     * @param diagnostics
     *            the SRUDiagnosticList {@link SRUDiagnosticList} from the aggregator.
     * @param resultSet 
     * The Query instance with the resultSet. 
     * @param query 
     * The original query. 
     * @param corporaInfo 
     * The coproraInfo instance with the features and capabilities of each corpus. 
     *
     */
    protected KorpSRUSearchResultSet(SRUServerConfig serverConfig, SRURequest request, SRUDiagnosticList diagnostics, final Query resultSet, final String query, final CorporaInfo corporaInfo) {
        super(diagnostics);
	this.serverConfig = serverConfig;
	this.request = request;
	this.resultSet = resultSet;
	this.query = query;
	this.corporaInfo = corporaInfo;
	
	startRecord = (request.getStartRecord() < 1) ? 1 : request.getStartRecord();
	currentRecordCursor = startRecord - 1;
	maximumRecords = startRecord - 1 + request.getMaximumRecords();
	recordCount = request.getMaximumRecords();
    }

    protected KorpSRUSearchResultSet(SRUServerConfig serverConfig, SRUDiagnosticList diagnostics, final Query resultSet, final String query, final CorporaInfo corporaInfo) {
        super(diagnostics);
	this.serverConfig = serverConfig;
	this.resultSet = resultSet;
	this.query = query;
	this.corporaInfo = corporaInfo;
	this.maximumRecords = 250;
	this.currentRecordCursor = startRecord - 1;
	this.recordCount = 250;
    }

    /**
     * The number of records matched by the query. If the query fails this must
     * be 0. If the search engine cannot determine the total number of matched
     * by a query, it must return -1.
     *
     * @return the total number of results or 0 if the query failed or -1 if the
     *         search engine cannot determine the total number of results
     */
    public int getTotalRecordCount() {
	if (resultSet != null) {
	    return resultSet.getHits();
	}
	return -1;
    }

    /**
     * The number of records matched by the query but at most as the number of
     * records requested to be returned (maximumRecords parameter). If the query
     * fails this must be 0.
     *
     * @return the number of results or 0 if the query failed
     */
    public int getRecordCount() {
	if (resultSet != null && resultSet.getHits() > -1) {
	    return resultSet.getHits() < maximumRecords ? resultSet.getHits() : maximumRecords;
	}
	return 0;
    }

    /**
     * The result set id of this result. the default implementation returns
     * <code>null</code>.
     *
     * @return the result set id or <code>null</code> if not applicable for this
     *         result
     */
    public String getResultSetId() {
        return resultSetId;
    }

    /**
     * The result set time to live. In SRU 2.0 it will be serialized as
     * <code>&lt;resultSetTTL&gt;</code> element; in SRU 1.2 as
     * <code>&lt;resultSetIdleTime&gt;</code> element. The default implementation
     * returns <code>-1</code>.
     *
     * @return the result set time to live or <code>-1</code> if not applicable for
     *         this result
     */
    public int getResultSetTTL() {
        return -1;
    }

    /**
     * (SRU 2.0) Indicate the accuracy of the result count reported by total
     * number of records that matched the query. Default implementation returns
     * <code>null</code>.
     *
     * @see SRUResultCountPrecision
     * @return the result count precision or <code>null</code> if not applicable
     *         for this result
     */
    public SRUResultCountPrecision getResultCountPrecision() {
        return SRUResultCountPrecision.EXACT;
    }

    /**
     * The record schema identifier in which the records are returned
     * (recordSchema parameter).
     *
     * @return the record schema identifier
     */
    public String getRecordSchemaIdentifier() {
	return request.getRecordSchemaIdentifier() != null ? request.getRecordSchemaIdentifier() : CLARIN_FCS_RECORD_SCHEMA;
    }

    /**
     * Moves the cursor forward one record from its current position. A
     * <code>SRUSearchResultSet</code> cursor is initially positioned before the
     * first record; the first call to the method <code>next</code> makes the
     * first record the current record; the second call makes the second record
     * the current record, and so on.
     * <p>
     * When a call to the <code>next</code> method returns <code>false</code>,
     * the cursor is positioned after the last record.
     * </p>
     *
     * @return <code>true</code> if the new current record is valid;
     *         <code>false</code> if there are no more records
     * @throws SRUException
     *             if an error occurred while fetching the next record
     */
    public boolean nextRecord() throws SRUException {
	if (currentRecordCursor < Math.min(resultSet.getHits(), maximumRecords)) {
	    currentRecordCursor++;
	    return true;
	}
	return false;
    }


    protected int getCurrentRecordCursor() {
	return currentRecordCursor;

    }

    /**
     * An identifier for the current record by which it can unambiguously be
     * retrieved in a subsequent operation.
     *
     * @return identifier for the record or <code>null</code> if none is
     *         available
     * @throws NoSuchElementException
     *             result set is past all records
     */
    public String getRecordIdentifier() {
	return null;
    }

    /**
     * Get surrogate diagnostic for current record. If this method returns a
     * diagnostic, the writeRecord method will not be called. The default
     * implementation returns <code>null</code>.
     *
     * @return a surrogate diagnostic or <code>null</code>
     */
    public SRUDiagnostic getSurrogateDiagnostic() {
	if ((getRecordSchemaIdentifier() != null) &&
                !CLARIN_FCS_RECORD_SCHEMA.equals(getRecordSchemaIdentifier())) {
            return new SRUDiagnostic(
                    SRUConstants.SRU_RECORD_NOT_AVAILABLE_IN_THIS_SCHEMA,
                    getRecordSchemaIdentifier(),
                    "Record is not available in record schema \"" +
                            getRecordSchemaIdentifier() + "\".");
        }

        return null;
    }

    /**
     * Serialize the current record in the requested format.
     *
     * @param writer
     *            the {@link XMLStreamException} instance to be used
     * @throws XMLStreamException
     *             an error occurred while serializing the result
     * @throws NoSuchElementException
     *             result set past all records
     * @see #getRecordSchemaIdentifier()
     */
    @Override
    public void writeRecord(XMLStreamWriter writer)
            throws XMLStreamException {
        AdvancedDataViewWriter helper =
                new AdvancedDataViewWriter(AdvancedDataViewWriter.Unit.ITEM);
        URI wordLayerId = URI.create("http://spraakbanken.gu.se/ns/fcs/layer/word");
        URI lemmaLayerId = URI.create("http://spraakbanken.gu.se/ns/fcs/layer/lemma");
        URI posLayerId = URI.create("http://spraakbanken.gu.se/ns/fcs/layer/pos");

	Kwic kwic = resultSet.getKwic().get(currentRecordCursor - startRecord);
	List<Token> tokens = kwic.getTokens();
	Match match = kwic.getMatch();
	String corpus = kwic.getCorpus();

	XMLStreamWriterHelper.writeStartResource(writer, corpus + "-" + match.getPosition(), null);
        XMLStreamWriterHelper.writeStartResourceFragment(writer, null, null);

	long start = 1;
	if (match.getStart() != 1) {
            for (int i = 0; i < match.getStart(); i++) {
		long end = start + tokens.get(i).getWord().length();
                helper.addSpan(wordLayerId, start, end, tokens.get(i).getWord());
		try {
		    helper.addSpan(posLayerId, start, end, SUCTranslator.fromSUC(tokens.get(i).getMsd()).get(0));
		} catch (SRUException se) {}
		helper.addSpan(lemmaLayerId, start, end, tokens.get(i).getLemma());
                start = end + 1;
            }
        }

        for (int i = match.getStart(); i < match.getEnd(); i++) {
	    long end = start + tokens.get(i).getWord().length();
            helper.addSpan(wordLayerId, start, end, tokens.get(i).getWord(), 1);
	    try {
		helper.addSpan(posLayerId, start, end, SUCTranslator.fromSUC(tokens.get(i).getMsd()).get(0), 1);
	    } catch (SRUException se) {}
	    helper.addSpan(lemmaLayerId, start, end, tokens.get(i).getLemma(), 1);
	    start = end + 1;
        }

        if (tokens.size() > match.getEnd()) {
            for (int i = match.getEnd(); i < tokens.size(); i++) {
		long end = start + tokens.get(i).getWord().length();
                helper.addSpan(wordLayerId, start, end, tokens.get(i).getWord());
		try {
		    helper.addSpan(posLayerId, start, end, SUCTranslator.fromSUC(tokens.get(i).getMsd()).get(0));
	    } catch (SRUException se) {}
		helper.addSpan(lemmaLayerId, start, end, tokens.get(i).getLemma());
                start = end + 1;
            }
        }

        helper.writeHitsDataView(writer, wordLayerId);
	if (request == null || request.isQueryType(Constants.FCS_QUERY_TYPE_FCS)) {
            helper.writeAdvancedDataView(writer);
        }

        XMLStreamWriterHelper.writeEndResourceFragment(writer);
        XMLStreamWriterHelper.writeEndResource(writer);

    }

    /**
     * Check, if extra record data should be serialized for the current record.
     * The default implementation returns <code>false</code>.
     *
     * @return <code>true</code> if the record has extra record data
     * @throws NoSuchElementException
     *             result set is already advanced past all records
     * @see #writeExtraResponseData(XMLStreamWriter)
     */
    public boolean hasExtraRecordData() {
        return false;
    }


    /**
     * Serialize extra record data for the current record. A no-op default
     * implementation is provided for convince.
     *
     * @param writer
     *            the {@link XMLStreamException} instance to be used
     * @throws XMLStreamException
     *             an error occurred while serializing the result extra data
     * @throws NoSuchElementException
     *             result set past already advanced past all records
     * @see #hasExtraRecordData()
     */
    public void writeExtraRecordData(XMLStreamWriter writer)
            throws XMLStreamException {
    }

}
