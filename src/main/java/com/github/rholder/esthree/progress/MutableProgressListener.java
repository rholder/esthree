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

import com.amazonaws.event.ProgressListener;

/**
 * A {@link ProgressListener} implementation that allows customization of the
 * completed progress.
 */
public interface MutableProgressListener extends ProgressListener {

    /**
     * Set and listen to the given {@link Progress} implementation.
     *
     * @param progress the underlying implementation to watch
     * @return this {@link MutableProgressListener}
     */
    MutableProgressListener withTransferProgress(Progress progress);

    /**
     * The current completed percentage from 0.0 - 100.0.
     *
     * @param completed current percentage completed
     * @return this {@link MutableProgressListener}
     */
    MutableProgressListener withCompleted(Double completed);

    /**
     * The number to multiply the current transferred percentage by to determine
     * how far along the download is. This is useful when multiple chunks are
     * being fetched and each incoming chunk contributes only a percentage of
     * the total number of bytes expected to be transferred.
     *
     * @param multiplier multiply the completed percentage by this
     * @return this {@link MutableProgressListener}
     */
    MutableProgressListener withMultiplier(Double multiplier);
}
