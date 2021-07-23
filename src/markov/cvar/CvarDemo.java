package markov.cvar;

import java.util.function.Supplier;

import finitestatemachine.Action;
import finitestatemachine.State;
import markov.MDP;
import markov.probas.DiscreteProbabilityDistributionAccuracyParameters;

public class CvarDemo {

	public final static DiscreteProbabilityDistributionAccuracyParameters CVAR_PARAMS = new DiscreteProbabilityDistributionAccuracyParameters(200,0.0001);
	public static final double DEFAULT_CVAR_DISCOUNT = 0.95;
	
	public static void main(String[] args)
	{
		
		
		MDP<State, Action> demoMdp = null;
		CvarAdvancedPolicyComparatorForStateActionChanges<State, Action> comparator = null;
		throw new Error();
	}
}
