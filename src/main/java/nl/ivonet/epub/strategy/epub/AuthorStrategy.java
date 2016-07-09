/*
 * Copyright (c) 2013 Ivo Woltring
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

import nl.ivonet.boundary.Book;
import nl.ivonet.boundary.BookResponse;
import nl.ivonet.elasticsearch.server.ElasticsearchFactory;
import nl.ivonet.elasticsearch.server.EmbeddedElasticsearchServer;
import nl.ivonet.epub.annotation.ConcreteEpubStrategy;
import nl.ivonet.epub.data.AuthorRemoveList;
import nl.ivonet.epub.data.AuthorsResource;
import nl.ivonet.epub.data.ListResource;
import nl.ivonet.epub.domain.Dropout;
import nl.ivonet.epub.domain.Epub;
import nl.ivonet.epub.domain.Name;
import nl.ivonet.epub.strategy.name.SwitchFirstnameAndSurnameStrategy;
import nl.ivonet.service.Isbndb;
import nl.siegmann.epublib.domain.Author;
import nl.siegmann.epublib.domain.Identifier;
import org.elasticsearch.action.get.GetResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


/**
 * Strategy for cleaning up and converting authors in an {@link nl.ivonet.epub.domain.Epub}.
 *
 * @author Ivo Woltring
 */
@ConcreteEpubStrategy
public class AuthorStrategy implements EpubStrategy {
    private static final Logger LOG = LoggerFactory.getLogger(AuthorStrategy.class);
    private final AuthorRemoveList removeList;
    private final AuthorsResource authorsResource;
    private final SwitchFirstnameAndSurnameStrategy switchFirstnameAndSurnameStrategy;
    private final EmbeddedElasticsearchServer elasticsearchServer;
    private final Isbndb isbndb;

    public AuthorStrategy() {
        isbndb = new Isbndb();
        removeList = new AuthorRemoveList();
        authorsResource = new AuthorsResource();
        switchFirstnameAndSurnameStrategy = new SwitchFirstnameAndSurnameStrategy();
        elasticsearchServer = ElasticsearchFactory.getInstance()
                                                  .elasticsearchServer();

    }

    @Override
    public void execute(final Epub epub) {
        LOG.debug("Applying {} on [{}]", getClass().getSimpleName(), epub.getOrigionalFilename());
        final Set<Author> converted = new HashSet<>();

        // TODO: 09-07-2016 First try the ISBN number
//        final String isbn = isbn(epub.getIdentifiers());
//        if (!isbn.isEmpty()) {
//            boolean authorFromIsbnFound = authorFromISBN(isbn);
//        }

        //Improve original author list from epub
        converted.addAll(epub.getAuthors()
                             .stream()
                             .map(Name::new)
                             .filter(p -> !removeList.is(p.name()
                                                          .toLowerCase()))
                             .filter(p -> authorsResource.is(p.name()))
                             .map(Name::asAuthor)
                             .collect(Collectors.toList()));

        //add names from the file name
        converted.addAll(retrieveAuthorFromFilename(epub.getOrigionalFilename()));

        if (converted.isEmpty()) {
            epub.addDropout(Dropout.AUTHOR_EMPTY);
        }
        epub.setAuthors(new ArrayList<>(converted));
    }

    private boolean authorFromISBN(final String input) {
        final String isbn = input.replace("-", "")
                                 .replace(" ", "")
                                 .replace(".", "")
                                 .replace("[", "")
                                 .replace("]", "")
                                 .replace("ISBN", "")
                                 .replace("urn:isbn:", "");
        // TODO: 09-07-2016 Write the author from isbn here
        // TODO: 09-07-2016 here am I

        //1 see if isbn exists in elastic search db
        final GetResponse response = elasticsearchServer.getClient()
                                                        .prepareGet("books", "isbn", isbn)
                                                        .get();
        if (response.isExists() && !response.isSourceEmpty()) {
            final BookResponse bookResponse = isbndb.getBookResponse(response.getSourceAsString());
            final Book book = bookResponse.firstBook();
        }
        //2 if exists see of author ids exist in edb
        //3 if not then fetch them from isbndb


        return false;
    }

    private String isbn(final List<Identifier> identifiers) {
        if (identifiers.isEmpty()) {
            return "";
        }
        for (final Identifier identifier : identifiers) {
            if ("isbn".equalsIgnoreCase(identifier.getScheme())) {
                return identifier.getValue();
            }
            // TODO: 09-07-2016 add other possitives like 'urn:isbn:'
        }
        return "";
    }

    // TODO: 12-06-2016 duplicate code with TitleStrategy!
    private List<String> cleanFilename(final String name) {
        final String filename = ListResource.removeAccents(name.replace(".kepub", "")
                                                               .replace(".epub", "")
                                                               .replace("_", "."));
        List<String> strings = new LinkedList<>(Arrays.asList(filename.split(" - ")));
        if (strings.size() == 1) {
            strings = Arrays.asList(name.split(" ~ "));
        }
        return strings;
    }

    private Set<Author> retrieveAuthorFromFilename(final String filename) {
        LOG.info("Retrieve Author from filename [{}]", filename);
        final List<String> strings = cleanFilename(filename);

        final Set<Author> converted = new HashSet<>();
        for (final String name : strings) {

            final Name possibleName = new Name(name);
            if (authorsResource.is(possibleName.name())) {
                converted.add(possibleName.asAuthor());
            } else {
                if (!possibleName.hasFirstname()) {
                    continue;
                }
                possibleName.setNameFormatStrategy(switchFirstnameAndSurnameStrategy);
                final Name switchedName = new Name(possibleName.name());
                if (authorsResource.is(switchedName.name())) {
                    LOG.info("Matched by Switching firstname with surname: {}", switchedName.name());
                    converted.add(switchedName.asAuthor());
                } else {
                    switchedName.setNameFormatStrategy(switchFirstnameAndSurnameStrategy);
                }
            }
        }
        return converted;
    }
}
