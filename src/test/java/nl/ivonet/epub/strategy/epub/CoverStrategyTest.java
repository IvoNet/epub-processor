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

import nl.ivonet.epub.domain.Dropout;
import nl.ivonet.epub.domain.Epub;
import nl.ivonet.epub.metadata.MetadataFactory;
import nl.siegmann.epublib.domain.Author;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static nl.ivonet.util.EpubTestUtils.createTestEpub;
import static org.junit.Assert.assertFalse;

/**
 * @author Ivo Woltring
 */
public class CoverStrategyTest {
    private CoverStrategy strategy;
    private Epub epub;

    @Before
    public void setUp() throws Exception {
        strategy = new CoverStrategy(new MetadataFactory());
        epub = createTestEpub();
    }

    @Test
    public void testExecute() throws Exception {
        //preparing testcase. needs COVER dropout
        epub.addDropout(Dropout.COVER);

        //Needs a real author
        final List<Author> authors = new ArrayList<>();
        authors.add(new Author("Ilona", "Andrews"));
        epub.setAuthors(authors);

        //Needs a real title
        final List<String> titles = new ArrayList<>();
        titles.add("Magic Slays");
        epub.setTitles(titles);

        //perform test
        strategy.execute(epub);
        assertFalse(epub.hasDropout(Dropout.COVER));
    }
}