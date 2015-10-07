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

import java.util.Arrays;
import java.util.List;

/**
 * Surname, Firstname [A-Z.)* This strategy will only work if full names are available to begin with.
 *
 * @author Ivo Woltring
 */
public class SurnameCommaFullFirstFirstnameThenInitialsStrategy implements NameFormattingStrategy {
    @Override
    public String format(final Name name) {
        String initials = "";
        if (name.isFirstnameAsInitials()) {
            initials = name.getFirstname();
        } else {

            final String[] split = name.getFirstname()
                                       .split(" ");
            final List<String> firstnames = Arrays.asList(split);
            if (!firstnames.isEmpty()) {
                int idx = 0;
                if (firstnames.size() > 1) {
                    initials = firstnames.get(0) + " ";
                    idx = 1;
                }
                for (int i = idx; i < firstnames.size(); i++) {
                    final String subname = firstnames.get(i);
                    if (!subname.isEmpty()) {
                        initials = initials + subname.substring(0, 1) + ".";
                    }
                }
            }
        }
        return finalFormat(name, initials);
    }

    private String finalFormat(final Name name, final String initials) {
        if (name.isJunior()) {
            return name.getSurname() + " Jr., " + initials;
        }
        return name.getSurname() + ", " + initials;
    }


}
