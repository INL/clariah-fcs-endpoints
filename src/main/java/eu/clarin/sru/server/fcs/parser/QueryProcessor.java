package eu.clarin.sru.server.fcs.parser;

import java.util.*;
import java.util.stream.Collectors;


import clariah.fcs.mapping.ConversionEngine;
import eu.clarin.sru.server.fcs.parser.Expression;
import eu.clarin.sru.server.fcs.parser.ExpressionAnd;
import eu.clarin.sru.server.fcs.parser.ExpressionGroup;
import eu.clarin.sru.server.fcs.parser.ExpressionNot;
import eu.clarin.sru.server.fcs.parser.ExpressionOr;
import eu.clarin.sru.server.fcs.parser.ExpressionWildcard;
import eu.clarin.sru.server.fcs.parser.QueryDisjunction;
import eu.clarin.sru.server.fcs.parser.QueryGroup;
import eu.clarin.sru.server.fcs.parser.QueryNode;
import eu.clarin.sru.server.fcs.parser.QueryNodeType;
import eu.clarin.sru.server.fcs.parser.QueryParser;
import eu.clarin.sru.server.fcs.parser.QuerySegment;
import eu.clarin.sru.server.fcs.parser.QuerySequence;
import eu.clarin.sru.server.fcs.parser.QueryVisitor;
import eu.clarin.sru.server.fcs.parser.SimpleWithin;

/**
 * This class is about converting a FSC-QL query (as a string or as a node) 
 * into a CQP query.
 * When doing so, the universal dependencies are also converted into the
 * tagset given as a parameter in the constructor.
 * 
 * Jesse:
 * Bleuh dit moet je in package eu.clarin.sru.server.fcs.parser zetten, anders 
 * kan je niks clonen. Bleurp.
 * 
 * @author jesse
 * 
 * TODO QueryWithWithin
 *
 */
public class QueryProcessor
{
	private ExpressionRewriter expressionRewriter;
	
	// ---------------------------------------------------------------------------------
	// constructors
	
	public QueryProcessor(ExpressionRewriter expressionRewriter)
	{
		this.expressionRewriter = expressionRewriter;
	}
	
	public QueryProcessor(ConversionEngine conversion)
	{
		this.expressionRewriter = new ExpressionConverter(conversion);
	}
	
	
	// ---------------------------------------------------------------------------------
	
	/**
	 * Rewrite a FSC-QL query string, and in particular convert the universal dependencies
	 * into the tag set given in the constructor
	 * 
	 * When input is a node, use rewriteNode method instead
	 * 
	 * @param cqp String
	 * @return a rewritten node
	 */
	public QueryNode rewrite(String cqp)
	{
		QueryParser parser = new QueryParser();
		try
		{
			// convert the query string into a node
			QueryNode qn = parser.parse(cqp);
			
			// now rewrite the node!
			return rewriteNode(qn);
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}
	
	
	/**
	 * Rewrite a FSC-QL query node, and in particular convert the universal dependencies
	 * into the tag set given in the constructor
	 * 
	 * When input is a string, use rewrite method instead
	 * 
	 * @param node
	 * @return a rewritten node
	 */
	private QueryNode rewriteNode(QueryNode node)
	{
		QueryNode n1;
		if (node instanceof QueryDisjunction) {
			n1=rewriteQueryDisjunction((QueryDisjunction) node);
		} else if (node instanceof QueryGroup) {
			n1=rewriteQueryGroup((QueryGroup) node);
		} else if (node instanceof QuerySegment) {
			n1=rewriteQuerySegment((QuerySegment) node);
		} else if (node instanceof QuerySequence) {
			n1=rewriteQuerySequence((QuerySequence) node);
		} else if (node instanceof ExpressionAnd) {
			n1=rewriteExpressionAnd((ExpressionAnd) node);
		} else if (node instanceof Expression) {
			n1=expressionRewriter.rewriteExpression( (Expression) node ); // this is where the tags/features are converted
		} else if (node instanceof ExpressionGroup) {
			n1=rewriteExpressionGroup((ExpressionGroup) node);
		} else if (node instanceof ExpressionNot) {
			n1=rewriteExpressionNot((ExpressionNot) node);
		} else if (node instanceof ExpressionOr) {
			n1=rewriteExpressionOr((ExpressionOr) node);
		} else if (node instanceof ExpressionWildcard) {
			n1=rewriteExpressionWildcard((ExpressionWildcard) node);
		} else if (node instanceof SimpleWithin) {
			n1=rewriteSimpleWithin((SimpleWithin) node);
		} else if (node instanceof QueryWithWithin) {
			n1=rewriteQueryWithWithin((QueryWithWithin) node);
		} else {
			throw new RuntimeException("unexpected node type: "  + node.getNodeType());
		}
		
		
		return n1;
	}
	
	
	/**
	 * Rewrite a list of FSC-QL query nodes 
	 * (which we have when dealing with AND, OR, etc.; that consists of at least 2 nodes).
	 * 
	 * In particular convert the universal dependencies
	 * into the tag set given in the constructor
	 * 
	 * @param list of nodes 
	 * @return a list of rewritten nodes 
	 */
	private List<QueryNode> mapRewrite(List<QueryNode> l)
	{
		return l.stream().map(n -> rewriteNode(n)).collect(Collectors.toList());
	}
	
	
	// ------------------------------------------------------------------------
	// special cases

	private QueryNode rewriteQueryWithWithin(QueryWithWithin node) {
		// TODO Auto-generated method stub
		return new QueryWithWithin(rewriteNode(node.getFirstChild()), rewriteNode(  node.getChildren().get(1) ));
	}

	private QueryNode rewriteSimpleWithin(SimpleWithin node) {
		SimpleWithin sw =  new SimpleWithin(node.getScope());
		return sw; // children ???? TODO dit kan niet kloppen!!!
	}

	private QueryNode rewriteExpressionWildcard(ExpressionWildcard node) {
		return new ExpressionWildcard(); // TODO snap ik dit??
	}

	private QueryNode rewriteExpressionOr(ExpressionOr node) {
		
		return new ExpressionOr(mapRewrite(node.getOperands()));
	}

	private QueryNode rewriteExpressionNot(ExpressionNot node) {
		return new ExpressionNot(rewriteNode(node.getFirstChild()));
	}

	private QueryNode rewriteExpressionGroup(ExpressionGroup node) {
		return new ExpressionGroup(rewriteNode(node.getFirstChild()));
	}

	private QueryNode rewriteExpression(Expression node) {
		Expression e = new Expression(node.getLayerQualifier(), node.getLayerIdentifier(), node.getOperator(), node.getRegexValue(), node.getRegexFlags());
		return e;
	}

	private QueryNode rewriteExpressionAnd(ExpressionAnd node) {
		return new ExpressionAnd(mapRewrite(node.getChildren()));
	}

	private QueryNode rewriteQuerySequence(QuerySequence node) {
		return new QuerySequence(mapRewrite(node.getChildren()));
	}

	private QueryNode rewriteQuerySegment(QuerySegment node) {
		return new QuerySegment(rewriteNode(node.getExpression()), node.getMinOccurs(), node.getMaxOccurs());
	}

	private QueryNode rewriteQueryGroup(QueryGroup node) {
		return new QueryGroup(rewriteNode(node.getContent()), node.getMinOccurs(), node.getMaxOccurs());
	}

	private QueryNode rewriteQueryDisjunction(QueryDisjunction node) {
		return new QueryDisjunction(mapRewrite(node.getChildren()));
	}
}

