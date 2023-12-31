/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.lucene;


/** Lucene's package information, including version. **/
public final class LucenePackage {

  private LucenePackage() {}                      // can't construct

  /** Return Lucene's package, including version information. */
  public static Package get() {
    return LucenePackage.class.getPackage();
  }

  public static class Log {
    public void write(String text) {
      System.out.println(text);
    }
    
    public void write(Exception e) {
      e.printStackTrace();
    }

    public void write(Throwable e) {
      e.printStackTrace();
    }
  }

  private static Log __log;

  public static Log log() {
    return __log;
  }

  public static void log(Log v) {
    __log = v;
  }

  public static void writeLog(String text) {
    if (__log == null) {
      System.out.println(text);
    } else {
      __log.write(text);
    }
  }
  
  public static void writeLog(Exception e) {
    if (__log == null) {
      e.printStackTrace();
    } else {
      __log.write(e);
    }
  }

  public static void writeLog(Throwable e) {
    if (__log == null) {
      e.printStackTrace();
    } else {
      __log.write(e);
    }
  }    
}
