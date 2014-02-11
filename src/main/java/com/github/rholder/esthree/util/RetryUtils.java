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

import com.amazonaws.AmazonServiceException;
import com.github.rholder.retry.Retryer;
import com.github.rholder.retry.RetryerBuilder;
import com.github.rholder.retry.StopStrategies;
import com.github.rholder.retry.WaitStrategies;
import com.google.common.base.Predicate;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class RetryUtils {

    /**
     * Meant for use with Amazon services that may sometimes indicate retry is appropriate (500s), or eventual
     * consistency behavior (404). Failures can actually be both service side (e.g. "conflicting operation in progress")
     * or client side (e.g. "flooding").
     */
    public static final Retryer<Object> AWS_RETRYER = RetryerBuilder.newBuilder()
            .retryIfException(new Predicate<Throwable>() {
                @Override
                public boolean apply(Throwable input) {
                    // 500-series may be retried http://aws.amazon.com/articles/Amazon-S3/1904
                    Throwable t = input.getCause();
                    if (t instanceof AmazonServiceException) {
                        int statusCode = ((AmazonServiceException) t).getStatusCode();
                        // not found (waiting for eventual consistency) or service exception
                        return isEqual(statusCode, 404) || isEqual(statusCode / 100, 5);
                    } else {
                        return false;
                    }
                }
            })
            .retryIfExceptionOfType(IOException.class) // hard drive failed, end of days, etc.
            .withWaitStrategy(WaitStrategies.exponentialWait(1000, 5, TimeUnit.SECONDS))
            .withStopStrategy(StopStrategies.stopAfterAttempt(5))
            .build();


    public static boolean isEqual(Object object1, Object object2) {
        if (object1 == object2) {
            return true;
        }
        if ((object1 == null) || (object2 == null)) {
            return false;
        }
        return object1.equals(object2);
    }
}
