package eu.clarin.sru.server.fcs.parser;

public interface ExpressionRewriter {
	public QueryNode rewriteExpression(Expression e);
}
