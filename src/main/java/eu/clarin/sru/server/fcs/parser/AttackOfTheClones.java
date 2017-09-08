package eu.clarin.sru.server.fcs.parser;

import java.util.*;
import java.util.stream.Collectors;

import clariah.fcs.mapping.Conversion;
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
 * Bleuh dit moet je in package eu.clarin.sru.server.fcs.parser zetten, anders 
 * kan je niks clonen. Bleurp.
 * @author does
 *
 */
public class AttackOfTheClones
{
	ExpressionRewriter erw;
	
	public AttackOfTheClones(ExpressionRewriter erw)
	{
		this.erw = erw;
	}
	
	public AttackOfTheClones(Conversion  c)
	{
		this.erw = new ExpressionConverter(c);
	}
	
	public List<QueryNode> mapClone(List<QueryNode> l)
	{
		return l.stream().map(n -> clone(n)).collect(Collectors.toList());
	}
	
	public QueryNode rewrite (QueryNode node)
	{
		return clone(node);
	}
	
	public QueryNode rewrite(String cqp)
	{
		QueryParser parser = new QueryParser();
		try
		{
			QueryNode qn = parser.parse(cqp);
			System.out.println(qn.toString());
			return rewrite(qn);
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}
	
	public QueryNode clone(QueryNode node)
	{
		QueryNode n1;
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
			n1=erw.rewriteExpression( (Expression) node );
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

	private QueryNode cloneSimpleWithin(SimpleWithin node) {
		// TODO Auto-generated method stub
		SimpleWithin sw =  new SimpleWithin(node.getScope());
		return sw; // children ???? TODO dit kan niet kloppen!!!
		//sw.children = node.children;
	}

	private QueryNode cloneExpressionWildcard(ExpressionWildcard node) {
		// TODO Auto-generated method stub
		return new ExpressionWildcard(); // snap ik dit?
	}

	private QueryNode cloneExpressionOr(ExpressionOr node) {
		// TODO Auto-generated method stub
		//List<>
		//ExpressionOr n1 = new ExpressionOr(null)
		return new ExpressionOr(mapClone(node.getOperands()));
	}

	private QueryNode cloneExpressionNot(ExpressionNot node) {
		// TODO Auto-generated method stub
		return new ExpressionNot(clone(node.getFirstChild()));
	}

	private QueryNode cloneExpressionGroup(ExpressionGroup node) {
		// TODO Auto-generated method stub
		return new ExpressionGroup(clone(node.getFirstChild()));
	}

	private QueryNode cloneExpression(Expression node) {
		// TODO Auto-generated method stub
		Expression e = new Expression(node.getLayerQualifier(), node.getLayerIdentifier(), node.getOperator(), node.getRegexValue(), node.getRegexFlags());
		return e;
	}

	private QueryNode cloneExpressionAnd(ExpressionAnd node) {
		// TODO Auto-generated method stub
		return new ExpressionAnd(mapClone(node.getChildren()));
	}

	private QueryNode cloneQuerySequence(QuerySequence node) {
		// TODO Auto-generated method stub
		return new QuerySequence(mapClone(node.getChildren()));
	}

	private QueryNode cloneQuerySegment(QuerySegment node) {
		// TODO Auto-generated method stub
		return new QuerySegment(clone(node.getExpression()), node.getMinOccurs(), node.getMaxOccurs());
	}

	private QueryNode cloneQueryGroup(QueryGroup node) {
		// TODO Auto-generated method stub
		return new QueryGroup(clone(node.getContent()), node.getMinOccurs(), node.getMaxOccurs());
	}

	private QueryNode cloneQueryDisjunction(QueryDisjunction node) {
		// TODO Auto-generated method stub
		return new QueryDisjunction(mapClone(node.getChildren()));
	}
	
	/**
	static class MyNode
    {
    	QueryNode original;
    	List<MyNode> children;
    	QueryNodeType type;
    	boolean changed = false;
    }
    
    public MyNode clone(QueryNode node)
    {
    	MyNode n1 = new MyNode();
        n1.original = node;
        n1.type = node.getNodeType();
        n1.children = node.getChildren().stream().map(n -> clone(n)).collect(Collectors.toList());
        return n1;
    }
    */
}

