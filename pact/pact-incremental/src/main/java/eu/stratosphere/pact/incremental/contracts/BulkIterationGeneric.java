package eu.stratosphere.pact.incremental.contracts;

import eu.stratosphere.pact.common.contract.MatchContract;
import eu.stratosphere.pact.common.contract.ReduceContract;
import eu.stratosphere.pact.common.stubs.aggregators.Aggregator;
import eu.stratosphere.pact.common.stubs.aggregators.ConvergenceCriterion;
import eu.stratosphere.pact.common.type.Value;
import eu.stratosphere.pact.generic.contract.BulkIteration;
import eu.stratosphere.pact.generic.contract.Contract;

/**
 * @author Vasia Kalavri
 *
 */
public class BulkIterationGeneric extends BulkIteration {
	
	private Contract dependencySet;
	private static String dependencySetKeyIndex = "DEPENDENCYSET_KEY_INDEX" ;
	private MatchContract computeDependencies;
	private ReduceContract updateValues;

	/**
	 * @param solutionSet
	 */
	public void setInitialSolutionSet(Contract solutionSet) {
		this.setInput(solutionSet);
	}
	
	/**
	 * @param dependencySet
	 */
	public void setDependencySet(Contract depSet, int keyIndex) {
		dependencySet = depSet;
		dependencySet.setParameter(dependencySetKeyIndex, keyIndex);
	}
	
	
	
	
	/**
	 * @param <T>
	 * @param criterion
	 */
	public <T extends Value> void setConvergenceCriterion(String name, Class<? extends Aggregator<T>> aggregator,Class<? extends ConvergenceCriterion<T>> convergenceCheck) {
		this.getAggregators().registerAggregationConvergenceCriterion(name, aggregator, convergenceCheck);
	}
}
