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
import nl.ivonet.epub.data.SubjectsRemoveList;
import nl.ivonet.epub.domain.Epub;
import nl.ivonet.epub.strategy.text.CapitalizeStrategy;
import nl.ivonet.epub.strategy.text.TextStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * Cleans up the Subjects list of a {@link nl.ivonet.epub.domain.Epub}.
 *
 * @author Ivo Woltring
 */
@ConcreteEpubStrategy
public class SubjectsStrategy implements EpubStrategy {
    private static final Logger LOG = LoggerFactory.getLogger(SubjectsStrategy.class);
    private final TextStrategy capitalizeStrategy;
    private final SubjectsRemoveList removeList;


    public SubjectsStrategy() {
        capitalizeStrategy = new CapitalizeStrategy();
        removeList = new SubjectsRemoveList();
    }


    //TODO Implement stuff from the urls...
    /*
    http://www.publishingquestions.com/booktext/genres.html
    http://en.wikipedia.org/wiki/Dewey_Decimal_Classification
    http://www.gutenberg.org/files/12513/12513-h/12513-h.htm
     */
    //TODO remove all Language subjects (e.g. Nederlands, Dutch, English, etc)
    //TODO remove title from subjects (e.g. Acorna (fictious character) or Harry Potter)
    //TODO remove if subjects contain words(parts) from the REMVOVE_WORDS list
    //TODO split & subjects (e.g. "Science Fiction & Fantasy" -> 2 subjects)
    //TODO convert stuff to generic notation (e.g. SciFi / SF / Science Fiction -> Science Fiction)
    //TODO when fictatios character add the name to the remove list

    @Override
    public void execute(final Epub epub) {
        LOG.debug("Applying {} on [{}]", getClass().getSimpleName(), epub.getOrigionalFilename());

        final List<String> subjectsIn = epub.getSubjects();
        final List<String> subjectsOut = reduceAndCapitalize(subjectsIn, removeList);

        print(subjectsIn, subjectsOut);

        epub.setSubjects(subjectsOut);
    }

    private void print(final List<String> subjectsIn, final List<String> subjectsOut) {
        subjectsIn.stream()
                  .forEach(LOG::trace);
        subjectsOut.stream()
                   .forEach(LOG::trace);
    }


    /*
    This lambda expression filters on all the subjectsIn where the items in the list in lowercase do not match
    any word in the removeList where the resulting list is mapped in the capitalizeStrategy and collected into
    another List.
     */
    private List<String> reduceAndCapitalize(final List<String> subjectsIn, final SubjectsRemoveList removeList) {
        return subjectsIn.stream()
                         .filter(p -> !removeList.is(p))
                         .map(capitalizeStrategy::execute)
                         .collect(toList());
    }


}
