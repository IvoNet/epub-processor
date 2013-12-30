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

package nl.ivonet.epub.strategy.text;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 *
 * @author Ivo Woltring
 */
public class CapitalizeStrategyTest {

    private TextStrategy strategy;

    @Before
    public void setUp() throws Exception {
        strategy = new CapitalizeStrategy();
    }

    @Test
    public void testCapitalize() throws Exception {
        assertEquals("De Heer des Huizes.", strategy.execute("de heer des huizes."));
        assertEquals("De Heer des Huizes.", strategy.execute("DE HEER DES HUIZES."));

        //Not the first word because of whitespace so no capitalcasing for "de"
        assertEquals(" de Heer des Huizes.", strategy.execute(" DE HEER DES HUIZES."));

        assertEquals("The Lord of the Rings.", strategy.execute("the lord of the rings."));
        assertEquals("Het Dagboek van een Herdershond.", strategy.execute("het dagboek van een herdershond."));
    }
}
