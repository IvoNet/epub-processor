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

import nl.ivonet.epub.data.ListResource;
import nl.ivonet.epub.strategy.name.NameFormattingStrategy;
import nl.ivonet.epub.strategy.name.SurnameCommaFirstnamesStrategy;
import nl.siegmann.epublib.domain.Author;

import static java.util.Arrays.copyOfRange;

/**
 * @author Ivo Woltring
 */
public class Name {
    private static final String INITIALS = "([a-zA-Z]\\.[ ]*)+|(([a-zA-Z][ ]+)+[a-zA-Z]?)";
    private static final String JUNIOR = " Jr.";
    private static final int FIRSTNAME_IDX = 1;
    private static final int SURNAME_IDX = 0;
    private static final String COMMA_T = " 't";
    private boolean junior;
    private NameFormattingStrategy nameFormatStrategy;
    private String firstname;
    private String surname;

    public Name(final String author) {
        if (author == null) {
            throw new IllegalStateException("There should be an author");
        }
        String name = author.replace("_", ".")
                            .replace("’", "'")
                            .trim();
        junior = name.contains(JUNIOR);
        if (junior) {
            name = name.replace(JUNIOR, "");
        }

        if (name.startsWith("'t, ")) {
            name = name.replace("'t, ", "") + COMMA_T;
            name = name.replaceFirst(" ", ", ");
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
                process(String.join(", ", (CharSequence[]) copyOfRange(strings, 0, strings.length - 1)), surname);
            }
        }

        this.nameFormatStrategy = new SurnameCommaFirstnamesStrategy();
    }

    public Name(final String author, final NameFormattingStrategy nameFormatStrategy) {
        this(author);
        this.nameFormatStrategy = nameFormatStrategy;
    }

    public Name(final Author author) {
        this(author.getFirstname(), author.getLastname());
    }

    public Name(final String firstname, final String surname) {
        this(firstname, surname, new SurnameCommaFirstnamesStrategy());
    }

    public Name(final String firstname, final String surname, final NameFormattingStrategy nameFormatStrategy) {
        junior = firstname.contains(JUNIOR) || surname.contains(JUNIOR);

        this.firstname = ListResource.removeAccents(firstname);
        this.surname = ListResource.removeAccents(surname);

        if (junior) {
            this.firstname = this.firstname.replace(JUNIOR, "");
            this.surname = this.surname.replace(JUNIOR, "");
        }

        process(this.firstname, this.surname);

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
        if (init.length() == 1) {
            return init.toUpperCase();
        }

        final StringBuilder ret = new StringBuilder(init.length());


        for (final String word : init.split(" ")) {
            if (!word.isEmpty()) {
                ret.append(word.substring(0, 1)
                               .toUpperCase());
                ret.append(word.substring(1)
                               .toLowerCase());
            }
            if (ret.length() != init.length()) {
                ret.append(" ");
            }
        }

        return ret.toString();
    }

    // TODO: 11-05-2016 refactor this.
    private void process(final String firstname, final String surname) {
        if ((firstname.split(" ").length > 1) && "A.".equals(surname)) {//A., Heinlein Robert
            final String ret = firstname + " A.";
            extractName(ret);
        } else if ((firstname.split(" ").length > 1) && "C.".equals(surname)) {//C., Clarke arthur
            final String ret = firstname + " C.";
            extractName(ret);
        } else if (nameStartsWithACommaT(surname)) { //'t, Hek Youp van
            final String ret = firstname + COMMA_T;
            extractName(ret);
        } else if (firstname.length() == 1) {
            this.firstname = firstname.toUpperCase() + ".";
            this.surname = toCamelCase(strip(surname));
        } else if (surname.matches(INITIALS)) {
            this.firstname = strip(surname).toUpperCase();
            this.surname = strip(firstname);
        } else {
            if (firstname.matches(INITIALS)) {
                this.firstname = strip(firstname).toUpperCase();
                this.surname = strip(surname);
            } else {
                this.firstname = toCamelCase(strip(firstname));
                this.surname = toCamelCase(strip(surname));
            }
        }

        // TODO: 11-05-2016 beautify! this is ugly as sin
        if ((this.firstname.length() > 2) && this.firstname.endsWith(".") && !this.firstname.matches(INITIALS)
            && !this.firstname.contains(" ")) {
            this.firstname = this.firstname.substring(0, this.firstname.lastIndexOf("."));
        }
        // TODO: 11-05-2016 beautify! this is ugly as sin
        if ((this.surname.length() > 2) && this.surname.endsWith(".") && !this.surname.matches(INITIALS)
            && !this.surname.contains(" ")) {
            this.surname = this.surname.substring(0, this.surname.lastIndexOf("."));
        }

        specialCamelCasing("Mac");
        specialCamelCasing("Mc");
    }

    private void extractName(final String ret) {
        final int firstIndexOfSpace = ret.indexOf(" ");
        this.surname = ret.substring(0, firstIndexOfSpace);
        this.firstname = ret.substring(firstIndexOfSpace + 1);
    }

    private boolean nameStartsWithACommaT(final String surname) {
        return "'t".equals(surname);
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

    public void setJunior(final boolean junior) {
        this.junior = junior;
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

    public boolean hasFirstname() {
        return (this.firstname != null) && !this.firstname.isEmpty();
    }
}
