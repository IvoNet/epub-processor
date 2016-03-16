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
import nl.ivonet.epub.data.ListResource;
import nl.ivonet.epub.domain.Dropout;
import nl.ivonet.epub.domain.Epub;
import nl.ivonet.epub.domain.Name;
import nl.ivonet.epub.strategy.name.NameFormattingStrategy;
import nl.ivonet.epub.strategy.name.SurnameCommaFirstInitialsStrategy;
import nl.ivonet.epub.strategy.name.SurnameCommaFirstnamesStrategy;
import nl.ivonet.epub.strategy.name.SurnameCommaFullFirstFirstnameThenInitialsStrategy;
import nl.ivonet.epub.strategy.name.SurnameCommaInitialsStrategy;
import nl.ivonet.epub.strategy.name.SwitchFirstnameAndSurnameStrategy;
import nl.ivonet.epub.strategy.text.CapitalizeStrategy;
import nl.ivonet.epub.strategy.text.TextStrategy;
import nl.siegmann.epublib.domain.Author;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * This Strategy works on the Title(s) of the {@link nl.ivonet.epub.domain.Epub}. - Cleans up whitespace - Capitalizes
 * words in the title
 *
 * @author Ivo Woltring
 */
@ConcreteEpubStrategy
public class TitleStrategy implements EpubStrategy {
    private static final Logger LOG = LoggerFactory.getLogger(TitleStrategy.class);

    private final TextStrategy capitalizeStrategy;
    private final ArrayList<NameFormattingStrategy> strategies;

    public TitleStrategy() {
        capitalizeStrategy = new CapitalizeStrategy();
        strategies = new ArrayList<>();
        strategies.add(new SurnameCommaFirstnamesStrategy());
        strategies.add(new SurnameCommaInitialsStrategy());
        strategies.add(new SurnameCommaFirstInitialsStrategy());
        strategies.add(new SurnameCommaFullFirstFirstnameThenInitialsStrategy());
        strategies.add(new SwitchFirstnameAndSurnameStrategy());

    }

    @Override
    public void execute(final Epub epub) {
        LOG.debug("Applying {} on [{}]", getClass().getSimpleName(), epub.getOrigionalFilename());

        List<String> titles = epub.getTitles()
                                  .stream()
                                  .map(p -> p.replace(" / ", " - ")
                                             .replace("/ ", " - ")
                                             .replace("/", " - ")
                                             .replace(":", "-"))
                                  .map(ListResource::removeAccents)
                                  .map(capitalizeStrategy::execute)
                                  .map(String::trim)
                                  .collect(toList());

        if (titles.isEmpty()) {
            titles = tryFilename(epub);
        }

        if (titles.isEmpty()) {
            epub.addDropout(Dropout.TITLE);
        }
        epub.setTitles(titles);
    }

    private List<String> tryFilename(final Epub epub) {
        final List<String> titles = new ArrayList<>();

        final String filename = epub.getOrigionalFilename()
                                    .replace(".epub", "")
                                    .replace("_", ".");

        List<String> strings = new LinkedList<>(Arrays.asList(filename.split(" - ")));
        if (strings.size() == 1) {
            strings = Arrays.asList(filename.split(" ~ "));
        }

        boolean found = false;
        final List<String> authors = new ArrayList<>(strings);
        for (final String string : authors) {
            final String ret = string.trim();
            LOG.trace("Trying title : {}", string);
            if (stringContainsAuthor(ret, epub.getAuthors())) {
                strings.remove(string);
                found = true;
            }
        }
        if (!strings.isEmpty() && found) {
            titles.addAll(strings);
            strings.stream()
                   .forEach(p -> LOG.warn("Title found in filename: {}", p));
        }

        return titles;
    }

    private boolean stringContainsAuthor(final String text, final List<Author> authors) {
        for (final Author author : authors) {
            final Name name = new Name(author);
            LOG.trace("Trying Author: {}", name.name());
            for (final NameFormattingStrategy strategy : strategies) {
                name.setNameFormatStrategy(strategy);
                if (text.contains(name.name())) {
                    LOG.trace("Name match   : {}", name.name());
                    return true;
                }
            }
        }
        return false;
    }


}
