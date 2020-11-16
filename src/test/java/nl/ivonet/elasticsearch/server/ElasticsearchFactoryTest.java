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
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

/**
 * @author Ivo Woltring
 */
@Ignore
public class ElasticsearchFactoryTest {

    private ElasticsearchFactory factory;

    @Before
    public void setUp() throws Exception {
        factory = ElasticsearchFactory.getInstance();

    }

    @Test
    public void getInstance() throws Exception {
        final Client client = getClient();

        client.prepareIndex("myindex", "document", "1")
              .setSource(jsonBuilder().startObject()
                                      .field("test", "123")
                                      .endObject())
              .execute()
              .actionGet();

        final GetResponse fields = client.prepareGet("myindex", "document", "1")
                                         .execute()
                                         .actionGet();
        assertThat(fields.getSource()
                         .get("test")).isEqualTo("123");
        factory.shutdown();
    }

    private Client getClient() {
        final EmbeddedElasticsearchServer embeddedElasticsearchServer = factory.elasticsearchServer();
        return embeddedElasticsearchServer.getClient();
    }

    @Ignore
    @Test
    public void shutdown() throws Exception {
        Client client = getClient();

        client.prepareIndex("myindex", "document", "1")
              .setSource(jsonBuilder().startObject()
                                      .field("test", "123")
                                      .endObject())
              .execute()
              .actionGet();

        final GetResponse fields = client.prepareGet("myindex", "document", "1")
                                         .execute()
                                         .actionGet();
        assertThat(fields.getSource()
                         .get("test")).isEqualTo("123");
        factory.shutdown();
        final EmbeddedElasticsearchServer embeddedElasticsearchServer = factory.elasticsearchServer(
                "target/test-elasticsearch", "test-cluster-shutdown");
        client = embeddedElasticsearchServer.getClient();

        client.prepareIndex("myindex2", "document", "1")
              .setSource(jsonBuilder().startObject()
                                      .field("test", "123")
                                      .endObject())
              .execute()
              .actionGet();

        final GetResponse fields2 = client.prepareGet("myindex2", "document", "1")
                                          .execute()
                                          .actionGet();
        assertThat(fields2.getSource()
                          .get("test")).isEqualTo("123");
        factory.shutdown();
        client = getClient();
        final SearchResponse searchResponse = client.prepareSearch("myindex")
                                                    .execute()
                                                    .get();
        assertThat(searchResponse.getHits()
                                 .totalHits()).isEqualTo(1);
        factory.shutdown();


    }

}
