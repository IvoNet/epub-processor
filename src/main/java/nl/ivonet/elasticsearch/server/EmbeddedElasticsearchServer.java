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
 * @author ivonet
 */
public class EmbeddedElasticsearchServer {

    private static final String HOME_DIRECTORY = "target/elasticsearch/";
    private static final String CLUSTER_NAME = "epubs";

    private final Node node;
    private final String dataDirectory;
    private final String logDirectory;

    public EmbeddedElasticsearchServer() {
        this(HOME_DIRECTORY, CLUSTER_NAME);
    }

    public EmbeddedElasticsearchServer(final String homedir, final String clusterName) {
        this.dataDirectory = homedir + "data";
        this.logDirectory = homedir + "log";
        final Builder elasticsearchSettings = Settings.settingsBuilder()
                                                      .put("http.enabled", "false")
                                                      .put("cluster.name", clusterName)
                                                      .put("path.home", homedir)
                                                      .put("path.data", this.dataDirectory)
                                                      .put("path.log", this.logDirectory);

        this.node = nodeBuilder().local(true)
                                 .settings(elasticsearchSettings.build())
                                 .node();
    }

    public Client getClient() {
        return this.node.client();
    }

    public void shutdown() {
        this.node.close();
//        deleteDataDirectory();
    }

    private void deleteDataDirectory() {
        try {
            FileUtils.deleteDirectory(new File(this.dataDirectory));
            System.out.println("delete");
        } catch (final IOException e) {
            throw new RuntimeException("Could not delete data directory of embedded elasticsearch server", e);
        }
    }
}
