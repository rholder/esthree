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

import com.google.common.util.concurrent.RateLimiter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class RateUtils {

    public static long rateLimitedCopy(RateLimiter rateLimiter,
                                       InputStream input,
                                       OutputStream output,
                                       byte[] buffer) throws IOException {
        long count = 0;
        int n;
        while (-1 != (n = input.read(buffer))) {
            rateLimiter.acquire(n);
            output.write(buffer, 0, n);
            count += n;
        }
        return count;
    }
}
