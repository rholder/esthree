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

import java.util.List;

import static com.github.rholder.esthree.util.FileChunker.FilePart;

public class FileChunkerTest extends FileChunker {

    @Test
    public void oddParts() {
        long contentLength = 73;
        long chunkSize = 22;

        List<FilePart> parts = FileChunker.chunk(contentLength, chunkSize);

        Assert.assertEquals(4, parts.size());

        FilePart fp0 = parts.get(0);
        Assert.assertEquals(fp0.start, 0);
        Assert.assertEquals(fp0.end, 21); // 22 bytes

        FilePart fp1 = parts.get(1);
        Assert.assertEquals(fp1.start, 22);
        Assert.assertEquals(fp1.end, 43); // 22 bytes?

        FilePart fp2 = parts.get(2);
        Assert.assertEquals(fp2.start, 44);
        Assert.assertEquals(fp2.end, 65);

        FilePart fp3 = parts.get(3);
        Assert.assertEquals(fp3.start, 66);
        Assert.assertEquals(fp3.end, 72);
    }

    @Test
    public void annoyinglyAlignedParts() {
        long contentLength = 26;
        long chunkSize = 13;

        List<FilePart> parts = FileChunker.chunk(contentLength, chunkSize);

        Assert.assertEquals(2, parts.size());

        FilePart fp0 = parts.get(0);
        Assert.assertEquals(fp0.start, 0);
        Assert.assertEquals(fp0.end, 12);

        FilePart fp1 = parts.get(1);
        Assert.assertEquals(fp1.start, 13);
        Assert.assertEquals(fp1.end, 25);
    }

    @Test
    public void singlePartUnderChunkSize() {
        long contentLength = 17;
        long chunkSize = 30;

        List<FilePart> parts = FileChunker.chunk(contentLength, chunkSize);

        Assert.assertEquals(1, parts.size());

        FilePart fp0 = parts.get(0);
        Assert.assertEquals(fp0.start, 0);
        Assert.assertEquals(fp0.end, 16);
    }

    @Test
    public void singleAlignedPart() {
        long contentLength = 17;
        long chunkSize = 17;

        List<FilePart> parts = FileChunker.chunk(contentLength, chunkSize);

        Assert.assertEquals(1, parts.size());

        FilePart fp0 = parts.get(0);
        Assert.assertEquals(fp0.start, 0);
        Assert.assertEquals(fp0.end, 16);
    }
}
