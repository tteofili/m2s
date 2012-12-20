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

import java.util.HashMap;
import java.util.regex.Matcher;

/**
 */
public class LeaveItem extends BranchItem {

  LeaveItem(String[] tokens) {
    this.lineKeyBuilder = new LineKeyBuilder();
    this.keyValueCouples = new HashMap<String, String>();
    for (int i = 0; i + 1 < tokens.length; i = i + 2) {
      String k = tokens[i].replaceAll("\"", "").trim();
      String v = tokens[i + 1].replaceAll("\\[", "").replaceAll("\\]", "");
      this.keyValueCouples.put(k, v);
    }
  }

  public LeaveItem(String line) {
    this.lineKeyBuilder = new LineKeyBuilder();
    this.keyValueCouples = new HashMap<String, String>();
    Matcher m = LinePatternProvider.getPattern().matcher(line.substring(1, line.length() - 2));
    while (m.find()) {
      this.keyValueCouples.put(m.group(1), m.group(2));

    }
  }

  @Override
  String getKey() {
    return this.getKeyValueCouples().get("iddocumento");
  }
}
