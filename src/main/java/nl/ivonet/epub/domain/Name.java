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
import nl.ivonet.epub.strategy.name.SurnameCommaFirstnameStrategy;
import nl.siegmann.epublib.domain.Author;

/**
 *
 * @author Ivo Woltring
 */
public class Name {
    private final String firstname;
    private final String surname;
    private final NameFormattingStrategy nameFormatStrategy;

    public Name(final Author author) {
        this(author.getFirstname(), author.getLastname());
    }

    public Name(final String firstname, final String surname) {
        this(firstname, surname, new SurnameCommaFirstnameStrategy());
    }

    public Name(final String firstname, final String surname, final NameFormattingStrategy nameFormatStrategy) {
        this.firstname = firstname;
        this.surname = surname;
        this.nameFormatStrategy = nameFormatStrategy;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getSurname() {
        return surname;
    }

    public String name() {
        return nameFormatStrategy.format(this);
    }

    public Author asAuthor() {
        return new Author(firstname, surname);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Name)) {
            return false;
        }

        final Name name = (Name) o;

        return !((this.firstname != null) ? !this.firstname
                .equals(name.firstname) : (name.firstname != null)) && !((this.surname != null) ? !this.surname
                .equals(name.surname) : (name.surname != null));

    }

    @Override
    public int hashCode() {
        int result = (this.firstname != null) ? this.firstname
                .hashCode() : 0;
        result = 31 * result + ((this.surname != null) ? this.surname
                .hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Name{");
        sb.append("firstname='")
          .append(firstname)
          .append('\'');
        sb.append(", surname='")
          .append(surname)
          .append('\'');
        sb.append('}');
        return sb.toString();
    }
}
