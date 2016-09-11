/**
 *
 * @license http://www.gnu.org/licenses/gpl-3.0.txt
 *  GNU General Public License v3
 */
package se.gu.spraakbanken.fcs.endpoint.korp;

import java.util.NoSuchElementException;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import eu.clarin.sru.server.SRUDiagnosticList;
import eu.clarin.sru.server.SRUException;
import eu.clarin.sru.server.SRUScanResultSet;

/**
 * A result set of a <em>scan</em> operation. It is used to iterate over the
 * term set and provides a method to serialize the terms.
 *
 * <p>
 * A <code>SRUScanResultSet</code> object maintains a cursor pointing to its
 * current term. Initially the cursor is positioned before the first term. The
 * <code>next</code> method moves the cursor to the next term, and because it
 * returns <code>false</code> when there are no more terms in the
 * <code>SRUScanResultSet</code> object, it can be used in a <code>while</code>
 * loop to iterate through the term set.
 * </p>
 * <p>
 * This class needs to be implemented for the target search engine.
 * </p>
 *
 * @see <a href="https://www.loc.gov/standards/sru/companionSpecs/scan.html"> SRU Scan
 *      Operation</a>
 */
public class KorpSRUScanResultSet extends SRUScanResultSet {

    /**
     * Constructor.
     *
     * @param diagnostics
     *            an instance of a SRUDiagnosticList.
     * @see SRUDiagnosticList
     */
    protected KorpSRUScanResultSet(SRUDiagnosticList diagnostics) {
        super(diagnostics);
    }


    /**
     * Moves the cursor forward one term from its current position. A result set
     * cursor is initially positioned before the first record; the first call to
     * the method <code>next</code> makes the first term the current term; the
     * second call makes the second term the current term, and so on.
     * <p>
     * When a call to the <code>next</code> method returns <code>false</code>,
     * the cursor is positioned after the last term.
     * </p>
     *
     * @return <code>true</code> if the new current term is valid;
     *         <code>false</code> if there are no more terms
     * @throws SRUException
     *             if an error occurred while fetching the next term
     */
    public boolean nextTerm() throws SRUException {
	return false;
    }


    /**
     * Get the current term exactly as it appears in the index.
     *
     * @return current term
     */
    public String getValue() {
	return new String("NOVALUETERM");
    }


    /**
     * Get the number of records for the current term which would be matched if
     * the index in the request's <em>scanClause</em> was searched with the term
     * in the <em>value</em> field.
     *
     * @return a non-negative number of records or
     *         <code>-1</code>, if the number is unknown.
     */
    public int getNumberOfRecords() {
	return -1;
    }


    /**
     * Get the string for the current term to display to the end user in place
     * of the term itself.
     *
     * @return display string or <code>null</code>
     */
    public String getDisplayTerm() {
	return new String("NODISPLAYTERM");
    }


    /**
     * Get the flag to indicate the position of the term within the complete
     * term list.
     *
     * @return position within term list or <code>null</code>
     */
    @Override
    public WhereInList getWhereInList() {
	return null;
    }


    /**
     * Check, if extra term data should be serialized for the current term. A
     * default implementation is provided for convenience and always returns
     * <code>false</code>.
     *
     * @return <code>true</code> if the term has extra term data
     * @throws NoSuchElementException
     *             term set is already advanced past all past terms
     * @see #writeExtraTermData(XMLStreamWriter)
     */
    public boolean hasExtraTermData() {
        return false;
    }


    /**
     * Serialize extra term data for the current term. A no-op default
     * implementation is provided for convince.
     *
     * @param writer
     *            the {@link XMLStreamException} instance to be used
     * @throws XMLStreamException
     *             an error occurred while serializing the term extra data
     * @throws NoSuchElementException
     *             result set already advanced past all terms
     * @see #hasExtraTermData()
     */
    public void writeExtraTermData(XMLStreamWriter writer)
            throws XMLStreamException {
    }

}
