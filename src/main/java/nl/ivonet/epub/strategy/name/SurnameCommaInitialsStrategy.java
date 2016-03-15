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

package nl.ivonet.epub.strategy.name;

import nl.ivonet.epub.domain.Name;

/**
 * Surname, ([A-Z].)+
 *
 * @author Ivo Woltring
 */
public class SurnameCommaInitialsStrategy implements NameFormattingStrategy {
    @Override
    public String format(final Name name) {
        String initials = "";
        if (name.isFirstnameAsInitials()) {
            initials = name.getFirstname();
        } else if (!name.getFirstname()
                        .isEmpty()) {
            final String[] firstnames = name.getFirstname()
                                            .split(" ");
            for (final String firstname : firstnames) {
                initials = initials + (firstname.isEmpty() ? "" : firstname.substring(0, 1)) + ".";
            }
        }
        if (name.isJunior()) {
            return name.getSurname() + " Jr., " + initials;
        }
        return name.getSurname() + ", " + initials;
    }
}
