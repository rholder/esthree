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
 * Here's a collection of S3 path munging methods.
 */
public abstract class S3PathUtils {

    public static final String S3_PROTOCOL = "s3://";

    /**
     * Return the bucket from an S3 path string (e.g. foo from s3://foo/bar).
     *
     * @param s3format an s3 path string
     */
    public static String getBucket(String s3format) {
        if(S3_PROTOCOL.equals(s3format) || "".equals(s3format)) {
            return null;
        }

        String bucket = null;
        if (s3format.startsWith(S3_PROTOCOL)) {
            String[] split = s3format.replaceFirst(S3_PROTOCOL, "").split("/");
            bucket = split.length > 0 ? split[0] : null;
        }
        return bucket;
    }

    /**
     * Return the prefix from an S3 path string (e.g. bar from s3://foo/bar).
     *
     * @param s3format an s3 path string
     */
    public static String getPrefix(String s3format) {
        String prefix = null;
        String bucket = getBucket(s3format);

        // no bucket means the input is wonky anyway, just bail out
        if (bucket != null) {

            // rip out the bucket
            String parsed = s3format.substring(S3_PROTOCOL.length() + bucket.length(), s3format.length());

            // skip leading "/", an empty prefix remains null
            if (parsed.length() > 1) {
                prefix = parsed.substring(1, parsed.length());
            }
        }
        return prefix;
    }
}
