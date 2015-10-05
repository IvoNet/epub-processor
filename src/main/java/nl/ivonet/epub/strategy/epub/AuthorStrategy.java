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
import nl.ivonet.epub.domain.Dropout;
import nl.ivonet.epub.domain.Epub;
import nl.ivonet.epub.domain.Name;
import nl.siegmann.epublib.domain.Author;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
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

    public AuthorStrategy() {
        removeList = new AuthorRemoveList();
    }

    @Override
    public void execute(final Epub epub) {
        LOG.debug("Applying {} on [{}]", getClass().getSimpleName(), epub.getOrigionalFilename());
        final Set<Author> converted = new HashSet<>();

        //FIXME This next line is just wrong! The processing is not yet done!
        //TODO All the authors need to be processed and cleaned up

        converted.addAll(epub.getAuthors()
                             .stream()
                             .map(Name::new)
                             .filter(p -> !removeList.stream()
                                                     .filter(r -> p.name()
                                                                   .toLowerCase()
                                                                   .contains(r))
                                                     .findAny()
                                                     .isPresent())
                             .map(Name::asAuthor)
                             .collect(Collectors.toList()));


        converted.stream()
                .map(Name::new)
                .map(Name::name)
                .forEach(this::writeAuthor);

//        final String[] split = epub.getOrigionalFilename()
//                                   .split("-");


//        final String authors = authorsToString(epub) + "/" + epub.getOrigionalPath();
//        System.out.println("authors = " + authors);
//        final Names names = namedEntityParser.parse(authors);
//        names.stream()
//             .forEach(p -> converted.add(new Author((p.getFirstname() + " " + p.getInsertion()).trim(),
//                                                    p.getSurname())));


        if (converted.isEmpty()) {
            //TODO here try to find the author from filename...
            epub.addDropout(Dropout.AUTHOR_EMPTY);
        }
        epub.setAuthors(new ArrayList<>(converted));
    }

    private void writeAuthor(final String name) {
        try {
            Files.write(Paths.get("/Users/ivonet/dev/ebook/epub-processor/artifact/authors/", name), name.getBytes());
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String authorsToString(final Epub epub) {
        final StringBuilder sb = new StringBuilder();
        epub.getAuthors()
            .stream()
            .forEach(p -> sb.append(p.toString())
                            .append(" / "));
        return sb.toString();
    }
}
