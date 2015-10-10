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

package nl.ivonet.epub.data;

import nl.ivonet.epub.domain.Name;
import nl.ivonet.epub.strategy.name.NameFormattingStrategy;
import nl.ivonet.epub.strategy.name.SurnameCommaFirstInitialsStrategy;
import nl.ivonet.epub.strategy.name.SurnameCommaFirstnamesStrategy;
import nl.ivonet.epub.strategy.name.SurnameCommaFullFirstFirstnameThenInitialsStrategy;
import nl.ivonet.epub.strategy.name.SurnameCommaInitialsStrategy;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

/**
 * @author Ivo Woltring
 */
public class AuthorsResource extends ListResource {

    private final List<String> names;
    private final List<NameFormattingStrategy> strategies;
    private final Set<String> aliasses;
    Consumer<Name> nameConsumer = new Consumer<Name>() {
        @Override
        public void accept(final Name name) {
            for (final NameFormattingStrategy strategy : strategies) {
                name.setNameFormatStrategy(strategy);
                aliasses.add(name.name());
            }

        }
    };


    public AuthorsResource() {
        names = listFromFilename("authors.txt");
        aliasses = new HashSet<>();
        strategies = new ArrayList<>();
        strategies.add(new SurnameCommaFirstnamesStrategy());
        strategies.add(new SurnameCommaInitialsStrategy());
        strategies.add(new SurnameCommaFirstInitialsStrategy());
        strategies.add(new SurnameCommaFullFirstFirstnameThenInitialsStrategy());

        aliasses.addAll(names);
        names.stream()
             .map(Name::new)
             .forEach(nameConsumer::accept);
    }

    /**
     * First check if the author exists as is. Then check against the default formatting strategy Then check against all
     * allowed strategies in order of importants an accuracy Then as final attempt switch the firstname and surname and
     * try it all again
     */
    @Override
    public boolean is(final String input) {
        return applyMatchingStrategies(input.trim());
    }

    private boolean applyMatchingStrategies(final String author) {
        if (aliasses.contains(author)) {
            return true;
        }
        final Name name = new Name(author);
        return applyStrategies(name);
    }

    private boolean applyStrategies(final Name name) {
        for (final NameFormattingStrategy strategy : strategies) {
            name.setNameFormatStrategy(strategy);
            if (aliasses.contains(name.name())) {
                return true;
            }
        }
        return false;
    }
}
