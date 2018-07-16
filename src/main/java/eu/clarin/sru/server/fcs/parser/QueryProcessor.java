package eu.clarin.sru.server.fcs.parser;

import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.stream.Collectors;

import org.ivdnt.fcs.mapping.ConversionEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is about converting a FSC-QL query (as a string or as a node) into
 * a CQP query. When doing so, the universal dependencies are also converted
 * into the tagset given as a parameter in the constructor.
 * 
 * Jesse: Bleuh dit moet je in package eu.clarin.sru.server.fcs.parser zetten,
 * anders kan je niks clonen. Bleurp.
 * 
 * @author jesse
 * 
 *
 */
public class QueryProcessor {

	// logger
	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	private ExpressionRewriter expressionRewriter;

	// ---------------------------------------------------------------------------------
	// constructors

	public QueryProcessor(ConversionEngine conversion) {
		this.expressionRewriter = new ExpressionConverter(conversion);
	}

	public QueryProcessor(ExpressionRewriter expressionRewriter) {
		this.expressionRewriter = expressionRewriter;
	}

	// ---------------------------------------------------------------------------------

	/**
	 * Rewrite a list of FSC-QL query nodes (which we have when dealing with AND,
	 * OR, etc.; that consists of at least 2 nodes).
	 * 
	 * In particular convert the universal dependencies into the tag set given in
	 * the constructor
	 * 
	 * @param list
	 *            of nodes
	 * @return a list of rewritten nodes
	 */
	private List<QueryNode> mapRewrite(List<QueryNode> nodesList) {
		return nodesList.stream().map(

				node -> rewriteNode(node)

		).collect(Collectors.toList());
	}

	/**
	 * Rewrite a FSC-QL query string (in particular convert the universal
	 * dependencies) into the tag set given in the constructor
	 * 
	 * (When input is a node, use rewriteNode method instead)
	 * 
	 * @param cqp
	 *            String
	 * @return a rewritten node
	 */
	public QueryNode rewrite(String cqp) {
		QueryParser parser = new QueryParser();
		// convert the query string into a node
		QueryNode qn = null;
		try {
			qn = parser.parse(cqp);
		} catch (QueryParserException e) {
			throw new RuntimeException("Not able to parse query: " + cqp, e);
		}

		logger.info("qn.toString() = " + qn.toString());

		// now rewrite the node!
		return rewriteNode(qn);
	}

	@SuppressWarnings("unused")
	private QueryNode rewriteExpression(Expression node) {
		Expression e = new Expression(node.getLayerQualifier(), node.getLayerIdentifier(), node.getOperator(),
				node.getRegexValue(), node.getRegexFlags());
		return e;
	}

	// ------------------------------------------------------------------------
	// special cases

	private QueryNode rewriteExpressionAnd(ExpressionAnd node) {
		return new ExpressionAnd(mapRewrite(node.getChildren()));
	}

	private QueryNode rewriteExpressionGroup(ExpressionGroup node) {
		return new ExpressionGroup(rewriteNode(node.getFirstChild()));
	}

	private QueryNode rewriteExpressionNot(ExpressionNot node) {
		return new ExpressionNot(rewriteNode(node.getFirstChild()));
	}

	private QueryNode rewriteExpressionOr(ExpressionOr node) {

		return new ExpressionOr(mapRewrite(node.getOperands()));
	}

	private QueryNode rewriteExpressionWildcard(ExpressionWildcard node) {
		return new ExpressionWildcard();
	}

	/**
	 * Rewrite a FSC-QL query node (in particular convert the universal
	 * dependencies) into the tag set given in the constructor
	 * 
	 * (When input is a string, use rewrite method instead)
	 * 
	 * @param node
	 * @return a rewritten node
	 */
	private QueryNode rewriteNode(QueryNode node) {
		QueryNode n1;
		if (node instanceof QueryDisjunction) {
			n1 = rewriteQueryDisjunction((QueryDisjunction) node);
		} else if (node instanceof QueryGroup) {
			n1 = rewriteQueryGroup((QueryGroup) node);
		} else if (node instanceof QuerySegment) {
			n1 = rewriteQuerySegment((QuerySegment) node);
		} else if (node instanceof QuerySequence) {
			n1 = rewriteQuerySequence((QuerySequence) node);
		} else if (node instanceof ExpressionAnd) {
			n1 = rewriteExpressionAnd((ExpressionAnd) node);
		} else if (node instanceof Expression) {
			n1 = this.expressionRewriter.rewriteExpression((Expression) node); // this is where the tags/features are
																				// converted
		} else if (node instanceof ExpressionGroup) {
			n1 = rewriteExpressionGroup((ExpressionGroup) node);
		} else if (node instanceof ExpressionNot) {
			n1 = rewriteExpressionNot((ExpressionNot) node);
		} else if (node instanceof ExpressionOr) {
			n1 = rewriteExpressionOr((ExpressionOr) node);
		} else if (node instanceof ExpressionWildcard) {
			n1 = rewriteExpressionWildcard((ExpressionWildcard) node);
		} else if (node instanceof SimpleWithin) {
			n1 = rewriteSimpleWithin((SimpleWithin) node);
		} else if (node instanceof QueryWithWithin) {
			n1 = rewriteQueryWithWithin((QueryWithWithin) node);
		} else {
			throw new RuntimeException("unexpected node type: " + node.getNodeType());
		}

		return n1;
	}

	private QueryNode rewriteQueryDisjunction(QueryDisjunction node) {
		return new QueryDisjunction(mapRewrite(node.getChildren()));
	}

	private QueryNode rewriteQueryGroup(QueryGroup node) {
		return new QueryGroup(rewriteNode(node.getContent()), node.getMinOccurs(), node.getMaxOccurs());
	}

	private QueryNode rewriteQuerySegment(QuerySegment node) {
		return new QuerySegment(rewriteNode(node.getExpression()), node.getMinOccurs(), node.getMaxOccurs());
	}

	private QueryNode rewriteQuerySequence(QuerySequence node) {
		return new QuerySequence(mapRewrite(node.getChildren()));
	}

	private QueryNode rewriteQueryWithWithin(QueryWithWithin node) {
		return new QueryWithWithin(rewriteNode(node.getFirstChild()), rewriteNode(node.getChildren().get(1)));
	}

	private QueryNode rewriteSimpleWithin(SimpleWithin node) {
		SimpleWithin sw = new SimpleWithin(node.getScope());
		return sw;
	}
}
