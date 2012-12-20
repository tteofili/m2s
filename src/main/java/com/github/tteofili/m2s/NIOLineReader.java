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

import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Arrays;

/**
 */
public class NIOLineReader {

  private static final byte LINE_END = (byte) ('\n' & 0xFF);
  private ByteBuffer buffer;
  private final FileChannel fc;

  public NIOLineReader(FileChannel fc, ByteBuffer buffer) {
    this.fc = fc;
    this.buffer = buffer;
  }

  public NIOLineReader(FileChannel fc) {
    this.fc = fc;
  }

  public byte[] nextLine(ByteBuffer byteBuffer) throws Exception {
    long startingPos = fc.position();
    int bytesRead = fc.read(byteBuffer);
    byteBuffer.flip();
    if (bytesRead > 0) {
      for (int i = byteBuffer.position(); i < byteBuffer.limit(); i++) {
        byte c = byteBuffer.get(i);
        if (c == LINE_END) {
          // reach the end
          byte[] car = byteBuffer.array();
          byte[] result = Arrays.copyOf(car, i - byteBuffer.position());
          fc.position(startingPos + i + 1);
          byteBuffer.clear();
          return result;
        }
      }
      byteBuffer.clear();
      return null;
    } else {
      byteBuffer.clear();
      return null;
    }

  }

  public byte[] nextLine() throws Exception {
    return nextLine(buffer);
  }

}
