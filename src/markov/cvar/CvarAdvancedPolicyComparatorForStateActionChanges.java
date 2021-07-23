package markov.cvar;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import finitestatemachine.Action;
import finitestatemachine.State;
import markov.GeneralizedValueFunction;
import markov.MDP;
import markov.Policy;
import markov.probas.DiscreteProbabilityDistributionAccuracyParameters;
import markov.rewarddistributions.RewardDistribution;
import markov.rewarddistributions.RewardDistributions;


/**
 * This comparator is meant to be used for optimizing a policy
 * -given an original state s0
 * -given a modification occurring at a state si
 * 
 *  The need for both s0 and si is important as backpropagation and the repetition of CVAR steps (i.e. removing over time the top x% value) easily creates precision errors. 
 *  Therefore, so it is useful to check both at the state si, which is used for comparing two states, whether there is a visible change when altering the action.
 *  
 *  As a consequence, this comparator is to be used sequentially; and it is necessary that the supplier is updated to the right state before performing the comparison.
 * @author loisv
 *
 * @param <S>
 * @param <A>
 */
public class CvarAdvancedPolicyComparatorForStateActionChanges<S extends State, A extends Action>  implements Comparator<Policy<S,A>> {
	
	private final S s0;
	private final MDP<S, A> mdp;
	private final int horizon;
	private final DiscreteProbabilityDistributionAccuracyParameters probabilityDistributionAccuracyParameters;
	private final Supplier<S> currentlyStudiedState;
	
	//this factor represents how far best outcomes are disregarded
	private final double skewingFactor;

	private CvarAdvancedPolicyComparatorForStateActionChanges(S s02, MDP<S, A> mdp2, int horizon,DiscreteProbabilityDistributionAccuracyParameters cvarParams, Supplier<S> currentlyStudiedState, double skewingFactor) {
		this.s0 = s02;
		this.mdp = mdp2;
		this.horizon = horizon;
		this.probabilityDistributionAccuracyParameters = cvarParams;
		this.currentlyStudiedState = currentlyStudiedState;
		this.skewingFactor = skewingFactor;
	}
	
	
	@Override
	public int compare(Policy<S, A> p1, Policy<S, A> p2) {
		boolean v1Above = false;
		double maxDiff = 0;

		GeneralizedValueFunction<S, RewardDistribution> valP1 = RewardDistributions.getValueFunction(mdp, p1, horizon,probabilityDistributionAccuracyParameters);
		RewardDistribution r1 = valP1.apply(s0);
		RewardDistribution r1Last =valP1.apply(currentlyStudiedState.get());


		GeneralizedValueFunction<S, RewardDistribution> valP2 = RewardDistributions.getValueFunction(mdp, p2, horizon, probabilityDistributionAccuracyParameters);

		RewardDistribution r2 = valP2.apply(s0);
		RewardDistribution r2Last =valP2.apply(currentlyStudiedState.get());

		RewardDistribution skewedR1=RewardDistributions.newInstanceRemovingTheProbabilityForUpperRewards(r1, skewingFactor,	probabilityDistributionAccuracyParameters);
		RewardDistribution skewedR2=RewardDistributions.newInstanceRemovingTheProbabilityForUpperRewards(r2, skewingFactor,	probabilityDistributionAccuracyParameters);
		RewardDistribution skewedR1Last=RewardDistributions.newInstanceRemovingTheProbabilityForUpperRewards(r1Last, skewingFactor,	probabilityDistributionAccuracyParameters);
		RewardDistribution skewedR2Last=RewardDistributions.newInstanceRemovingTheProbabilityForUpperRewards(r2Last, skewingFactor,	probabilityDistributionAccuracyParameters);
		
		double v1 = skewedR1.getAverageReward();
		double v2 = skewedR2.getAverageReward();
		double maxDiffLast = skewedR1Last.getAverageReward() - skewedR2Last.getAverageReward();


		if(v1>v2 && v1-v2 > maxDiff|| RewardDistributions.isStronglyDominating(r1, r2)) {maxDiff = v1-v2; v1Above = true;}
		if(v2>v1 && v2-v1 > maxDiff) {maxDiff = v2-v1; v1Above = false;}

		//if the difference is too meager, we pick the safest state given the last state explored, 
		//else it might just be a side effect of double approximations
		if(maxDiff<Double.MAX_VALUE)//0.001 
		{
			

			if(Math.abs(maxDiffLast)<0.005) return 0;

			if(maxDiffLast<0) 
				return -1;
			else 
				return 1;            				
		}

		if(RewardDistributions.isStronglyDominating(skewedR1, skewedR2))
			return 1;
		if(RewardDistributions.isStronglyDominating(skewedR2, skewedR1))
			return -1;

		else 
			if(v1Above) 
				return 1;
			else 
				return -1;
	}

	public static<S extends State,A extends Action> CvarAdvancedPolicyComparatorForStateActionChanges<S, A> newInstance(S s0, MDP<S,A> mdp, int horizon,DiscreteProbabilityDistributionAccuracyParameters cvarParams, Supplier<S> currentlyStudiedState, double skewingFactor) {
		return new CvarAdvancedPolicyComparatorForStateActionChanges<S,A>(s0,mdp, horizon, cvarParams, currentlyStudiedState, skewingFactor);
	}
	
	

}
