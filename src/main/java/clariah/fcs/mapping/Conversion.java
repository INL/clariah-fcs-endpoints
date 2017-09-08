package clariah.fcs.mapping;

import java.util.*;

/**
 * Hm.
 * Already at this level, a single feature might translate to a disjunction or a conjunction 
 * of other features, or a disjunction of conjunctions. 
 * this should also cover translation of word= to t_lc= for Nederlab, etc.
 */

public abstract class Conversion
{
	String tagsetFrom;
	String tagsetTo;
	
	public abstract Set<String> translatePoS(String PoS);
	public abstract Set<FeatureConjunction> translateFeature(String feature, String value);
}