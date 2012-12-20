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

import java.io.File;
import java.io.FileInputStream;
import java.nio.ByteBuffer;

import static org.junit.Assert.*;

/**
 */
public class NIOLineReaderTest {
  @Test
  public void testFirstLinesRead() throws Exception {
    NIOLineReader nioLineReader = new NIOLineReader(new FileInputStream(new File(getClass().
            getResource("/MiniTree.json").getFile())).getChannel(), ByteBuffer.allocate(1024));
    for (int i = 0; i < 999; i++) {
      byte[] line = nioLineReader.nextLine();
      assertNotNull(line);
      assertTrue(i + " line is empty", line.length > 0);
    }
  }

  @Test
  public void testLastLinesRead() throws Exception {
    NIOLineReader nioLineReader = new NIOLineReader(new FileInputStream(new File(getClass().
            getResource("/MicroTree.json").getFile())).getChannel());
    for (int i = 0; i < 10; i++) {
      byte[] line = nioLineReader.nextLine(ByteBuffer.allocate(1024));
      if (i < 7) {
        assertNotNull(i + " line is null", line);
        assertTrue(line.length > 0);
      }
    }
  }
}
