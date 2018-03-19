package eu.clarin.sru.server.fcs.parser;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.ivdnt.util.StringUtils;

/**
 * Reserialize parsed query to CQP.<br>
 * The clarin fcs Parser classes did not have this.
 * 
 * @author jesse
 * 
 */
public class CqpWriter 
{
	boolean useRegex = false;
	boolean includeFeatureNameInRegex = true;

	String posTagFeature;
	Set<String> grammaticalFeatures;
	String valueQuote = "'";

	public void setRegexHack(String posTagFeature, String[] grammaticalFeatures, boolean includeFeatureNameInRegex)
	{
		this.posTagFeature = posTagFeature;
		this.grammaticalFeatures = new HashSet<>();
		for (String s: grammaticalFeatures)
			this.grammaticalFeatures.add(s);
		this.useRegex = true;
		this.includeFeatureNameInRegex = includeFeatureNameInRegex;
	}

	public void setQuote(String s)
	{
		this.valueQuote = s;
	}

	public List<String> writeList(List<QueryNode> l)
	{
		return l.stream().map(
				
				n -> writeAsCQP(n)
				
		).collect(Collectors.toList());
	}

	public  String writeAsCQP(QueryNode node)
	{
		String n1;
		if (node instanceof QueryDisjunction) {
			n1=writeQueryDisjunction((QueryDisjunction) node);
		} else if (node instanceof QueryGroup) {
			n1=writeQueryGroup((QueryGroup) node);
		} else if (node instanceof QuerySegment) {
			n1=writeQuerySegment((QuerySegment) node);
		} else if (node instanceof QuerySequence) {
			n1=writeQuerySequence((QuerySequence) node);
		} else if (node instanceof ExpressionAnd) {
			n1=writeExpressionAnd((ExpressionAnd) node);
		} else if (node instanceof Expression) {
			n1=writeExpression( (Expression) node );
		} else if (node instanceof ExpressionGroup) {
			n1=writeExpressionGroup((ExpressionGroup) node);
		} else if (node instanceof ExpressionNot) {
			n1=writeExpressionNot((ExpressionNot) node);
		} else if (node instanceof ExpressionOr) {
			n1=writeExpressionOr((ExpressionOr) node);
		} else if (node instanceof ExpressionWildcard) {
			n1=writeExpressionWildcard((ExpressionWildcard) node);
		} else if (node instanceof SimpleWithin) {
			n1=writeSimpleWithin((SimpleWithin) node);
		} else if (node instanceof QueryWithWithin) {
			n1=writeQueryWithWithin((QueryWithWithin) node);
		}else {
			throw new RuntimeException("unexpected node type: "  + node.getNodeType());
		}
		return n1;
	}

	// TODO: dit moet ook in de mapping....

	private String writeQueryWithWithin(QueryWithWithin node) {

		return writeAsCQP(node.getFirstChild())  + "  within " + writeAsCQP(node.getWithin());

	}

	private   String writeSimpleWithin(SimpleWithin node) {
		SimpleWithin sw =  new SimpleWithin(node.getScope());
		switch (sw.getScope())
		{
		case SENTENCE: return "<s/>"; // HM
		case UTTERANCE: case TEXT: case PARAGRAPH: case TURN: case SESSION: 
		default: return "<kweenie/>";
		}
	}

	private   String writeExpressionWildcard(ExpressionWildcard node) {
		return "" ; // TODO snap ik dit?? De parser maakt een QuerySegment (Wildcard) aan
	}

	private   String writeExpressionOr(ExpressionOr node) {
		if (node.parent.children.size() > 1)
			return "(" + StringUtils.join(writeList(node.getChildren()), " | ") + ")"; // brackets needed only if also other clauses
		else
			return StringUtils.join(writeList(node.getChildren()), " | ");
	}

	private   String writeExpressionNot(ExpressionNot node) {
		if (node.getFirstChild() != null && 
				node.getFirstChild().getChildren() != null && 
				node.getFirstChild().getChildren().size() == 1)
			return "!" + writeAsCQP(node.getFirstChild()); // if child has only one child, there is no need for the extra bracketing
		else 
			return "!("  + writeAsCQP(node.getFirstChild()) + ")";
	}

	private   String writeExpressionGroup(ExpressionGroup node) {
		return "("  + writeAsCQP(node.getFirstChild()) + ")";
	}

	private String writeOperator(Operator o)
	{
		switch (o)
		{
		case EQUALS: return "=";
		case NOT_EQUALS: return "!=";
		default: return "WADDE?";
		}
	}

	/**
	 * TODO feature regexes are too simple
	 * @param node (expression node)
	 * @return expression as CQL (<i>name='value'</i> if grammatical features are indexed separately, or else <i>pos=some regex</i>)
	 */
	private String writeExpression(Expression node) 
	{
		Expression e = new Expression(node.getLayerQualifier(), node.getLayerIdentifier(), node.getOperator(), node.getRegexValue(), node.getRegexFlags());
		String n = e.getLayerIdentifier();
		String v = e.getRegexValue();

		String operator = writeOperator(node.getOperator());
		if (this.useRegex && n.equals(posTagFeature))
			return  String.format("%s%s%s^(%s).*%s",n,operator, valueQuote,v, valueQuote);


		if (this.useRegex && this.grammaticalFeatures.contains(n))
		{
			if (this.includeFeatureNameInRegex)
			{
				String valueMatch = String.format("([^\\|,\\(\\)]*\\|)*(%s)[,\\|\\(\\)]", v);
				return String.format("%s%s%s.*%s=(%s).*%s", posTagFeature, operator, valueQuote, n, valueMatch, valueQuote);
			}
			else
				return String.format("%s%s%s.*[\\(,\\|](%s)[,\\)\\|].*%s", posTagFeature, operator, valueQuote, v, valueQuote);
		} else
			return n +  writeOperator(node.getOperator())  + valueQuote + v+ valueQuote;
	}

	private   String writeExpressionAnd(ExpressionAnd node) {
		if (node.parent.children.size() > 1)
			return "(" + StringUtils.join(writeList(node.getChildren()), " & ") + ")"; // brackets needed only if also other clauses
		else
			return StringUtils.join(writeList(node.getChildren()), " & ");
	}

	private   String writeQuerySequence(QuerySequence node) {
		return StringUtils.join(writeList(node.getChildren()), " ");
	}

	private   String writeQuerySegment(QuerySegment node) {
		String arg0 = "[" + writeAsCQP(node.getExpression()) + "]";
		int min = node.getMinOccurs();
		int max = node.getMaxOccurs();
		if (min ==1 && max==1)
			return arg0;
		return String.format("%s{%d, %d}", arg0, min, max);
	}

	private   String writeQueryGroup(QueryGroup node) 
	{
		String arg0 = "(" + writeAsCQP(node.getFirstChild()) + ")";
		int min = node.getMinOccurs();
		int max = node.getMaxOccurs();
		if (min ==1 && max==1)
			return arg0;
		return String.format("%s{%d, %d}", arg0, min, max);
	}

	private   String writeQueryDisjunction(QueryDisjunction node) 
	{
		return StringUtils.join(writeList(node.getChildren()), " | ");
	}
}
