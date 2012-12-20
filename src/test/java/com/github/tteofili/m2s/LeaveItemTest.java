/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.tteofili.m2s;

import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 */
public class LeaveItemTest {

  @Test
  public void testCreation() throws Exception {
    String line = "{ \"_id\" : { \"$oid\" : \"4e6885fbb1149bd3c48e61f3\" }, \"iddocumento\" : 4517193, \"Pacchetti\" : [ 177, 2048 ], \"iddoctype\" : 3, \"idLeaf\" : 4, \"idIndex\" : 854, \"collection\" : \"DocType3\" }";
    LeaveItem leaveItem = new LeaveItem(line);
    assertTrue(leaveItem.getKey() != null);
  }
}
