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
import nl.siegmann.epublib.domain.Metadata;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * @author Ivo Woltring
 */
public class TitleStrategyTest extends BaseT {

    private TitleStrategy strategy;
    private Epub epub;

    @Before
    public void setUp() throws Exception {
        strategy = new TitleStrategy();
    }


    @Test
    public void testExecute() throws Exception {
        epub = EpubTestUtils.createTestEpub();
        final Metadata metadata = epub.data()
                                      .getMetadata();

        final List<String> input = new ArrayList<>();
        input.add("this is the first title.");
        input.add("en dit is de tweede titel");
        input.add("en dit is de/ derde titel");

        //override the test titles with my own.
        metadata.setTitles(input);

        //now execute the strategy
        strategy.execute(epub);

        //and see if my expectations are correct.
        assertEquals("This is the First Title.", metadata.getFirstTitle());
        final List<String> titles = metadata.getTitles();
        assertEquals(3, titles.size());
        assertEquals("En dit is de Tweede Titel", titles.get(1));
        assertEquals("En dit is de - Derde Titel", titles.get(2));
    }

    @Test
    public void testExecuteWithOrigTitleContainingAuthors() throws Exception {
        epub = EpubTestUtils.createTestEpub();
        final Metadata metadata = epub.data()
                                      .getMetadata();

        final List<String> input = new ArrayList<>();
        input.add("Bailey, Arnold - Bujold, Lois Mcmaster - Vorkosigan 06.5 - the Mountains of Mourning");

        //override the test titles with my own.
        metadata.setTitles(input);

        //now execute the strategy
        strategy.execute(epub);

        //and see if my expectations are correct.
        assertEquals("The Mountains of Mourning", metadata.getFirstTitle());
    }


    @Test
    public void byFilename() throws Exception {
        epub = EpubTestUtils.createTestEpub(
                "/foo/Bailey, Arnold - Bujold, Lois Mcmaster - Vorkosigan 06.5 - the Mountains of Mourning.kepub.epub");
        makeTitlesEmpty();

        strategy.execute(epub);

        assertEquals("The Mountains of Mourning", epub.getFirstTitle());
    }

    @Test
    public void byFilenameButWithAuthorAtEnd() throws Exception {
        epub = EpubTestUtils.createTestEpub(
                "/foo/Vorkosigan 06.5 - the Mountains of Mourning - Lois Mcmaster Bujold - Arnold Bailey.kepub.epub");
        makeTitlesEmpty();

        strategy.execute(epub);

        assertEquals("The Mountains of Mourning", epub.getFirstTitle());
    }

    private void makeTitlesEmpty() {
        final Metadata metadata = epub.data()
                                      .getMetadata();
        final List<String> input = new ArrayList<>();
        metadata.setTitles(input);
    }
}
