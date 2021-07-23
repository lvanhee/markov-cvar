module markov.cvar {
	requires finitestatemachine;
	requires markov;
	requires transitive markov.rewarddistributions;
	
	exports markov.cvar;
}