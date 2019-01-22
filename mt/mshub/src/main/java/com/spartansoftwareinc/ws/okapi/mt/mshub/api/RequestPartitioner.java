package com.spartansoftwareinc.ws.okapi.mt.mshub.api;

import com.spartansoftwareinc.ws.okapi.mt.mshub.api.MicrosoftTextApiClient;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.idiominc.wssdk.component.mt.WSMTRequest;

/**
 * Partition Microsoft Custom Translator requests.
 */
public class RequestPartitioner {
    private static final Logger LOG = Logger.getLogger(RequestPartitioner.class);

    public List<List<WSMTRequest>> partition(WSMTRequest[] requests) throws UnsupportedEncodingException {
        if (requests.length == 0) {
            return new ArrayList<>();
        }

        List<List<WSMTRequest>> partitions = new ArrayList<>();

        List<WSMTRequest> currentPartition = new ArrayList<>();
        int currentSize = 0;

        for (WSMTRequest request : requests) {
            int size = request.getSource().trim().length();

            if (size == 0) {
                continue;
            }
            if (size > MicrosoftTextApiClient.MAX_TOTAL_CHARS_PER_REQUEST) {
                LOG.warn("Source text with size "+size+" cannot be added to a partition since it exceeds "
                        + "the maximum partition size of "+MicrosoftTextApiClient.MAX_TOTAL_CHARS_PER_REQUEST
                        + ": " + request.getSource());
                continue;
            }

            if (currentPartition.size() == MicrosoftTextApiClient.MAX_STRINGS_PER_REQUEST
                    || currentSize + size > MicrosoftTextApiClient.MAX_TOTAL_CHARS_PER_REQUEST) {
                partitions.add(currentPartition);
                currentPartition = new ArrayList<>();
                currentSize = 0;
            }

            currentPartition.add(request);
            currentSize += size;
        }

        if (!currentPartition.isEmpty()) {
            partitions.add(currentPartition);
        }
        return partitions;
    }
}
