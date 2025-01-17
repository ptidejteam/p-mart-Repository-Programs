/**
 * Copyright 2005 The Apache Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.nutch.mapReduce;

import org.apache.nutch.io.*;
import org.apache.nutch.ipc.*;
import org.apache.nutch.util.*;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.logging.*;

/** Runs a map task. */
class MapTaskRunner extends TaskRunner {
  public MapTaskRunner(Task task, TaskTracker tracker) {
    super(task, tracker);
  }
  /** Delete any temporary files from previous failed attempts. */
  public void prepare() throws IOException {
    MapOutputFile.removeAll(getTask().getTaskId());
  }

  /** Delete all of the temporary map output files. */
  public void close() throws IOException {
    LOG.info("Task "+getTask()+" done; removing files.");
    MapOutputFile.removeAll(getTask().getTaskId());
  }
}
