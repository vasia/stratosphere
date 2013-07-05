/***********************************************************************************************************************
 *
 * Copyright (C) 2010 by the Stratosphere project (http://stratosphere.eu)
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *
 **********************************************************************************************************************/

package eu.stratosphere.pact.runtime.plugable.pactrecord;

import eu.stratosphere.pact.common.type.Key;
import eu.stratosphere.pact.common.type.NullKeyFieldException;
import eu.stratosphere.pact.common.type.PactRecord;
import eu.stratosphere.pact.common.util.InstantiationUtil;
import eu.stratosphere.pact.generic.types.TypePairComparator;


/**
 * Implementation of the {@link TypePairComparator} interface for Pact Records. The equality is established on a set of
 * key fields. The indices of the key fields may be different on the reference and candidate side.
 */
public class PactRecordPairComparator extends TypePairComparator<PactRecord, PactRecord> {
	
	private final int[] keyFields1, keyFields2;			// arrays with the positions of the keys in the records
	
	private final Key[] keyHolders1, keyHolders2;		// arrays with mutable objects for the key types
	
	
	public PactRecordPairComparator(int[] keyFieldsReference, int[] keyFieldsCandidate, Class<? extends Key>[] keyTypes) {
		if (keyFieldsReference.length != keyFieldsCandidate.length || keyFieldsCandidate.length != keyTypes.length) {
			throw new IllegalArgumentException(
				"The arrays describing the key positions and types must be of the same length.");
		}
		this.keyFields1 = keyFieldsReference;
		this.keyFields2 = keyFieldsCandidate;
		
		// instantiate fields to extract keys into
		this.keyHolders1 = new Key[keyTypes.length];
		this.keyHolders2 = new Key[keyTypes.length];
		
		for (int i = 0; i < keyTypes.length; i++) {
			if (keyTypes[i] == null) {
				throw new NullPointerException("Key type " + i + " is null.");
			}
			this.keyHolders1[i] = InstantiationUtil.instantiate(keyTypes[i], Key.class);
			this.keyHolders2[i] = InstantiationUtil.instantiate(keyTypes[i], Key.class);
		}
	}
	
	private  PactRecordPairComparator(int[] keyFieldsReference, int[] keyFieldsCandidate, Key[] keyTypes) {
		this.keyFields1 = keyFieldsReference;
		this.keyFields2 = keyFieldsCandidate;
		
		// instantiate fields to extract keys into
		this.keyHolders1 = new Key[keyTypes.length];
		this.keyHolders2 = new Key[keyTypes.length];
		
		try {
			for (int i = 0; i < keyTypes.length; i++) {
				this.keyHolders1[i] = keyTypes[i].getClass().newInstance();
				this.keyHolders2[i] = keyTypes[i].getClass().newInstance();
			}
		}
		catch (Exception e) {
			throw new RuntimeException("Bug: Error instantiating key classes during comparator duplication.");
		}
	}
	
	// --------------------------------------------------------------------------------------------

	/* (non-Javadoc)
	 * @see eu.stratosphere.pact.runtime.plugable.TypeComparator#setReference(java.lang.Object)
	 */
	@Override
	public void setReference(PactRecord reference)
	{
		for (int i = 0; i < this.keyFields1.length; i++) {
			if (!reference.getFieldInto(this.keyFields1[i], this.keyHolders1[i])) {
				throw new NullKeyFieldException(this.keyFields1[i]);
			}
		}
	}

	/* (non-Javadoc)
	 * @see eu.stratosphere.pact.runtime.plugable.TypeComparator#equalToReference(java.lang.Object)
	 */
	@Override
	public boolean equalToReference(PactRecord candidate)
	{
		for (int i = 0; i < this.keyFields2.length; i++) {
			final Key k = candidate.getField(this.keyFields2[i], this.keyHolders2[i]);
			if (k == null)
				throw new NullKeyFieldException(this.keyFields2[i]);
			else if (!k.equals(this.keyHolders1[i]))
				return false;
		}
		return true;
	}

	/* (non-Javadoc)
	 * @see eu.stratosphere.pact.runtime.plugable.TypePairComparator#compareToReference(java.lang.Object)
	 */
	@Override
	public int compareToReference(PactRecord candidate)
	{
		for (int i = 0; i < this.keyFields2.length; i++) {
			final Key k = candidate.getField(this.keyFields2[i], this.keyHolders2[i]);
			if (k == null)
				throw new NullKeyFieldException(this.keyFields2[i]);
			else {
				final int comp = k.compareTo(this.keyHolders1[i]);
				if (comp != 0) {
					return comp;
				}
			}
		}
		return 0;
	}

	@Override
	public PactRecordPairComparator duplicate() {
		return new PactRecordPairComparator(this.keyFields1, this.keyFields2, this.keyHolders1);
	}
}
