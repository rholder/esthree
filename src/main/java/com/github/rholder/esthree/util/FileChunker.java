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

import java.util.ArrayList;
import java.util.List;

public abstract class FileChunker {

    public static List<FilePart> chunk(long contentLength, long chunkSize) {
        List<FilePart> fileParts = new ArrayList<FilePart>();
        long totalParts = (long) Math.ceil(contentLength / (chunkSize * 1.0));

        for(int i = 0; i < totalParts; i++) {
            long start = i * chunkSize;
            long end = Math.min(start + chunkSize, contentLength) - 1;

            fileParts.add(new FilePart(start, end));
        }
        return fileParts;
    }

    public static class FilePart {
        public FilePart(long start, long end) {
            this.start = start;
            this.end = end;
        }
        public final long start;
        public final long end;
    }
}
