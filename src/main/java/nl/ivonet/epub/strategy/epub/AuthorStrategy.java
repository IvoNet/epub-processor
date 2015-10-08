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

import nl.ivonet.epub.annotation.ConcreteEpubStrategy;
import nl.ivonet.epub.data.AuthorRemoveList;
import nl.ivonet.epub.data.AuthorsResource;
import nl.ivonet.epub.domain.Dropout;
import nl.ivonet.epub.domain.Epub;
import nl.ivonet.epub.domain.Name;
import nl.ivonet.epub.strategy.name.SurnameCommaFirstnamesStrategy;
import nl.ivonet.epub.strategy.name.SwitchFirstnameAndSurnameStrategy;
import nl.siegmann.epublib.domain.Author;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashSet;
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
    private SwitchFirstnameAndSurnameStrategy switchFirstnameAndSurnameStrategy;
    private SurnameCommaFirstnamesStrategy surnameCommaFirstnamesStrategy;

    public AuthorStrategy() {
        removeList = new AuthorRemoveList();
        authorsResource = new AuthorsResource();
        switchFirstnameAndSurnameStrategy = new SwitchFirstnameAndSurnameStrategy();
        surnameCommaFirstnamesStrategy = new SurnameCommaFirstnamesStrategy();


    }

    @Override
    public void execute(final Epub epub) {
        LOG.debug("Applying {} on [{}]", getClass().getSimpleName(), epub.getOrigionalFilename());
        final Set<Author> converted = new HashSet<>();

        converted.addAll(epub.getAuthors()
                             .stream()
                             .map(Name::new)
                             .filter(p -> !removeList.is(p.name()
                                                          .toLowerCase()))
                             .filter(p -> authorsResource.is(p.name()))
                             .map(Name::asAuthor)
                             .collect(Collectors.toList()));


        if (converted.isEmpty()) {
            converted.addAll(retrieveAuthorFromFilename(epub.getOrigionalFilename()));
        }
        if (converted.isEmpty()) {
            epub.addDropout(Dropout.AUTHOR_EMPTY);
        }
        epub.setAuthors(new ArrayList<>(converted));
    }

    private Set<Author> retrieveAuthorFromFilename(final String filename) {

        final String fname = filename.replace(".epub", "");
        String[] names = fname.split(" - ");
        if (names.length == 1) {
            names = fname.split(" ~ ");
        }
        final Set<Author> converted = new HashSet<>();
        for (final String name : names) {
            final Name possibleName = new Name(name);
            if (authorsResource.is(possibleName.name())) {
                converted.add(possibleName.asAuthor());
            } else {
                possibleName.setNameFormatStrategy(switchFirstnameAndSurnameStrategy);
                final Name switchedName = new Name(possibleName.name());
                if (authorsResource.is(switchedName.name())) {
                    LOG.warn("Matched by Switching firstname with surname: {}", switchedName.name());
                    converted.add(switchedName.asAuthor());
                }
            }
        }
        return converted;
    }


}
