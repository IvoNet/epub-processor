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

import org.apache.commons.io.FileUtils;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.settings.Settings.Builder;
import org.elasticsearch.node.Node;

import java.io.File;
import java.io.IOException;

import static org.elasticsearch.node.NodeBuilder.nodeBuilder;

/**
 * Elasticsearch singleton.
 *
 * Not that it can only be instantiated, so choose your getEmbeddedElasticsearchServer well.
 * After that you will get the already created instance even if you choose to provide
 * other parameters as input.
 *
 * @author ivonet
 */
public class EmbeddedElasticsearchServer {

    private static final String HOME_DIRECTORY = "target/elasticsearch/";
    private static final String CLUSTER_NAME = "epubs";
    private static EmbeddedElasticsearchServer instance;
    private final Node node;
    private final String dataDirectory;


    public EmbeddedElasticsearchServer() {
        this(HOME_DIRECTORY, CLUSTER_NAME);
    }

    public EmbeddedElasticsearchServer(final String homedir) {
        this(homedir, CLUSTER_NAME);
    }
    public EmbeddedElasticsearchServer(final String homedir, final String clusterName) {
        this.dataDirectory = endslash(homedir) + "data";
        final String logDirectory = endslash(homedir) + "log";
        final Builder elasticsearchSettings = Settings.settingsBuilder()
                                                      .put("http.enabled", "false")
                                                      .put("cluster.name", clusterName)
                                                      .put("path.home", homedir)
                                                      .put("path.data", this.dataDirectory)
                                                      .put("path.log", logDirectory);

        this.node = nodeBuilder().local(true)
                                 .settings(elasticsearchSettings.build())
                                 .node();
    }

    private static void initialize() {
        instance = null;
    }

    public Client getClient() {
        return this.node.client();
    }

    /**
     * Shutdown the embedded server and reset the instance.
     */
    public void shutdown() {
        this.node.close();
        initialize();
//        deleteDataDirectory();
    }


    private String endslash(final String output) {
        return output.endsWith("/") ? output : String.format("%s/", output);
    }

    private void deleteDataDirectory() {
        try {
            FileUtils.deleteDirectory(new File(this.dataDirectory));
            System.out.println("delete");
        } catch (final IOException e) {
            throw new RuntimeException("Could not delete data directory of embedded elasticsearch instance", e);
        }
    }
}
