/*
 * Copyright (c) 2016 Ivo Woltring
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package nl.ivonet.isbndb;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import nl.ivonet.isbndb.data.AccessKeyResource;
import nl.ivonet.isbndb.model.Keystats;
import nl.ivonet.isbndb.model.Stats;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * @author Ivo Woltring
 */
public class Isbndb {

    private static final Logger LOG = LoggerFactory.getLogger(Isbndb.class);

    private static final String STAT_URL = "http://isbndb.com/api/v2/json/%s/author/DONOTFINDME?opt=keystats";
    private final AccessKeyResource accessKeyResource;
    private String current_key;

    public Isbndb() {
        accessKeyResource = new AccessKeyResource();
        if (accessKeyResource.hasMore()) {
            current_key = accessKeyResource.next();
        }
        if ((current_key == null) || current_key.isEmpty()) {
            throw new IllegalStateException("Have no keys available");
        }
    }

    private String jsonGET(final String url) {
        final HttpClient client = HttpClientBuilder.create()
                                                   .build();
        final HttpGet httpGet = new HttpGet(url);
        httpGet.addHeader("Accept", "application/json");

        final HttpResponse response;
        try {
            response = client.execute(httpGet);
            if (response.getStatusLine()
                        .getStatusCode() == HttpStatus.SC_OK) {
                return EntityUtils.toString(response.getEntity(), "UTF-8");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return "";
    }

    private Keystats stats(final String json) {
        final Gson gson = gson();
        final Stats stats = gson.fromJson(json, Stats.class);
        if (isInvalidApiKey(stats)) {
            throw new InvalidApiKeyException(stats.getError());
        }
        return stats.getKeystats();
    }


    public Keystats stats() {
        final String url = String.format(STAT_URL, current_key);
        final String response = jsonGET(url);
        return stats(response);
    }

    public void searchByAuthor(String name) {

    }

    public void overrideKey(final String key) {
        current_key = key;
    }

    private boolean isInvalidApiKey(final Stats stats) {
        return (stats.getError() != null) && stats.getError()
                                                  .contains("Invalid api key");
    }

    private Gson gson() {
        return new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                                .create();
    }
}
