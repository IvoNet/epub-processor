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

package nl.ivonet.elasticsearch.server;

import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.client.Client;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

/**
 * @author Ivo Woltring
 */
public class EmbeddedElasticsearchServerTest {
    private EmbeddedElasticsearchServer embeddedElasticsearchServer;

    @Before
    public void startEmbeddedElasticsearchServer() {
        embeddedElasticsearchServer = new EmbeddedElasticsearchServer("target/test-elasticsearch", "test-cluster");
    }

    @After
    public void shutdownEmbeddedElasticsearchServer() {
        embeddedElasticsearchServer.shutdown();
    }

    /**
     * By using this method you can access the embedded server.
     */
    protected Client getClient() {
        return embeddedElasticsearchServer.getClient();
    }

    @Test
    public void indexSimpleDocument() throws IOException {
        getClient().prepareIndex("myindex", "document", "1")
                   .setSource(jsonBuilder().startObject()
                                           .field("test", "123")
                                           .endObject())
                   .execute()
                   .actionGet();

        final GetResponse fields = getClient().prepareGet("myindex", "document", "1")
                                              .execute()
                                              .actionGet();
        assertThat(fields.getSource()
                         .get("test")).isEqualTo("123");
    }


    @Ignore
    @Test
    public void existingData() throws Exception {
        final EmbeddedElasticsearchServer epubs = new EmbeddedElasticsearchServer(
                "/Users/ivonet/Books/epub-processed/elasticsearch", "epubs");
        Thread.sleep(1000);
//        final SearchResponse searchResponse = epubs.getClient()
//                                                   .prepareSearch("isbn").addField("9780142419403")
//                                                   .execute()
//                                                   .get();
        final GetResponse getFields = epubs.getClient()
                                           .prepareGet("isbn", "book", "9780142419403")
                                           .get();
        System.out.println("getFields.isExists() = " + getFields.isExists());
        System.out.println("getFields.getSourceAsString() = " + getFields.getSourceAsString());
//        System.out.println("searchResponse = " + searchResponse);
    }
}