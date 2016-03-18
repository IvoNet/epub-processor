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
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * @author Ivo Woltring
 */
public class NameComparatorTest {

    private NameComparator comparator;

    @Before
    public void setUp() throws Exception {
        comparator = new NameComparator();
    }

    @Test
    public void testCompare() throws Exception {
        final List<Name> names = new ArrayList<>(5);
        names.add(new Name("c", "c"));
        names.add(new Name("a", "c"));
        names.add(new Name("a", "b"));
        names.add(new Name("b", "a"));
        names.add(new Name("a", "a"));
        Collections.sort(names, comparator);

        assertEquals("A.", names.get(0)
                                .getFirstname());
        assertEquals("A", names.get(0)
                               .getSurname());
        assertEquals("B.", names.get(1)
                                .getFirstname());
        assertEquals("A", names.get(1)
                               .getSurname());
        assertEquals("A.", names.get(2)
                                .getFirstname());
        assertEquals("B", names.get(2)
                               .getSurname());
        assertEquals("C.", names.get(4)
                                .getFirstname());
        assertEquals("C", names.get(4)
                               .getSurname());


    }
}
