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

/**
 * @author Ivo Woltring
 */
public class ElasticsearchFactory {


    private EmbeddedElasticsearchServer embeddedElasticsearchServer;

    private ElasticsearchFactory() {
    }

    public EmbeddedElasticsearchServer elasticsearchServer() {

        if (embeddedElasticsearchServer == null) {
            embeddedElasticsearchServer = new EmbeddedElasticsearchServer();
        }
        return embeddedElasticsearchServer;
    }

    public EmbeddedElasticsearchServer elasticsearchServer(final String homedir) {
        if (embeddedElasticsearchServer == null) {
            embeddedElasticsearchServer = new EmbeddedElasticsearchServer(homedir);
        }
        return embeddedElasticsearchServer;
    }

    public EmbeddedElasticsearchServer elasticsearchServer(final String homedir, final String clusterName) {
        if (embeddedElasticsearchServer == null) {
            embeddedElasticsearchServer = new EmbeddedElasticsearchServer(homedir, clusterName);
        }
        return embeddedElasticsearchServer;
    }

    public void shutdown() {
        if (embeddedElasticsearchServer != null) {
            giveOtherStuffSecondsToShutdown(3);
            embeddedElasticsearchServer.shutdown();
            embeddedElasticsearchServer = null;
        }
    }

    private void giveOtherStuffSecondsToShutdown(final int i) {
        try {
            Thread.sleep(i * 1000);
        } catch (InterruptedException ignored) {
        }
    }

    private static final class Instance {
        static final ElasticsearchFactory SINGLETON = new ElasticsearchFactory();
    }

    public static ElasticsearchFactory getInstance() {
        return Instance.SINGLETON;
    }
}
