package eu.stratosphere.pact.incremental.contracts;

import eu.stratosphere.pact.common.plan.PlanException;
import eu.stratosphere.pact.generic.contract.Contract;
import eu.stratosphere.pact.generic.contract.WorksetIteration;

/**
 *
 * Generic WorkList Iteration
 * for each element x in S that changes value, 
 * we add in the WorkList all the elements of S that depend on x
 * and recompute only these in the next iteration 
 * 
 */
public class DependencyIteration extends WorksetIteration {
	
	public DependencyIteration(int keyPosition, String name) {
		super(keyPosition, name);
	}

	private Contract dependencySet;	//the Dependency set input
	
	/**
	 * Sets the contract of the DependencySet
	 * 
	 * @param delta The contract representing the dependencies / graph structure
	 */
	public void setDependencySet(Contract dependencies) {
		this.dependencySet = dependencies;
	}
	
	/**
	 * Gets the contract that has been set as the dependency set
	 * 
	 * @return The contract that has been set as the dependency set.
	 */
	public Contract getDependencySet() {
		return this.dependencySet;
	}

	/**
	 * checks if the dependency iteration has been configured properly
	 */
	public boolean isConfigured() {
		if (this.getDependencySet()== null)
			throw new PlanException("The dependency Set is empty");
		else if(this.getInitialWorkset() == null)
			throw new PlanException("The initial WorkSet is empty");
		else if(this.getInitialSolutionSet() == null)
			throw new PlanException("The initial SolutionSet is empty");
		else if(this.getNextWorkset() == null)
			throw new PlanException("Next WorkSet is empty");
		else if(this.getSolutionSetDelta() == null)
			throw new PlanException("SolutionSetDelta is empty");
		else
			return true;			
	}

}
