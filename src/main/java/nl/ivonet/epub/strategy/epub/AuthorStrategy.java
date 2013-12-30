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
import nl.ivonet.epub.domain.Dropout;
import nl.ivonet.epub.domain.Epub;
import nl.ivonet.name_entity.model.Names;
import nl.ivonet.name_entity.parser.NamedEntityParser;
import nl.siegmann.epublib.domain.Author;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/*
TODO Too many dropout resasons for not having an author. Expand strategy to get name from filepath/name
TODO If an author is names like editor, publisher, various, etc it should be stripped from the author list
TODO if the end list of authors is empty I should try to get it from the path or filename
TODO what about names that are sometimes with initials and sometimes completely written?

TODO remove the whole clean() part when the NameProcessor is completely finished!!
*/


/**
 * Strategy for cleaning up and converting authors in an {@link nl.ivonet.epub.domain.Epub}.
 *
 * @author Ivo Woltring
 */
@ConcreteEpubStrategy
public class AuthorStrategy implements EpubStrategy {
    private static final Logger LOG = LoggerFactory.getLogger(AuthorStrategy.class);
    private final NamedEntityParser namedEntityParser;

    public AuthorStrategy() {
        namedEntityParser = new NamedEntityParser();
    }

    @Override
    public void execute(final Epub epub) {
        LOG.debug("Applying {} on [{}]", getClass().getSimpleName(), epub.getOrigionalFilename());
        final Set<Author> converted = new HashSet<>();

        final String authors = authorsToString(epub) + "/" + epub.getOrigionalPath();
        final Names names = namedEntityParser.parse(authors);
        names.stream()
             .forEach(p -> converted.add(new Author((p.getFirstname() + " " + p.getInsertion()).trim(),
                                                    p.getSurname())));

        if (converted.isEmpty()) {
            epub.addDropout(Dropout.AUTHOR_EMPTY);
        }
        epub.setAuthors(new ArrayList<>(converted));
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
