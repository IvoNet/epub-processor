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

package nl.ivonet.elasticsearch.service;

import nl.ivonet.boundary.BookResponse;
import nl.ivonet.elasticsearch.server.ElasticsearchFactory;
import nl.ivonet.elasticsearch.server.EmbeddedElasticsearchServer;
import nl.ivonet.service.Isbndb;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.get.GetResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutionException;

/**
 * @author Ivo Woltring
 */
public class ElasticService {
    private static final Logger LOG = LoggerFactory.getLogger(ElasticService.class);

    private final EmbeddedElasticsearchServer server;
    private final Isbndb isbndb;

    private ElasticService() {
        isbndb = new Isbndb();
        isbndb.disableErrorhandling();
        server = ElasticsearchFactory.getInstance()
                                     .elasticsearchServer();
    }

    public synchronized boolean saveIsbn(final String isbn, final String jsonData) {
        try {
            server.getClient()
                  .prepareIndex("books", "isbn", cleanIsbn(isbn))
                  .setSource(jsonData)
                  .execute()
                  .get();
            LOG.debug("ISBN [{}] saved to database", cleanIsbn(isbn));
        } catch (InterruptedException | ExecutionException e) {
            LOG.error(e.getMessage(), e);
            return false;
        }
        return true;
    }

    public BookResponse retrieveBookResponse(final String isbn) {
        final GetResponse[] response = new GetResponse[1];
        server.getClient()
              .prepareGet("books", "isbn", cleanIsbn(isbn))
              .execute(new ActionListener<GetResponse>() {
                  @Override
                  public void onResponse(final GetResponse getFields) {
                      System.out.println("getFields = " + getFields);
                      response[0] = getFields;
                  }

                  @Override
                  public void onFailure(final Throwable e) {
                      LOG.error("ISBN Error:", e.getMessage());
//                      throw new RuntimeException(e);
                  }
              });
        final GetResponse ret = response[0];
        if (ret == null) {
            System.out.println("ret = " + ret);
            return null;
        }
        return response[0].isExists() ? isbndb.getBookResponse(response[0].getSourceAsString()) : null;
    }

    private String cleanIsbn(final String isbn) {
        return isbn.replace("-", "")
                   .replace(" ", "")
                   .replace(".", "")
                   .replace("[", "")
                   .replace("]", "")
                   .replace("ISBN", "");

    }

    private static final class Instance {
        static final ElasticService SINGLETON = new ElasticService();
    }

    public static ElasticService getInstance() {
        return Instance.SINGLETON;
    }


}
