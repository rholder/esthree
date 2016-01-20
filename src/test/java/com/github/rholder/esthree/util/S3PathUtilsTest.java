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

import org.junit.Assert;
import org.junit.Test;

public class S3PathUtilsTest extends S3PathUtils {

    @Test
    public void checkBucket() {
        Assert.assertNull(getBucket(""));
        Assert.assertNull(getBucket("foo"));
        Assert.assertNull(getBucket("s3://"));
        Assert.assertNull(getBucket("s3:///"));
        Assert.assertNull(getBucket("S3://foo"));
        Assert.assertNull(getBucket("s3:/foo"));
        Assert.assertNull(getBucket("s3//foo"));
        Assert.assertNull(getBucket("s3:/"));
        Assert.assertNull(getBucket("s3:/foo"));

        Assert.assertEquals("foo", getBucket("s3://foo"));
        Assert.assertEquals("foo", getBucket("s3://foo/"));
        Assert.assertEquals("foo", getBucket("s3://foo/bar"));
        Assert.assertEquals("foo", getBucket("s3://foo/bar/baz"));
    }

    @Test
    public void checkPrefix() {
        Assert.assertNull(getPrefix("foo"));
        Assert.assertNull(getPrefix("S3://foo"));
        Assert.assertNull(getPrefix("s3:/foo"));
        Assert.assertNull(getPrefix("s3//foo"));
        Assert.assertNull(getPrefix("s3:/"));
        Assert.assertNull(getPrefix("s3:/foo"));

        Assert.assertNull(getPrefix("s3://foo"));
        Assert.assertNull(getPrefix("s3://foo/"));

        Assert.assertEquals("bar", getPrefix("s3://foo/bar"));
        Assert.assertEquals("bar/", getPrefix("s3://foo/bar/"));
        Assert.assertEquals("bar/baz", getPrefix("s3://foo/bar/baz"));
        Assert.assertEquals("bar/baz/", getPrefix("s3://foo/bar/baz/"));
    }

    @Test
    public void checkInferredFilename() {
        Assert.assertNull(getFilename("foo"));
        Assert.assertNull(getFilename("S3://foo"));
        Assert.assertNull(getFilename("s3:/foo"));
        Assert.assertNull(getFilename("s3//foo"));
        Assert.assertNull(getFilename("s3:/"));
        Assert.assertNull(getFilename("s3:/foo"));

        Assert.assertNull(getFilename("s3://foo"));
        Assert.assertNull(getFilename("s3://foo/"));
        Assert.assertNull(getFilename("s3://foo/bar/"));
        Assert.assertNull(getFilename("s3://foo/bar/baz/"));
        Assert.assertNull(getFilename("s3://foo/bar/baz//"));
        Assert.assertNull(getFilename("s3://foo/bar/baz///"));

        Assert.assertEquals("bar", getFilename("s3://foo/bar"));
        Assert.assertEquals("baz", getFilename("s3://foo/bar/baz"));

        Assert.assertEquals("bar.txt", getFilename("s3://foo/baz/bar.txt"));
    }
}
