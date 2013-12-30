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

package nl.ivonet.util;

import nl.ivonet.epub.domain.Epub;
import nl.siegmann.epublib.domain.Author;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Identifier;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.domain.TOCReference;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;

/**
 * Utilities for getting a test book.
 *
 * @author Ivo Woltring
 */
public final class EpubTestUtils {

    public static EpubTestUtils getInstance() {
        return new EpubTestUtils();
    }

    public static Book createTestBook() {
        return getInstance().buildTestBook();

    }

    public static Epub createTestEpub() {
        return new Epub(Paths.get("/tmp/testebook.epub"), createTestBook());
    }

    private Book buildTestBook() {
        final Book book = new Book();

        try {
            book.getMetadata()
                .addTitle("epub processor test book 1. The hounds of the baskervilles.");
            book.getMetadata()
                .addTitle("test2");

            book.getMetadata()
                .addIdentifier(new Identifier(Identifier.Scheme.ISBN, "987654321"));
            book.getMetadata()
                .addAuthor(new Author("Ivo", "Net"));
            book.getMetadata()
                .addAuthor(new Author("Ivo2", "Net"));
            book.setCoverPage(new Resource(this.getClass()
                                               .getResourceAsStream("/book1/cover.html"), "cover.html"));
            book.setCoverImage(new Resource(this.getClass()
                                                .getResourceAsStream("/book1/cover.png"), "cover.png"));
            final ArrayList<String> descriptions = new ArrayList<>();
            descriptions.add("This an english description of the book in question");
            book.getMetadata()
                .setDescriptions(descriptions);
            book.addSection("Chapter 1", new Resource(this.getClass()
                                                          .getResourceAsStream("/book1/chapter1.html"),
                                                      "chapter1.html"));
            book.addResource(new Resource(this.getClass()
                                              .getResourceAsStream("/book1/book1.css"), "book1.css"));
            final TOCReference chapter2 = book.addSection("Second chapter", new Resource(this.getClass()
                                                                                             .getResourceAsStream(
                                                                                                     "/book1/chapter2"
                                                                                                     + ".html"),
                                                                                         "chapter2.html"));
            book.addResource(new Resource(this.getClass()
                                              .getResourceAsStream("/book1/flowers_320x240.jpg"), "flowers.jpg"));
            book.addSection(chapter2, "Chapter 2 section 1", new Resource(this.getClass()
                                                                              .getResourceAsStream(
                                                                                      "/book1/chapter2_1" + ".html"),
                                                                          "chapter2_1.html"));
            book.addSection("Chapter 3", new Resource(this.getClass()
                                                          .getResourceAsStream("/book1/chapter3.html"),
                                                      "chapter3.html"));
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
        return book;
    }
}
