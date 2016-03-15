/*
 * Copyright (c) 2015 Ivo Woltring
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

import nl.ivonet.epub.domain.Epub;
import nl.ivonet.epub.domain.Name;
import nl.siegmann.epublib.domain.Identifier;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.codecraft.xsoup.Xsoup;

import java.io.IOException;
import java.util.List;

//TODO some other search engines for books
//TODO http://www.worldcat.org/search?q=179807355&qt=owc_search

/**
 * Tries to get more metadata from Stores based on ISBN.
 *
 * @author Ivo Woltring
 */
//@ConcreteEpubStrategy
public class IdentifierStrategy implements EpubStrategy {
    private static final Logger LOG = LoggerFactory.getLogger(IdentifierStrategy.class);

    private static final String KOBOBOOKS = "http://www.kobobooks.com/search/search.html?q=";
    private static final String NO_RESULTS = "//p[contains(@class, 'search-zero-results')]/text()";
    private static final String AUTHOR = "//h2[contains(@class, 'author')]//a[contains(@class, 'contributor')]/text()";
    private static final String DESCRIPTION = "//p[contains(@class, 'synopsis-description')]/tidyText()";


    @Override
    public void execute(final Epub epub) {
        LOG.debug("Applying {} on [{}]", getClass().getSimpleName(), epub.getOrigionalFilename());
        final List<Identifier> identifiers = epub.getIdentifiers();
        for (final Identifier identifier : identifiers) {
            System.out.println("Trying identifier:" + identifier.getValue());
            System.out.println("identifier.isBookId() = " + identifier.isBookId());
            final Document document;
            try {

                document = Jsoup.connect(KOBOBOOKS + identifier.getValue())
                                .timeout(15000)
                                .get();

//                System.out.println("document.body() = " + document.body());
                final String s = retrieveXpath(document, NO_RESULTS);
                if ((s != null) && s.contains("Sorrie!")) {
                    System.out.println("No results found for:" + identifier.getValue());
                } else {
                    final String author = retrieveXpath(document, AUTHOR);
                    System.out.println("author = " + (author != null ? new Name(author).name() : ""));
                    final String description = retrieveXpath(document, DESCRIPTION);
                    System.out.println("description = " + description);
                }

            } catch (final IOException e) {
                return;
            }


        }
    }

    private String retrieveXpath(final Document document, final String xpath) {
        return Xsoup.compile(xpath)
                    .evaluate(document)
                    .get();


    }


}
