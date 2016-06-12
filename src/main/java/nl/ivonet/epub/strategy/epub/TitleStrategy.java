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
import nl.ivonet.epub.data.AuthorsResource;
import nl.ivonet.epub.data.ListResource;
import nl.ivonet.epub.domain.Dropout;
import nl.ivonet.epub.domain.Epub;
import nl.ivonet.epub.strategy.name.NameFormattingStrategy;
import nl.ivonet.epub.strategy.name.SurnameCommaFirstInitialsStrategy;
import nl.ivonet.epub.strategy.name.SurnameCommaFirstnamesStrategy;
import nl.ivonet.epub.strategy.name.SurnameCommaFullFirstFirstnameThenInitialsStrategy;
import nl.ivonet.epub.strategy.name.SurnameCommaInitialsStrategy;
import nl.ivonet.epub.strategy.name.SwitchFirstnameAndSurnameStrategy;
import nl.ivonet.epub.strategy.text.CapitalizeStrategy;
import nl.ivonet.epub.strategy.text.TextStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

/**
 * This Strategy works on the Title(s) of the {@link nl.ivonet.epub.domain.Epub}. - Cleans up whitespace - Capitalizes
 * words in the title
 *
 * Best to order this strategy after the {@link AuthorStrategy}
 *
 * @author Ivo Woltring
 */
@ConcreteEpubStrategy(order = 10)
public class TitleStrategy implements EpubStrategy {
    private static final Logger LOG = LoggerFactory.getLogger(TitleStrategy.class);

    private final TextStrategy capitalizeStrategy;
    private final ArrayList<NameFormattingStrategy> strategies;
    private final AuthorsResource authorsResource;

    public TitleStrategy() {
        capitalizeStrategy = new CapitalizeStrategy();
        strategies = new ArrayList<>();
        strategies.add(new SurnameCommaFirstnamesStrategy());
        strategies.add(new SurnameCommaInitialsStrategy());
        strategies.add(new SurnameCommaFirstInitialsStrategy());
        strategies.add(new SurnameCommaFullFirstFirstnameThenInitialsStrategy());
        strategies.add(new SwitchFirstnameAndSurnameStrategy());
        authorsResource = new AuthorsResource();
    }

    @Override
    public void execute(final Epub epub) {
        LOG.debug("Applying {} on [{}]", getClass().getSimpleName(), epub.getOrigionalFilename());

        List<String> titles = cleanTitles(epub.getTitles()
                                              .stream());

        if (titles.isEmpty()) {
            titles = cleanTitles(cleanFilename(epub.getOrigionalFilename()).stream());
        } else if (titles.size() == 1) {
            titles = cleanTitles(cleanFilename(titles.get(0)).stream());
        }

        final ArrayList<String> strings = new ArrayList<>();
        for (final String title : titles) {
            strings.addAll(validateTitle(title));
        }

        if (epub.getTitles()
                .isEmpty() || (epub.getTitles()
                                   .size() == 1)) {
            Collections.reverse(strings);
        }

        titles = strings;

        if (titles.isEmpty()) {
            epub.addDropout(Dropout.TITLE);
        }
//        titles.stream()
//              .forEach(System.out::println);

        epub.setTitles(titles);
    }

    private List<String> cleanTitles(final Stream<String> stream) {
        return stream.map(p -> p.replace(" / ", " - ")
                                .replace("/ ", " - ")
                                .replace("/", " - ")
                                .replace(":", "-"))
                     .map(ListResource::removeAccents)
                     .map(capitalizeStrategy::execute)
                     .map(String::trim)
                     .collect(toList());
    }

    private List<String> validateTitle(final String string) {
        final List<String> titles = new ArrayList<>();


        final String ret = string.trim();
        if (stringContainsAuthor(ret)) {
            return Collections.emptyList();
        }

        titles.add(string);

        return titles;
    }

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

    private boolean stringContainsAuthor(final String text) {
        return authorsResource.is(text);
    }


}
