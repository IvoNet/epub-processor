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
 * Surname, [A-Z].
 *
 * @author Ivo Woltring
 */
public class SurnameCommaFirstInitialsStrategy implements NameFormattingStrategy {
    @Override
    public String format(final Name name) {
        String initial = "";
        if (!name.getFirstname()
                 .isEmpty()) {
            initial = name.getFirstname()
                          .substring(0, 1) + ".";
        }
        if (name.isJunior()) {
            return name.getSurname() + " Jr., " + initial;
        }
        return name.getSurname() + ", " + initial;
    }
}
