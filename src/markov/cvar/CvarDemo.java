package markov.cvar;

import finitestatemachine.Action;
import finitestatemachine.State;
import markov.MDP;
import markov.probas.DiscreteProbabilityDistributionAccuracyParameters;

public class CvarDemo {

	public static void main(String[] args)
	{
		DiscreteProbabilityDistributionAccuracyParameters CVAR_PARAMS = new DiscreteProbabilityDistributionAccuracyParameters(200,0.0001);
		
		MDP<State, Action> demoMdp = null;
		CvarAdvancedPolicyComparatorForStateActionChanges<State, Action> comparator = null;
		throw new Error();
	}
}
