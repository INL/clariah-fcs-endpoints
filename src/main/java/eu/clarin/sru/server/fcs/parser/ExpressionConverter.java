package eu.clarin.sru.server.fcs.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.ivdnt.fcs.mapping.ConversionEngine;
import org.ivdnt.fcs.mapping.Feature;
import org.ivdnt.fcs.mapping.FeatureConjunction;

/**
 * dit is een hack: implementatie van een class waarvan de velden protected
 * zijn, om die toch te kunnen benaderen...
 * 
 * @author jesse
 *
 */

public class ExpressionConverter implements ExpressionRewriter {
	private ConversionEngine conversionEngine;

	// ---------------------------------------------------------------------------------

	/**
	 * Constructor
	 * 
	 * An ExpressionConverter takes a ConversionEngine as a parameter, so as to be
	 * able to convert features etc.
	 * 
	 * @param ConversionEngine
	 */
	public ExpressionConverter(ConversionEngine conversionEngine) {
		this.conversionEngine = conversionEngine;
	}

	// ---------------------------------------------------------------------------------

	/**
	 * Build a queryNode for a given feature
	 * 
	 * (this is a special case when dealing with nodes, since nodes can also be
	 * groups of nodes, etc.)
	 * 
	 * @param feature
	 *            object (which consists of a feature name and a set of values)
	 * @return a QueryNode representing the feature
	 */
	private QueryNode buildNodeForFeature(Feature feature) {
		List<QueryNode> orz =

				feature.getValues().stream().map(

						// an expression is an extension of QueryNode:
						// the QueryNode is being attached an operator and
						// (sometimes) a regex representing its values
						value -> new Expression(null, feature.getFeatureName(), Operator.EQUALS, value, null)

				).collect(Collectors.toList());

		// Features out of a conjunction should be joined into a OR expression;
		// for simplex nodes, this is not necessary

		if (orz.size() == 1)
			return orz.get(0);

		return new ExpressionOr(orz);
	}

	/**
	 * Add a negation operator to a QueryNode
	 * 
	 * (and make sure a neglected operator is processed correctly: [-]x[-] = [+])
	 * 
	 * @param nodes
	 * @return
	 */
	private QueryNode negation(QueryNode nodes) {
		// 2 possibilities:

		// [1] Negation is applied to simplex Expressions (=no conjunctions, etc.)

		if (nodes instanceof Expression) {
			// an expression is an extension of QueryNode:
			// the QueryNode is being attached an operator and
			// (sometimes) a regex representing its values

			Expression e = (Expression) nodes;

			// negation of negation means equality
			Operator flip = (e.getOperator() == Operator.NOT_EQUALS) ? Operator.EQUALS : Operator.NOT_EQUALS;

			return new Expression(e.getLayerQualifier(), e.getLayerIdentifier(), flip, e.getRegexValue(),
					e.getRegexFlags());
		}

		// [2] In case we have a conjunction of nodes, it will be tagged as a negative
		// expression

		return new ExpressionNot(nodes);
		// TODO? kan je natuurlijk naar binnen proberen te duwen, etc, maar laat maar
		// even
	}

	// ---------------------------------------------------------------------------------

	/**
	 * Translate an Expression (which is an extension of QueryNode, supplying it
	 * with an operator and some Regex flags)
	 * 
	 * This method is called for simplex nodes, meaning NO disjunction NOR group of
	 * nodes of any kind. A simplex node is supposed to contain a single feature,
	 * consisting of a feature name and a feature value (identifier => regex value).
	 * 
	 */
	@Override
	public QueryNode rewriteExpression(Expression e) // TODO: if the operator is a NOT_EQUALS, this is too simple
	{
		final boolean negative = e.getOperator() == Operator.NOT_EQUALS;

		// get feature name and value

		String feature = e.getLayerIdentifier();
		String value = e.getRegexValue();

		// Translating a feature can result into a conjunction of more features
		// like:
		//
		// {"pos": "NOUN"} => {"pos": "N", "feat.ntype": "soort"}
		//
		// That's why the output of the translation is a Set of FeatureConjunctions

		Set<FeatureConjunction> featureConjunctions = this.conversionEngine.translateFeature(feature, value);

		List<QueryNode> nodesOr = new ArrayList<>();

		// build QueryNodes for all (conjunctions of) features

		for (FeatureConjunction oneFeatureConjunction : featureConjunctions) {
			List<QueryNode> nodesOfCurrentConjunction =

					oneFeatureConjunction.getFeatures().map(

							feat -> buildNodeForFeature(feat)

					).collect(Collectors.toList());

			// Siblings of a conjunction are joined into an AND expression;
			// for simplex nodes, this is not necessary

			if (nodesOfCurrentConjunction.size() == 1) {
				nodesOr.add(nodesOfCurrentConjunction.get(0));
			} else {
				ExpressionAnd ea = new ExpressionAnd(nodesOfCurrentConjunction);
				nodesOr.add(ea);
			}
		}

		// the different conjunctions are joined into the output with a OR operator

		if (nodesOr.size() == 1) {
			QueryNode o1 = nodesOr.get(0);
			return negative ? negation(o1) : o1;
		}
		ExpressionOr exprOr = new ExpressionOr(nodesOr);
		return negative ? negation(exprOr) : exprOr;
	}
}
