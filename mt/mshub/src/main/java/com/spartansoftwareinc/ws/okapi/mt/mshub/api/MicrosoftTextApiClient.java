package com.spartansoftwareinc.ws.okapi.mt.mshub.api;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Microsoft Translator Text API v3 client.
 */
public class MicrosoftTextApiClient {
    private static final Logger LOG = Logger.getLogger(MicrosoftTextApiClient.class);

    // https://docs.microsoft.com/en-us/azure/cognitive-services/translator/reference/v3-0-translate?tabs=curl
    public static final int MAX_STRINGS_PER_REQUEST = 25;
    public static final int MAX_TOTAL_CHARS_PER_REQUEST = 4800; // API limit is actually 5000, but allow for some padding

    private final String baseURL;

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public MicrosoftTextApiClient() {
        this.baseURL = "https://api.cognitive.microsofttranslator.com/translate";

        int TIMEOUT = 300000;
        this.httpClient = HttpClientBuilder.create()
                .setDefaultRequestConfig(RequestConfig.custom()
                        .setConnectTimeout(TIMEOUT)
                        .setSocketTimeout(TIMEOUT)
                        .setConnectionRequestTimeout(TIMEOUT)
                        .build())
                .build();
        this.objectMapper = new ObjectMapper();
    }

    public String getBaseURL() {
        return baseURL;
    }

    public List<TranslateResponse> translate(List<String> texts, String source, String target,
            String subscriptionKey, String category) throws URISyntaxException, ClientProtocolException, IOException {

        URIBuilder uriBuilder = new URIBuilder(getBaseURL());
        uriBuilder.addParameter("api-version", "3.0");
        uriBuilder.addParameter("from", source);
        uriBuilder.addParameter("to", target);
        if (category != null && !category.isEmpty()) {
            uriBuilder.addParameter("category", category);
        }
        HttpPost httpPost = new HttpPost(uriBuilder.build());

        httpPost.addHeader("Content-Type", "application/json");
        httpPost.addHeader("Ocp-Apim-Subscription-Key", subscriptionKey);
        httpPost.addHeader("X-ClientTraceId", UUID.randomUUID().toString());

        List<TranslateRequest> translateRequests = new ArrayList<>();
        for (String text : texts) {
            translateRequests.add(new TranslateRequest(text));
        }
        String requestBody = objectMapper.writeValueAsString(translateRequests);
        // UTF-8 is required, otherwise translation will fail on source texts like "Español"
        httpPost.setEntity(new StringEntity(requestBody,
                ContentType.create(ContentType.APPLICATION_JSON.getMimeType(), StandardCharsets.UTF_8)));

        return execute(httpPost, new TypeReference<List<TranslateResponse>>(){}, requestBody);
    }

    private <T> T execute(HttpUriRequest request, TypeReference<T> responseType, String requestBody)
            throws IOException {
        HttpResponse response = httpClient.execute(request);

        // Read as a string first so that in an error case, we can log the response in a readable format
        String responseBody = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);

        if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
            String message = String.format("Call to %s \"%s\" failed with code \"%d\", reason \"%s\", and response"
                    + " body %s for request body %s",
                    request.getMethod(),
                    request.getURI().toString(),
                    response.getStatusLine().getStatusCode(),
                    response.getStatusLine().getReasonPhrase(),
                    responseBody,
                    requestBody);
            throw new IOException(message);
        }

        try {
            return objectMapper.readValue(responseBody, responseType);
        } catch (IOException e) {
            LOG.error("Unable to deserialize response: " + responseBody);
            throw e;
        }
    }
}
