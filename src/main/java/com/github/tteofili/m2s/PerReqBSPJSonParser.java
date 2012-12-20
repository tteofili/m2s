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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.NullWritable;
import org.apache.hama.bsp.BSP;
import org.apache.hama.bsp.BSPPeer;
import org.apache.hama.bsp.message.type.IntegerMessage;
import org.apache.hama.bsp.sync.SyncException;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;

import com.github.tteofili.m2s.NIOLineReader;

/**
 */
public class PerReqBSPJSonParser extends BSP<NullWritable, NullWritable, NullWritable, NullWritable, IntegerMessage> {

  private static final Integer BUFFER_SIZE = 1024;
  private static final Log log = LogFactory.getLog(PerReqBSPJSonParser.class);
  private String masterTask;
  private SolrServer solrServer;

  @Override
  public void bsp(BSPPeer<NullWritable, NullWritable, NullWritable, NullWritable, IntegerMessage> peer)
          throws IOException, SyncException, InterruptedException {

    Configuration conf = peer.getConfiguration();
    if (peer.getPeerName().equals(masterTask)) {
      /* master */

      // first super step : send a line at a time

      String branchFilePath = conf.get("branch.file.path");
      File branchFile = new File(branchFilePath);
      FileChannel bc = new FileInputStream(branchFile).getChannel();
      int i = 1;
      NIOLineReader nioLineReader = new NIOLineReader(bc, ByteBuffer.allocate(BUFFER_SIZE));
      byte[] nextLine;
      try {
        while ((nextLine = nioLineReader.nextLine()) != null) {
          IntegerMessage integerMessage = new IntegerMessage(new String(nextLine), i);
          String otherPeer = getNextSlaveName(peer, i);
          peer.send(otherPeer, integerMessage);
          i++;
          if (nextLine == null || nextLine.length == 0) {
            bc.close();
            break;
          }
        }
      } catch (Exception e) {
        log.warn("exception at iteration " + i + " due to " + e.getLocalizedMessage());
      }
      log.info("sent " + i + " lines to " + (peer.getNumPeers() - 1) + " slave peers");

      if (bc.isOpen())
        bc.close();

      peer.sync();
      peer.sync();
      peer.sync();

    } else {
      /* slave */

      // first super step: wait for the master to send the lines

      peer.sync();

      // second superstep : receive the branch lines and parse them
      log.info(peer.getPeerName() + " is handling " + peer.getNumCurrentMessages() + " branch lines");
      IntegerMessage received;
      Map<String, BranchItem> branchItemMap = new HashMap<String, BranchItem>();

      while ((received = (IntegerMessage) peer.getCurrentMessage()) != null) {
        try {
          String tag = received.getTag();
          BranchItem branchItem = new BranchItem(tag.split(","));
          // ask for the leaves of a branch
          String branchKey = branchItem.getKey();
          branchItemMap.put(branchKey, branchItem);
        } catch (Exception e) {
          log.warn("error while processing line:\n" + received.getTag());
        }
      }

      log.info("collected branch lines");
      peer.sync();

      // third superstep : iterate over the leaves lines
      SolrConverter solrConverter = new SolrConverter();
      String leavesFilePath = conf.get(("leaves.file.path"));
      FileChannel lc = new FileInputStream(new File(leavesFilePath)).getChannel();
      NIOLineReader nioLineReader = new NIOLineReader(lc, ByteBuffer.allocate(BUFFER_SIZE));
      LineKeyBuilder lineKeyBuilder = new LineKeyBuilder();
      byte[] nextLine;
      try {
        while ((nextLine = nioLineReader.nextLine()) != null) {
          String leaveLine = new String(nextLine);
          String key = lineKeyBuilder.extractKey(leaveLine, "idIndex", "idLeaf");
          BranchItem branchItem = branchItemMap.get(key);

          if (branchItem != null) {
            try {
              solrServer.add(solrConverter.convert(branchItem, leaveLine));
            } catch (Exception e) {
              log.warn("could not add a document");
            }
          }

          if (nextLine == null || nextLine.length == 0) {
            lc.close();
            break;
          }
        }
      } catch (Throwable e) {
        log.warn(e.getLocalizedMessage());
      }
      log.info("slave finished");
      peer.sync();
    }
  }

  private String getNextSlaveName(BSPPeer peer, int i) {
    String name = peer.getAllPeerNames()[i % peer.getNumPeers()];
    if (name.equals(masterTask)) {
      i++;
      name = peer.getAllPeerNames()[i % peer.getNumPeers()];
    }
    return name;
  }

  @Override
  public void setup(BSPPeer<NullWritable, NullWritable, NullWritable, NullWritable, IntegerMessage> peer) throws IOException {
    // Choose one as a master
    this.masterTask = peer.getAllPeerNames()[0];
    // initialize the solr server
    this.solrServer = new HttpSolrServer(peer.getConfiguration().get("solr.url"));
  }


  @Override
  public void cleanup(BSPPeer<NullWritable, NullWritable, NullWritable, NullWritable, IntegerMessage> peer) throws IOException {
    if (peer.getPeerName().equals(masterTask)) {
      log.info("cleaning up the master");
      try {
        this.solrServer.commit();
        this.solrServer.optimize();
      } catch (SolrServerException e) {
        try {
          this.solrServer.rollback();
        } catch (SolrServerException e1) {
          log.error(e1.getLocalizedMessage());
        }
      }
    }

  }
}