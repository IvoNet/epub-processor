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

package nl.ivonet.epub.domain;

import nl.siegmann.epublib.domain.Book;
import org.junit.Before;
import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;

import static org.easymock.EasyMock.createMock;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 *
 * @author Ivo Woltring
 */
public class EpubTest {

    private Book book;
    private Path path;

    @Before
    public void setUp() throws Exception {
        path = Paths.get("/");
        book = createMock(Book.class);
    }

    @Test
    public void testContext() throws Exception {
        final Epub epub = new Epub(path, book);
        assertNotNull(epub.data());
        assertNotNull(epub.path());
    }

    @Test
    public void testDropoutReasons() throws Exception {
        final Epub epub = new Epub(path, book);
        epub.addDropout(Dropout.READ_ERROR);
        epub.addDropout(Dropout.AUTHOR_EMPTY);

        assertTrue(epub.dropoutReasons()
                       .contains("Read error"));
        assertTrue(epub.dropoutReasons()
                       .contains("Author"));

    }

    @Test
    public void testDropoutFolder() throws Exception {
        final Epub epub = new Epub(path, book);
        epub.addDropout(Dropout.READ_ERROR);
        epub.addDropout(Dropout.AUTHOR_EMPTY);
        assertEquals("No_Author_&_Read_error", epub.dropoutFolder());

    }
}
