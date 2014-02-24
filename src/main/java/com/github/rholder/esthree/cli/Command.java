/*
 * Copyright 2014 Ray Holder
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.rholder.esthree.cli;

public interface Command {

    /**
     * Return the name of the command (e.g. "ls" or "put")
     *
     * @return name of the command
     */
    String getName();

    /**
     * Execute the given command and return a 0 for success or non-zero for a failure.
     *
     * @return zero on success, non-zero on failure
     */
    int execute();
}
