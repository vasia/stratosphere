package eu.stratosphere.pact.example.incremental.pagerank;

import eu.stratosphere.pact.common.stubs.Collector;
import eu.stratosphere.pact.common.stubs.MatchStub;
import eu.stratosphere.pact.common.stubs.StubAnnotation.ConstantFieldsFirst;
import eu.stratosphere.pact.common.type.PactRecord;
import eu.stratosphere.pact.common.type.base.PactDouble;

@ConstantFieldsFirst({0, 1})
public class DependencyOldRankComparisonMatch extends MatchStub {
	
	@Override
	public void match(PactRecord newRank, PactRecord oldRank, Collector<PactRecord> out) throws Exception {
		double newRankValue = newRank.getField(1, PactDouble.class).getValue();
		double oldRankValue = oldRank.getField(1, PactDouble.class).getValue();
		
		//if (newRankValue != oldRankValue) {
		if (Math.abs(newRankValue - oldRankValue) > 0) {
			out.collect(newRank);
		}
	}

}
