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

import nl.ivonet.epub.strategy.name.FirstnameSpaceSurnameStrategy;
import nl.ivonet.epub.strategy.name.SurnameCommaFirstnameStrategy;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 *
 * @author Ivo Woltring
 */
public class NameTest {

    @Test
    public void testNameFormatting() throws Exception {
        final Name name = new Name("Ivo", "Woltring");
        assertEquals("Woltring, Ivo", name.name());
        assertEquals("Ivo Woltring", new FirstnameSpaceSurnameStrategy().format(name));

        final Name name2 = new Name("Ivo", "Woltring", new FirstnameSpaceSurnameStrategy());
        assertEquals("Ivo Woltring", name2.name());
        assertEquals("Woltring, Ivo", new SurnameCommaFirstnameStrategy().format(name2));
    }


}
