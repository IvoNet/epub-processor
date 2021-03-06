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

package nl.ivonet.epub.annotation;

import nl.ivonet.epub.domain.Epub;
import nl.ivonet.epub.strategy.epub.EpubStrategy;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.easymock.EasyMock.createMock;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author Ivo Woltring
 */
public class EpubStrategyDependencyFinderTest {

    private Epub epubMock;

    @Before
    public void setUp() throws Exception {
        epubMock = createMock(Epub.class);

    }

    @Test
    public void testLoad() throws Exception {
        final EpubStrategyDependencyFinder finder = new EpubStrategyDependencyFinder("foo.strategy.epub");
        final List<EpubStrategy> strategies = finder.load();
        assertNotNull(strategies);
        assertEquals(3, strategies.size());
        assertEquals("IAmAStrategy", strategies.get(0)
                                               .getClass()
                                               .getSimpleName());
        for (final EpubStrategy strategy : strategies) {
            strategy.execute(epubMock);
        }
    }

    @Test
    public void ordering() throws Exception {
        final EpubStrategyDependencyFinder finder = new EpubStrategyDependencyFinder("foo.strategy.epub");
        final List<EpubStrategy> strategies = finder.load();
        assertNotNull(strategies);
        assertEquals(3, strategies.size());
        assertThat(strategies.get(1)
                             .getClass()
                             .getSimpleName(), is("IAmAStrategyWithOrderOne"));
        assertThat(strategies.get(2)
                             .getClass()
                             .getSimpleName(), is("IAmAStrategyWithOrderTwo"));


    }


}
