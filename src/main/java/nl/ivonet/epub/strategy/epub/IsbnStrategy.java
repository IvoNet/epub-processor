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

package nl.ivonet.epub.strategy.epub;

import nl.ivonet.boundary.BookResponse;
import nl.ivonet.elasticsearch.server.ElasticsearchFactory;
import nl.ivonet.elasticsearch.server.EmbeddedElasticsearchServer;
import nl.ivonet.epub.annotation.ConcreteEpubStrategy;
import nl.ivonet.epub.domain.Dropout;
import nl.ivonet.epub.domain.Epub;
import nl.ivonet.service.Isbndb;
import nl.siegmann.epublib.domain.Identifier;
import org.elasticsearch.action.get.GetResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.ExecutionException;

// TODO: 25-06-2016 if through daily quota get new key as long as there are keus to get
// TODO: 25-06-2016 safe found isbns to elastic search
// TODO: 25-06-2016 search elastic search first for results before asking isbndb

/**
 * @author Ivo Woltring
 */
@ConcreteEpubStrategy
public class IsbnStrategy implements EpubStrategy {
    private static final Logger LOG = LoggerFactory.getLogger(IsbnStrategy.class);
    private final Isbndb isbndb;
    private final EmbeddedElasticsearchServer esearch;
    private boolean dailyLimitReached;

    public IsbnStrategy() {
        isbndb = new Isbndb();
        isbndb.disableErrorhandling();
        dailyLimitReached = false;
        esearch = ElasticsearchFactory.getInstance()
                                      .elasticsearchServer();
    }

    @Override
    public void execute(final Epub epub) {
        LOG.debug("Applying {} on [{}]", getClass().getSimpleName(), epub.getOrigionalFilename());
        if (dailyLimitReached) {
            LOG.warn("Daily isbndb api call limit reached. This strategy is disabled for the rest of this run.");
            return;
        }
        final List<Identifier> identifiers = epub.getIdentifiers();
        if (not(identifiers)) {
            return;
        }

        identifiers.forEach(identifier -> {
            final String isbn = identifier.getValue()
                                          .replace("-", "");
            LOG.debug("Identifier found of type [{}] and value [{}]. bookid [{}]", identifier.getScheme(), isbn,
                      identifier.isBookId());

            if (notIsbn(identifier.getScheme())) {
                return;
            }

            final GetResponse book = doWeHaveTheIsbnAlready(isbn);

            final BookResponse bookResponse;
            if (book.isExists()) {
                bookResponse = isbndb.getBookResponse(book.getSourceAsString());
                LOG.info("!!!!ISBN FOUND in elastic search!!");
            } else {
                bookResponse = isbndb.bookById(isbn);
                dailyLimitReached = bookResponse.getKeystats()
                                                .memberLimitReached();
            }

            if (dailyLimitReached) {
                // TODO: 26-06-2016 get next api key when through limit and reset the boolean.
                // TODO: 26-06-2016 check if we have reached our daily limit and all the keys
            }

            //------------------------------------------------------------------------------
            // TODO: 27-06-2016 Temporary, until clear how stuff works with elastic search
            if (bookResponse == null) {
                writeISBN(isbn + ".null.json", "{\"error\":\"bookResponse was null\"}");
                return;
            }
            if (bookResponse.hasError()) {
                writeISBN(isbn + ".has_error.json", bookResponse.getJson());
                return;
            }
            //------------------------------------------------------------------------------
            // TODO: 26-06-2016 if found put the metadata into the epub
            /*
            Note that here we have the possibility of feature envy.
            What If I find an ISBN number and start overriding data from other strategies? Isn't that
            counter productive.
            It might be better make sure that this strategy runs first and let other strategies query the elastic
            search db
            and enrich their data based on those findings.
            I have to think on this.
            This might mean that the base ordering of strategies should not be 0 but e.g. an arbitrary 1000
            so that we can also prioritize higher than normal.

            I have to think on it.
            First I'll do a run without enriching based on findings. If all goes well I will start creating a database
             */


            if (!saveIsbn(identifier, bookResponse.getJson())) {
                epub.addDropout(Dropout.ISBN);
            }

            //------------------------------------------------------------------------------
            // TODO: 27-06-2016 Temporary
            writeISBN(isbn, bookResponse.getJson());
            //------------------------------------------------------------------------------
        });
    }

    private boolean saveIsbn(final Identifier identifier, final String json) {
        try {
            esearch.getClient()
                   .prepareIndex("books", "isbn", identifier.getValue())
                   .setSource(json)
                   .execute()
                   .get();
        } catch (InterruptedException | ExecutionException e) {
            LOG.error(e.getMessage(), e);
            return false;
        }
        return true;
    }

    private GetResponse doWeHaveTheIsbnAlready(final String isbn) {
        return esearch.getClient()
                      .prepareGet("books", "isbn", isbn)
                      .get();
    }

    private boolean notIsbn(final String scheme) {
        return !"isbn".equalsIgnoreCase(scheme);
    }

    private boolean not(final List<Identifier> identifiers) {
        return identifiers.isEmpty();
    }

    // TODO: 26-06-2016 Temp code for analysis purposes
    private void writeISBN(final String name, final String content) {
        try {
            final String folder = "/Users/ivonet/dev/ebook/output/isbn/";
            final File file = new File(folder);
            if (!file.exists()) {
                //noinspection ResultOfMethodCallIgnored
                file.mkdirs();
            }
            Files.write(Paths.get(folder, name /*+ ".json"*/), content.getBytes());
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

}
