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

import org.apache.solr.common.SolrInputDocument;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

/**
 */
public class SolrConverterTest {

  @Test
  public void testConversion() throws Exception {
    String leaveLine = "{ \"_id\" : { \"$oid\" : \"4e6885fbb1149bd3c48e61f7\" }, \"iddocumento\" : 9488299, \"Pacchetti\" : [ 110, 146, 181, 182, 2006, 2007, 2048, 2267 ], \"iddoctype\" : 3, \"idLeaf\" : 3, \"idIndex\" : 854, \"collection\" : \"DocType3\" }";
    String branchLine = "{ \"_id\" : { \"$oid\" : \"4e6885fb0324990d8ce1aded\" }, \"idLeaf\" : 3, \"historyTree\" : 0, \"idIndex\" : 854, \"idParent\" : 5, \"text\" : \"kkk\", \"fd\" : -1, \"Fonti\" : [], \"DocType\" : [], \"Pacchetti\" : [] }";
    SolrConverter solrConverter = new SolrConverter();
    SolrInputDocument solrInputDocument = solrConverter.convert(new BranchItem(branchLine.split(",")), leaveLine);
    assertNotNull(solrInputDocument);
  }
}
