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

/**
 */
public class SolrConverter {

  public SolrInputDocument convert(BranchItem branchItem, String leaveLine) {
    SolrInputDocument solrInputDocument = new SolrInputDocument();
    addItemToSolrDoc(branchItem, solrInputDocument);
    LeaveItem leaveItem = new LeaveItem(leaveLine);
    addItemToSolrDoc(leaveItem, solrInputDocument);
    solrInputDocument.setField("id", new StringBuilder(leaveItem.getKey()).append('-').append(branchItem.getKey()));
    return solrInputDocument;
  }

  private void addItemToSolrDoc(BranchItem branchItem, SolrInputDocument solrInputDocument) {
    for (String branchFieldName : branchItem.getKeyValueCouples().keySet()) {
      String branchFieldValue = branchItem.getKeyValueCouples().get(branchFieldName);

      if (branchFieldValue.startsWith("[") && branchFieldValue.endsWith("]"))
        branchFieldValue = branchFieldValue.replace("[", "").replace("]", "");

      if (solrInputDocument.getField(branchFieldName) == null)
        solrInputDocument.addField(branchFieldName.toLowerCase(), branchFieldValue);
      else
        solrInputDocument.getFieldValues(branchFieldName).add(branchFieldValue);
    }
  }
}
