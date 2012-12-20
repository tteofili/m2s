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

import org.apache.hama.HamaConfiguration;
import org.apache.hama.bsp.*;

/**
 */
public class MongoSolrIndexerJob {

  public void parse(int tasks, String branchFilePath, String leavesFilePath, Class<? extends BSP> parserJobClass) throws Exception {
    // BSP job configuration
    HamaConfiguration conf = new HamaConfiguration();
    conf.set("branch.file.path", branchFilePath);
    conf.set("leaves.file.path", leavesFilePath);
    conf.set("solr.url","http://localhost:8983/solr/");
    BSPJob bsp = new BSPJob(conf, MongoSolrIndexerJob.class);
    bsp.setJobName("JSoN MongoDB Parser");
    bsp.setBspClass(parserJobClass);
    bsp.setInputFormat(NullInputFormat.class);
    bsp.setOutputFormat(NullOutputFormat.class);
    BSPJobClient jobClient = new BSPJobClient(conf);
    if (tasks <= 2) {
      ClusterStatus cluster = jobClient.getClusterStatus(false);
      bsp.setNumBspTask(cluster.getMaxTasks());
    } else
      bsp.setNumBspTask(tasks);

    long startTime = System.currentTimeMillis();
    if (bsp.waitForCompletion(true)) {
      System.out.println("Job Finished in "
              + (double) (System.currentTimeMillis() - startTime) / 1000.0
              + " seconds");
    }
  }

}
