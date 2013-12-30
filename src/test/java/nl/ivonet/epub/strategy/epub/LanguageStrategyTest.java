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

package nl.ivonet.epub.strategy.epub;

import nl.ivonet.epub.domain.Epub;
import nl.ivonet.util.BaseT;
import nl.ivonet.util.EpubTestUtils;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 *
 * @author Ivo Woltring
 */
public class LanguageStrategyTest extends BaseT {
    private LanguageStrategy strategy;
    private Epub epub;

    @Before
    public void setUp() throws Exception {
        strategy = new LanguageStrategy();
        epub = EpubTestUtils.createTestEpub();
    }

    @Test
    public void testExecute1() throws Exception {
        epub.setLanguage("en");
        strategy.execute(epub);
        assertEquals("en", epub.getLanguage());

    }

    @Test
    public void testExecute2() throws Exception {
        epub.setLanguage("nl");
        strategy.execute(epub);
        assertEquals("en", epub.getLanguage());
    }


//    @Test
//    public void testContents() throws Exception {
//        final List<Resource> contents = epub.getContents();
//        final Resource resource = contents.get(2);
//        final Reader reader = resource.getReader();
//        final String res = HtmlUtils.extractText(reader);
//
//        strategy.execute(epub);
//
//        assertEquals("en", epub.getLanguage());
//    }
}
