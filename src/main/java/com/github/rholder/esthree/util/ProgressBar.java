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

package com.github.rholder.esthree.util;

/**
 * Progress bar hackery can be found in here.
 */
public class ProgressBar {

    /**
     * Return a String that represents a progress bar from 0 - 100 percent complete.
     *
     * @param percent generate a bar for this percentage complete
     */
    public static String generate(int percent) {
        StringBuilder bar = new StringBuilder("[");

        // if this is over 100, then don't overflow
        if(percent > 100) {
            percent = 99;
        }

        // 50 bars are 100%
        for (int i = 0; i < 50; i++) {
            if (i < (percent / 2)) {
                bar.append("=");
            } else if (i == (percent / 2)) {
                bar.append(">");
            } else {
                bar.append(" ");
            }
        }

        bar.append("] ").append(String.format("%3s%%", percent));
        return bar.toString();
    }

    /**
     * Return a String that represents a human readable byte count. Adapted from:
     * http://stackoverflow.com/questions/3758606/how-to-convert-byte-size-into-human-readable-format-in-java
     *
     * @param bytes bytes to convert
     * @param si    use SI units when true (1000 = 1kB), otherwise binary (1024 = 1KiB)
     */
    public static String humanReadableByteCount(long bytes, boolean si) {
        int unit = si ? 1000 : 1024;
        if (bytes < unit) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp - 1) + (si ? "" : "i");
        return String.format("%.2f %sB", bytes / Math.pow(unit, exp), pre);
    }
}
