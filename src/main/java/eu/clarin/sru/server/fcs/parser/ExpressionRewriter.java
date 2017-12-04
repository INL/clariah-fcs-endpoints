package eu.clarin.sru.server.fcs.parser;

import eu.clarin.sru.server.fcs.parser.Expression;
import eu.clarin.sru.server.fcs.parser.QueryNode;

public interface ExpressionRewriter 
{
	public QueryNode rewriteExpression(Expression e);
}
