/**
 *
 * @license http://www.gnu.org/licenses/gpl-3.0.txt
 *  GNU General Public License v3
 */
package se.gu.spraakbanken.fcs.endpoint.korp;

import eu.clarin.sru.server.SRUConfigException;
import eu.clarin.sru.server.SRUDiagnosticList;
import eu.clarin.sru.server.SRUException;
import eu.clarin.sru.server.SRUQueryParserRegistry;
import eu.clarin.sru.server.SRURequest;
import eu.clarin.sru.server.SRUScanResultSet;
import eu.clarin.sru.server.SRUExplainResult;
import eu.clarin.sru.server.SRUServerConfig;


/**
 * A result set of an <em>explain</em> operation. A database implementation may
 * use it implement extensions to the SRU protocol, i.e. providing
 * extraResponseData.
 *
 * <p>
 * This class needs to be implemented for the target data source.
 * </p>
 *
 * @see <a href="http://www.loc.gov/standards/sru/specs/explain.html">SRU
 *      Explain Operation </a>
 */
public abstract class KorpSRUExplainResult extends SRUExplainResult {

    /**
     * Constructor.
     *
     * @param diagnostics
     *            an instance of a SRUDiagnosticList
     * @see SRUDiagnosticList
     */
    protected KorpSRUExplainResult(SRUDiagnosticList diagnostics) {
        super(diagnostics);
    }

} // class SRUExplainResult
