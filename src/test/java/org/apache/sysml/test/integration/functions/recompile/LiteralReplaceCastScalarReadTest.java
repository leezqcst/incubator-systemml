/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.sysml.test.integration.functions.recompile;

import org.junit.Assert;
import org.junit.Test;

import org.apache.sysml.lops.UnaryCP;
import org.apache.sysml.parser.Expression.ValueType;
import org.apache.sysml.test.integration.AutomatedTestBase;
import org.apache.sysml.test.integration.TestConfiguration;
import org.apache.sysml.test.utils.TestUtils;
import org.apache.sysml.utils.Statistics;

/**
 * 
 */
public class LiteralReplaceCastScalarReadTest extends AutomatedTestBase 
{
	
	private final static String TEST_NAME = "LiteralReplaceCastScalar";
	private final static String TEST_DIR = "functions/recompile/";
	private final static String TEST_CLASS_DIR = TEST_DIR + 
		LiteralReplaceCastScalarReadTest.class.getSimpleName() + "/";
	
	@Override
	public void setUp() 
	{
		TestUtils.clearAssertionInformation();
		addTestConfiguration(TEST_NAME, new TestConfiguration(TEST_CLASS_DIR, TEST_NAME, new String[] { "R" }));
	}

	
	@Test
	public void testRemoveCastsInputInteger() 
	{
		runScalarCastTest(ValueType.INT);
	}
	
	@Test
	public void testRemoveCastsInputDouble() 
	{
		runScalarCastTest(ValueType.DOUBLE);
	}
	
	@Test
	public void testRemoveCastsInputBoolean() 
	{
		runScalarCastTest(ValueType.BOOLEAN);
	}


	/**
	 * 
	 * @param vt
	 */
	private void runScalarCastTest( ValueType vt )
	{	
		TestConfiguration config = getTestConfiguration(TEST_NAME);
		
		// input value
		String val = null;
		switch( vt ) {
			case INT: val = "7"; break;
			case DOUBLE: val = "7.3"; break;
			case BOOLEAN: val = "TRUE"; break;
			default: //do nothing
		}
		
		// This is for running the junit test the new way, i.e., construct the arguments directly
		String HOME = SCRIPT_DIR + TEST_DIR;
		fullDMLScriptName = HOME + TEST_NAME + ".dml";
		//note: stats required for runtime check of rewrite
		programArgs = new String[]{"-explain","-stats","-args", val };
		
		loadTestConfiguration(config);

		runTest(true, false, null, -1); 
		
		//CHECK cast replacement and sum replacement
		Assert.assertEquals(false, Statistics.getCPHeavyHitterOpCodes().contains(UnaryCP.CAST_AS_INT_OPCODE));
		Assert.assertEquals(false, Statistics.getCPHeavyHitterOpCodes().contains(UnaryCP.CAST_AS_DOUBLE_OPCODE));
		Assert.assertEquals(false, Statistics.getCPHeavyHitterOpCodes().contains(UnaryCP.CAST_AS_BOOLEAN_OPCODE));
		Assert.assertEquals(false, Statistics.getCPHeavyHitterOpCodes().contains("uak+")); //sum
	}

}