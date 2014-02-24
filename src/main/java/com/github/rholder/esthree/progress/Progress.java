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

package com.github.rholder.esthree.progress;

public interface Progress {

    /**
     * Return a percentage that's been transferred between 0.0 and 100.0.
     */
    double getPercentTransferred();

    long getBytesTransferred();

    void setTotalBytesToTransfer(long totalBytesToTransfer);

    long getTotalBytesToTransfer();

    void updateProgress(long bytes);
}
