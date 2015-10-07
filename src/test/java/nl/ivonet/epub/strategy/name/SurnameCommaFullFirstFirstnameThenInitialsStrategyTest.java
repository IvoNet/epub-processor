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
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Ivo Woltring
 */
public class SurnameCommaFullFirstFirstnameThenInitialsStrategyTest {

    private SurnameCommaFullFirstFirstnameThenInitialsStrategy strategy;

    @Before
    public void setUp() throws Exception {
        strategy = new SurnameCommaFullFirstFirstnameThenInitialsStrategy();
    }

    @Test
    public void testName() throws Exception {
        final Name name = new Name("Woltring, Ivo Agnes Hendricus");
        name.setNameFormatStrategy(strategy);
        assertEquals("Woltring, Ivo A.H.", name.name());
    }

    @Test
    public void testNameWithInitials() throws Exception {
        final Name name = new Name("I_A_H_ Woltring");
        name.setNameFormatStrategy(strategy);
        assertEquals("Woltring, I.A.H.", name.name());
    }
}