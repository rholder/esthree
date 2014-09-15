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
import java.util.concurrent.TimeUnit;

import static com.amazonaws.event.ProgressEventType.TRANSFER_COMPLETED_EVENT;
import static com.amazonaws.event.ProgressEventType.TRANSFER_STARTED_EVENT;
import static com.github.rholder.esthree.util.ProgressBar.generate;
import static com.github.rholder.esthree.util.ProgressBar.humanReadableByteCount;
import static com.google.common.primitives.Ints.saturatedCast;
import static java.lang.Math.round;

public class PrintingProgressListener extends SyncProgressListener implements MutableProgressListener {

    public static final long ONE_S_AS_NS = TimeUnit.SECONDS.toNanos(1);
    public static final long UPDATE_ETA_EVERY_NS = 2 * ONE_S_AS_NS;

    public volatile Progress progress;
    public PrintStream out;
    public Double completed;
    public Double multiplier;
    public Long startTime;
    public long lastTimeLeft;
    public TimeProvider timeProvider;
    public Long lastTime;
    public Long lastBytes;

    public PrintingProgressListener(PrintStream out, TimeProvider timeProvider) {
        this.out = out;
        this.multiplier = 1.0;
        this.completed = 0.0;
        this.lastTimeLeft = 0;
        this.timeProvider = timeProvider;
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
            long timeLeft = getTimeLeft();
            if (lastTimeLeft < 1 && timeLeft > 0) {
                // prime this value with a sane starting point
                lastTimeLeft = timeLeft;
            }

            // use an exponential moving average to smooth the estimate
            lastTimeLeft += 0.90 * (timeLeft - lastTimeLeft);

            out.print(String.format("\r%1$s  %2$s / %3$s  %4$s      ",
                    generate(saturatedCast(round(completed + (progress.getPercentTransferred() * multiplier)))),
                    humanReadableByteCount(progress.getBytesTransferred(), true),
                    humanReadableByteCount(progress.getTotalBytesToTransfer(), true), fromSeconds(lastTimeLeft)));
            out.flush();
        }
    }

    /**
     * Return the estimated number of seconds left to complete the transfer.
     *
     * @return an estimate, in seconds
     */
    public long getTimeLeft() {
        long now = timeProvider.now();
        if (lastTime == null) {
            lastTime = now;
        }

        long currentBytes = progress.getTotalBytesToTransfer() - progress.getBytesTransferred();
        if(lastBytes == null) {
            lastBytes = currentBytes;
        }

        long diffTime = now - lastTime;
        long estimate = lastTimeLeft;

        if (diffTime > UPDATE_ETA_EVERY_NS && currentBytes > 0) {
            // bytes per ns
            double measuredSpeed = (lastBytes - currentBytes) / (double)(diffTime);

            // bytes left / bytes per ns, converted to s
            estimate = (long)((currentBytes / measuredSpeed) / ONE_S_AS_NS);

            lastTime = now;
            lastBytes = currentBytes;
        }

        return estimate;
    }

    public static String fromSeconds(long seconds) {
        long s = seconds % 60;
        long m = (seconds / 60) % 60;
        long h = (seconds / (60 * 60)) % 24;
        return String.format("%02d:%02d:%02d", h, m, s);
    }
}
