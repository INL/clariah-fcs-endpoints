package eu.clarin.sru.server.fcs.parser;

import java.util.List;
import java.util.stream.Collectors;

import org.ivdnt.util.StringUtils;

public class WriteAsCQP 
{
	static public List<String> mapClone(List<QueryNode> l)
	{
		return l.stream().map(n -> writeAsCQP(n)).collect(Collectors.toList());
	}
	
	public static String writeAsCQP(QueryNode node)
	{
		String n1;
		if (node instanceof QueryDisjunction) {
			n1=cloneQueryDisjunction((QueryDisjunction) node);
		} else if (node instanceof QueryGroup) {
			n1=cloneQueryGroup((QueryGroup) node);
		} else if (node instanceof QuerySegment) {
			n1=cloneQuerySegment((QuerySegment) node);
		} else if (node instanceof QuerySequence) {
			n1=cloneQuerySequence((QuerySequence) node);
		} else if (node instanceof ExpressionAnd) {
			n1=cloneExpressionAnd((ExpressionAnd) node);
		} else if (node instanceof Expression) {
			n1=cloneExpression( (Expression) node );
		} else if (node instanceof ExpressionGroup) {
			n1=cloneExpressionGroup((ExpressionGroup) node);
		} else if (node instanceof ExpressionNot) {
			n1=cloneExpressionNot((ExpressionNot) node);
		} else if (node instanceof ExpressionOr) {
			n1=cloneExpressionOr((ExpressionOr) node);
		} else if (node instanceof ExpressionWildcard) {
			n1=cloneExpressionWildcard((ExpressionWildcard) node);
		} else if (node instanceof SimpleWithin) {
			n1=cloneSimpleWithin((SimpleWithin) node);
		} else {
			throw new RuntimeException("unexpected node type: "  + node.getNodeType());
		}
		return n1;
	}
	
	private static  String cloneSimpleWithin(SimpleWithin node) {
		SimpleWithin sw =  new SimpleWithin(node.getScope());
		return " within <snapikniet/>" ; // children ???? TODO dit kan niet kloppen!!!
	}

	private  static String cloneExpressionWildcard(ExpressionWildcard node) {
		return "[]" ; // TODO snap ik dit??
	}

	private  static String cloneExpressionOr(ExpressionOr node) {
		
		return StringUtils.join(mapClone(node.getChildren()), " | ");
	}

	private  static String cloneExpressionNot(ExpressionNot node) {
		return "!" + writeAsCQP(node.getFirstChild());
	}

	private  static String cloneExpressionGroup(ExpressionGroup node) {
		return "("  + writeAsCQP(node.getFirstChild()) + ")";
	}

	private  static String cloneExpression(Expression node) {
		Expression e = new Expression(node.getLayerQualifier(), node.getLayerIdentifier(), node.getOperator(), node.getRegexValue(), node.getRegexFlags());
		return e.getLayerIdentifier() +  '='  + "'" + e.getRegexValue() + "'";
	}

	private  static String cloneExpressionAnd(ExpressionAnd node) {
		return StringUtils.join(mapClone(node.getChildren()), " & ");
	}

	private  static String cloneQuerySequence(QuerySequence node) {
		return StringUtils.join(mapClone(node.getChildren()), " ");
	}

	private  static String cloneQuerySegment(QuerySegment node) {
		String arg0 = "[" + writeAsCQP(node.getExpression()) + "]";
		int min = node.getMinOccurs();
		int max = node.getMaxOccurs();
		if (min ==1 && max==1)
			return arg0;
		return String.format("%s{%d, %d}", arg0, min, max);
	}

	private  static String cloneQueryGroup(QueryGroup node) 
	{
		String arg0 = "(" + writeAsCQP(node.getFirstChild()) + ")";
		int min = node.getMinOccurs();
		int max = node.getMaxOccurs();
		if (min ==1 && max==1)
			return arg0;
		return String.format("%s{%d, %d}", arg0, min, max);
	}

	private  static String cloneQueryDisjunction(QueryDisjunction node) 
	{
		return StringUtils.join(mapClone(node.getChildren()), " | ");
	}
}
