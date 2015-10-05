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
public class SurnameCommaInitialsStrategyTest {

    private SurnameCommaInitialsStrategy strategy;

    @Before
    public void setUp() throws Exception {
        strategy = new SurnameCommaInitialsStrategy();
    }

    @Test
    public void testStragegy() throws Exception {
        final Name name = new Name("Ivo Woltring");
        final String format = strategy.format(name);
        assertEquals("Woltring, I.", format);
    }

    @Test
    public void testStragegy2() throws Exception {
        final Name name = new Name("Ivo Agnes Hendricus Woltring");
        final String format = strategy.format(name);
        assertEquals("Woltring, I.A.H.", format);
    }

    @Test
    public void testStragegy3() throws Exception {
        final Name name = new Name("Woltring, Ivo Agnes Hendricus");
        final String format = strategy.format(name);
        assertEquals("Woltring, I.A.H.", format);
    }

    @Test
    public void testStragegy4() throws Exception {
        final Name name = new Name("Woltring, Ivo Agnes Hendricus Jr.");
        final String format = strategy.format(name);
        assertEquals("Woltring Jr., I.A.H.", format);
    }

    @Test
    public void testStragegy5() throws Exception {
        final Name name = new Name("Woltring, I.A.H. Jr.");
        final String format = strategy.format(name);
        assertEquals("Woltring Jr., I.A.H.", format);
    }
}