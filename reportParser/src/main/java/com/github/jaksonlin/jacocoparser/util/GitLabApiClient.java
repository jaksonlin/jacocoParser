package com.github.jaksonlin.jacocoparser.util;

import java.util.List;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.apache.http.HttpResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.jaksonlin.jacocoparser.model.GitLabCompareResponse;
import com.github.jaksonlin.jacocoparser.model.GitLabDiff;

public class GitLabApiClient {
    private final String baseUrl;
    private final String privateToken;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public GitLabApiClient(String baseUrl, String privateToken) {
        this.baseUrl = baseUrl;
        this.privateToken = privateToken;
        this.httpClient = HttpClientBuilder.create().build();
        this.objectMapper = new ObjectMapper();
    }

    public GitLabCompareResponse compareRepositories(String projectId, String from, String to) throws Exception {
        String url = String.format("%s/api/v4/projects/%s/repository/compare?from=%s&to=%s&straight=true",
                baseUrl, projectId, from, to);
        HttpGet request = new HttpGet(url);
        if (privateToken != null) {
            request.addHeader("PRIVATE-TOKEN", privateToken);
        }

        HttpResponse response = httpClient.execute(request);

        if (response.getStatusLine().getStatusCode() != 200) {
            throw new RuntimeException("Failed to get comparison: " + EntityUtils.toString(response.getEntity()));
        }
        return objectMapper.readValue(response.getEntity().getContent(), GitLabCompareResponse.class);
    }

    public List<GitLabDiff> getDiffs(String projectId, String from, String to) throws Exception {
        GitLabCompareResponse compareResponse = compareRepositories(projectId, from, to);
        return compareResponse.getDiffs();
    }
}