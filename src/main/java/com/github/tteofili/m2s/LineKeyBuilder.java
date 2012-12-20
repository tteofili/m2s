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

/**
 */
class LineKeyBuilder {

  String getBranchKey(String idIndex, String idLeaf) {
    return new StringBuilder(idIndex).append("-").append(idLeaf).toString();
  }

  String extractKey(String leaveLine, String idIndex, String idLeaf) {
    int s1 = leaveLine.indexOf(idIndex) + idIndex.length();
    String idIdx = leaveLine.substring(leaveLine.indexOf(":", s1) + 1, leaveLine.indexOf(",", s1)).trim();
    int s2 = leaveLine.indexOf(idLeaf) + idLeaf.length();
    String idL = leaveLine.substring(leaveLine.indexOf(":", s2) + 1, leaveLine.indexOf(",", s2)).trim();
    return getBranchKey(idIdx, idL);
  }
}
