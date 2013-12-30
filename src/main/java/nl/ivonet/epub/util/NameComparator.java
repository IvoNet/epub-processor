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

package nl.ivonet.epub.util;

import nl.ivonet.epub.domain.Name;

import java.text.Collator;
import java.util.Comparator;

/**
 * Comparator for {@link nl.ivonet.epub.domain.Name}s.
 * @author Ivo Woltring
 */
public class NameComparator implements Comparator<Name> {
    private final Collator textComparator;

    public NameComparator() {
        textComparator = Collator.getInstance();
    }

    @Override
    public int compare(final Name o1, final Name o2) {
        final int compareResult = textComparator.compare(o1.getSurname(), o2.getSurname());
        if (compareResult == 0) {
            return textComparator.compare(o1.getFirstname(), o2.getFirstname());
        }
        return compareResult;
    }
}
