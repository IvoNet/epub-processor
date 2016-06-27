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
import org.junit.Ignore;
import org.junit.Test;

import java.util.Map;

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
        final Map<String, String> covers = bigBookSearch.retrievePossibles("ilona andrews");
        assertNotNull(covers);

        final String s1 = covers.keySet()
                                .stream()
                                .filter(s -> s.toLowerCase()
                                              .contains("magic slays"))
                                .findAny()
                                .orElse(null);
        assertNotNull(s1);
        assertThat(covers.get(s1), is("http://ecx.images-amazon.com/images/I/51L71r%2Bb12L.jpg"));
    }

    @Test
    public void findByAuthorAndThenTile() throws Exception {
        final Resource resource = bigBookSearch.findByAutorAndThenTitle("Ilona Andrews", "magic slays");
        assertNotNull(resource);
        assertThat(resource.getHref(), is("cover.jpg"));
        assertThat(resource.getMediaType(), is(MediatypeService.determineMediaType(".jpg")));
    }

    @Ignore //Not finished yet
    @Test
    public void findByAuthorAndTitle() throws Exception {
        final Resource resource = bigBookSearch.findByAutorAndTitle("Ilona Andrews magic slays");
        assertNotNull(resource);
        assertThat(resource.getHref(), is("cover.jpg"));
        assertThat(resource.getMediaType(), is(MediatypeService.determineMediaType(".jpg")));


    }
}