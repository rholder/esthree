package com.github.rholder.esthree;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.Headers;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.github.rholder.moar.concurrent.partition.Part;
import com.github.rholder.moar.concurrent.partition.Parts;
import com.google.common.primitives.Ints;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.junit.Assert;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AmazonS3ClientMockUtils {

    // MD5 of 100 'a' characters
    public static final String A_MD5 = "36a92cc94a9e0fa21f625f8bfb007adf";

    public static final long CONTENT_LENGTH = 100;

    public static AmazonS3Client createMockedClient(int chunkSize, int expectedChunks) throws IOException {

        // mock the ObjectMetadata for a 100 bytes of a's
        ObjectMetadata om = new ObjectMetadata();
        om.setContentLength(CONTENT_LENGTH);
        om.setHeader(Headers.ETAG, A_MD5) ;

        List<S3Object> os = new ArrayList<S3Object>();
        List<Part> parts = Parts.among(CONTENT_LENGTH, chunkSize);
        for(final Part p : parts) {
            // fake some content InputStream's by filling in chunks of a's
            S3Object o = mock(S3Object.class);
            S3ObjectInputStream input = new S3ObjectInputStream(
                    IOUtils.toInputStream(StringUtils.repeat("a", Ints.checkedCast(p.end - p.start + 1))),
                    null);
            // mock the returned content
            when(o.getObjectContent()).thenReturn(input);
            when(o.getObjectMetadata()).thenReturn(om);
            os.add(o);
        }
        Assert.assertEquals(expectedChunks, os.size());

        // mock the AmazonS3Client
        AmazonS3Client client = mock(AmazonS3Client.class);
        when(client.getObjectMetadata(anyString(), anyString())).thenReturn(om);
        when(client.getObject(any(GetObjectRequest.class)))
                .thenReturn(os.get(0), os.subList(1, os.size()).toArray(new S3Object[os.size() - 1]));

        return client;
    }
}
