/*
 * Copyright (c) 2016 Ivo Woltring
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
import nl.ivonet.util.EpubTestUtils;
import nl.siegmann.epublib.domain.Resource;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author Ivo Woltring
 */
public class KepubStrategyTest {
    private KepubStrategy strategy;
    private Epub epub;

    @Before
    public void setUp() throws Exception {
        strategy = new KepubStrategy();
        epub = EpubTestUtils.createTestEpub();

    }

    @Test
    public void testExecute() throws Exception {
        strategy.execute(epub);
        final List<Resource> collect = epub.getContents()
                                           .stream()
                                           .filter(p -> p.getMediaType()
                                                         .toString()
                                                         .contains("application/xhtml+xml"))
                                           .collect(toList());
        assertFalse(collect.isEmpty());
        final String html = IOUtils.toString(collect.get(1).getReader());
        assertTrue(html.contains("<h1 id=\"kobo.1.1\">"));
        assertTrue(html.contains("<p class=\"ivonet\" id=\"kobo.2.1\">"));
    }
}