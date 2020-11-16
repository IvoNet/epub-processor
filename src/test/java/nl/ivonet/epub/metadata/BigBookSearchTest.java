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

package nl.ivonet.epub.metadata;

import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.service.MediatypeService;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotNull;

/**
 * @author Ivo Woltring
 */
public class BigBookSearchTest {

    private BigBookSearch bigBookSearch;

    @Before
    public void setUp() throws Exception {
        bigBookSearch = new MetadataFactory().getBigBookSearchInstance();
    }

    @Test
    public void retrievePossibles() throws Exception {
        final BigBookResults covers = bigBookSearch.retrievePossibles("ilona andrews magic slays");
        assertNotNull(covers);

        final String s1 = covers.getResults()
                                .stream()
                                .filter(s -> s.getTitle()
                                              .toLowerCase()
                                              .contains("magic slays"))
                                .map(BigBookImage::getImage)
                                .findAny()
                                .orElse(null);
        assertNotNull(s1);
        assertThat(s1, is("https://m.media-amazon.com/images/I/51RUImTGIeL.jpg"));
    }

    @Test
    public void findByAuthorAndThenTile() throws Exception {
        final Resource resource = bigBookSearch.findByAutorAndThenTitle("Ilona Andrews", "magic slays");
        assertNotNull(resource);
        assertThat(resource.getHref(), is("cover.jpg"));
        assertThat(resource.getMediaType(), is(MediatypeService.determineMediaType(".jpg")));
    }


}
