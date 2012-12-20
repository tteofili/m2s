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
import java.util.Map;

/**
 */
public class BranchItem {
  Map<String, String> keyValueCouples;
  LineKeyBuilder lineKeyBuilder;

  BranchItem(Map<String, String> keyValueCouples) {
    this.lineKeyBuilder = new LineKeyBuilder();
    this.keyValueCouples = keyValueCouples;
  }

  BranchItem(String[] keyValueCouples) {
    this.lineKeyBuilder = new LineKeyBuilder();
    this.keyValueCouples = new HashMap<String, String>();
    for (String keyValueCoupleString : keyValueCouples) {
      String[] keyValueArray = keyValueCoupleString.split(":");
      String k = keyValueArray[0].replaceAll("\"", "").trim();
      String v;
      if (keyValueArray.length >= 1) {
        StringBuilder vb = new StringBuilder();
        for (int i = 1; i < keyValueArray.length; i++) {
          vb.append(keyValueArray[i]);
        }
        v = vb.toString().trim();
      } else
        v = "";
      this.keyValueCouples.put(k, v);
    }
  }

  BranchItem() {
  }

  Map<String, String> getKeyValueCouples() {
    return keyValueCouples;
  }

  String getKey() {
    return lineKeyBuilder.getBranchKey(this.keyValueCouples.get("idIndex"), this.keyValueCouples.get("idLeaf"));
  }
}
