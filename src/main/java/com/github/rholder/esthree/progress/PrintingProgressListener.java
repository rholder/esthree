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

import com.amazonaws.event.ProgressEvent;
import com.amazonaws.event.ProgressEventType;
import com.amazonaws.event.SyncProgressListener;

import java.io.PrintStream;

import static com.amazonaws.event.ProgressEventType.TRANSFER_COMPLETED_EVENT;
import static com.amazonaws.event.ProgressEventType.TRANSFER_STARTED_EVENT;
import static com.github.rholder.esthree.util.ProgressBar.generate;
import static com.github.rholder.esthree.util.ProgressBar.humanReadableByteCount;
import static com.google.common.primitives.Ints.saturatedCast;
import static java.lang.Math.round;

public class PrintingProgressListener extends SyncProgressListener implements MutableProgressListener {

    public volatile Progress progress;
    public PrintStream out;
    public Double completed;
    public Double multiplier;

    public PrintingProgressListener(PrintStream out) {
        this.out = out;
        this.multiplier = 1.0;
        this.completed = 0.0;
    }

    public PrintingProgressListener withTransferProgress(Progress progress) {
        this.progress = progress;
        return this;
    }

    public PrintingProgressListener withCompleted(Double completed) {
        this.completed = completed;
        return this;
    }

    public PrintingProgressListener withMultiplier(Double multiplier) {
        this.multiplier = multiplier;
        return this;
    }

    @Override
    public void progressChanged(ProgressEvent progressEvent) {
        ProgressEventType type = progressEvent.getEventType();
        if (type.equals(TRANSFER_COMPLETED_EVENT) || type.equals(TRANSFER_STARTED_EVENT)) {
            out.println();
        }

        if (type.isByteCountEvent()) {
            out.print(String.format("\r%1$s %2$10s / %3$s",
                    generate(saturatedCast(round(completed + (progress.getPercentTransferred() * multiplier)))),
                    humanReadableByteCount(progress.getBytesTransferred(), true),
                    humanReadableByteCount(progress.getTotalBytesToTransfer(), true)));
            out.flush();
        }
    }
}
