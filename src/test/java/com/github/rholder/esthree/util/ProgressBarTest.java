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

import org.junit.Test;

import static com.github.rholder.esthree.util.ProgressBar.generate;
import static com.github.rholder.esthree.util.ProgressBar.humanReadableByteCount;

public class ProgressBarTest {

    @Test
    public void checkGenerate() throws InterruptedException {
        for(int i = 0; i < 101; i++) {
            System.out.print(generate(i) + "\r");
            Thread.sleep(100);
        }
    }

    @Test
    public void checkGenerateOverflow() throws InterruptedException {
        for(int i = 0; i < 143; i++) {
            System.out.print(generate(i) + "\r");
            Thread.sleep(100);
        }
    }

    @Test
    public void checkHumanReadable() {
        System.out.println(humanReadableByteCount(0, true));
        System.out.println(humanReadableByteCount(27, true));
        System.out.println(humanReadableByteCount(999, true));
        System.out.println(humanReadableByteCount(1000, true));
        System.out.println(humanReadableByteCount(1023, true));
        System.out.println(humanReadableByteCount(1024, true));
        System.out.println(humanReadableByteCount(1728, true));
        System.out.println(humanReadableByteCount(110592, true));
        System.out.println(humanReadableByteCount(7077888, true));
        System.out.println(humanReadableByteCount(452984832, true));
        System.out.println(humanReadableByteCount(28991029248L, true));
        System.out.println(humanReadableByteCount(1855425871872L, true));
        System.out.println(humanReadableByteCount(9223372036854775807L, true));
    }
}
