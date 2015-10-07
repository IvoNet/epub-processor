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

package nl.ivonet.epub.domain;

import nl.ivonet.epub.strategy.name.NameFormattingStrategy;
import nl.ivonet.epub.strategy.name.SurnameCommaFirstnamesStrategy;
import nl.siegmann.epublib.domain.Author;

import java.util.Arrays;

/**
 * @author Ivo Woltring
 */
public class Name {
    private static final String INITIALS = "([a-zA-Z]\\.[ ]*)+|(([a-zA-Z][ ]+)+[a-zA-Z]?)";
    private static final String JUNIOR = " Jr.";
    private static final int FIRSTNAME_IDX = 1;
    private static final int SURNAME_IDX = 0;
    private final boolean junior;
    private NameFormattingStrategy nameFormatStrategy;
    private String firstname;
    private String surname;

    public Name(final String author) {
        String name = author.replace("_", ".")
                            .trim();
        junior = name.contains(" Jr.");
        if (junior) {
            name = name.replace(JUNIOR, "");
        }

        if (name.contains(", ")) {
            final String first = split(name, FIRSTNAME_IDX);
            final String last = split(name, SURNAME_IDX);
            process(first, last);
        } else {
            final String[] strings = name.split(" ");
            final String surname = strings[strings.length - 1];
            if (strings.length == 1) {
                process("", surname);
            } else {
                process(String.join(", ", Arrays.copyOfRange(strings, 0, strings.length - 1)), surname);
            }
        }


        this.nameFormatStrategy = new SurnameCommaFirstnamesStrategy();
    }

    public Name(final Author author) {
        this(author.getFirstname(), author.getLastname());
    }

    public Name(final String firstname, final String surname) {
        this(firstname, surname, new SurnameCommaFirstnamesStrategy());
    }

    public Name(final String firstname, final String surname, final NameFormattingStrategy nameFormatStrategy) {
        junior = firstname.contains(JUNIOR) || surname.contains(JUNIOR);
        this.firstname = firstname;
        this.surname = surname;

        if (junior) {
            this.firstname = this.firstname.replace(JUNIOR, "");
            this.surname = this.surname.replace(JUNIOR, "");
        }

        process(firstname, surname);

        this.nameFormatStrategy = nameFormatStrategy;
    }

    private static String split(final String author, final int index) {
        final String[] strings = author.split(", ");
        return (index < strings.length) ? strings[index] : "";
    }

    private static String toCamelCase(final String init) {
        if (init == null) {
            return null;
        }

        final StringBuilder ret = new StringBuilder(init.length());

        for (final String word : init.split(" ")) {
            if (!word.isEmpty()) {
                ret.append(word.substring(0, 1)
                               .toUpperCase());
                ret.append(word.substring(1)
                               .toLowerCase());
            }
            if (!(ret.length() == init.length())) {
                ret.append(" ");
            }
        }

        return ret.toString();
    }

    private void process(final String firstname, final String surname) {

        if (surname.matches(INITIALS)) {
            this.firstname = strip(surname).toUpperCase();
            this.surname = strip(firstname);
        } else if (firstname.matches(INITIALS)) {
            this.firstname = strip(firstname).toUpperCase();
            this.surname = strip(surname);
        } else {
            this.firstname = toCamelCase(strip(firstname));
            this.surname = toCamelCase(strip(surname));
        }

        specialCamelCasing("Mac");
        specialCamelCasing("Mc");
    }

    private void specialCamelCasing(final String specialName) {
        if (surname.toLowerCase()
                   .startsWith(specialName.toLowerCase()) && (surname.length() > (specialName.length() + 1))) {
            final StringBuilder ret = new StringBuilder();
            ret.append(surname.substring(0, specialName.length()));
            ret.append(surname.substring(specialName.length(), specialName.length() + 1)
                              .toUpperCase());
            ret.append(surname.substring(specialName.length() + 1));
            surname = ret.toString();
        }
    }

    private String strip(final String text) {
        return text.replace("  ", " ")
                   .replace(",", "");
    }

    public String getFirstname() {
        return firstname;
    }

    public String getSurname() {
        return surname;
    }

    public boolean isJunior() {
        return junior;
    }

    public boolean isFirstnameAsInitials() {
        return firstname.matches(INITIALS);
    }

    public String name() {
        return nameFormatStrategy.format(this);
    }

    public Author asAuthor() {
        return new Author(firstname, surname);
    }

    public void setNameFormatStrategy(final NameFormattingStrategy nameFormatStrategy) {
        this.nameFormatStrategy = nameFormatStrategy;
    }

}
