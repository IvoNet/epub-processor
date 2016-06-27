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

import nl.ivonet.io.WebPage;
import nl.siegmann.epublib.domain.Resource;
import org.apache.commons.io.IOUtils;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Ivo Woltring
 */
public class BigBookSearch {
    private static final Logger LOG = LoggerFactory.getLogger(BigBookSearch.class);

    private static final String NO_RESULTS = "no_results";

    private static final String location = "http://bigbooksearch.com/query"
                                           + ".php?SearchIndex=books&Keywords=%s&ItemPage=%s";
    private final WebPage webPage;

    public BigBookSearch(final WebPage webPage) {
        this.webPage = webPage;
    }

    Map<String, String> retrievePossibles(final String search) {
        final String tokens = tokenize(search);
        final Map<String, String> pictures = new HashMap<>();
        int page = 1;
        Document document = webPage.get(bigBookSearchUrl(tokens, page));
        while (!NO_RESULTS.equals(document.body()
                                          .text()) && (page <= 10)) {
            LOG.debug("Searching cover for [{}] on page [{}]", search, page);
            document.body()
                    .select("img")
                    .forEach(element -> pictures.put(element.attr("alt"), element.attr("src")));
            page++;
            document = webPage.get(bigBookSearchUrl(tokens, page));
        }
        return pictures;
    }

    public Resource findByAutorAndThenTitle(final String author, final String title) {
        final Map<String, String> covers = retrievePossibles(author.toLowerCase());
        final String cover = covers.keySet()
                                   .stream()
                                   .filter(s -> s.toLowerCase()
                                                 .startsWith(title.toLowerCase()))
                                   .map(covers::get)
                                   .findFirst()
                                   .orElse("");
        if (cover.isEmpty()) {
            return null;
        }
        try {
            final byte[] bytes = IOUtils.toByteArray(new URL(cover).openConnection());
            return new Resource(bytes, "cover" + retrieveExtension(cover));
        } catch (final IOException e) {
            return null;
        }
    }


    public Resource findByAutorAndTitle(final String search) {
        final Map<String, String> covers = retrievePossibles(search.toLowerCase());
        final String cover = covers.keySet()
                                   .stream()
                                   .filter(s -> s.toLowerCase()
                                                 .startsWith(search.toLowerCase()))
                                   .map(covers::get)
                                   .findFirst()
                                   .orElse("");
        if (cover.isEmpty()) {
            return null;
        }
        try {
            final byte[] bytes = IOUtils.toByteArray(new URL(cover).openConnection());
            return new Resource(bytes, "cover" + retrieveExtension(cover));
        } catch (final IOException e) {
            return null;
        }
    }

    private String retrieveExtension(final String name) {
        return name.substring(name.lastIndexOf("."));
    }

    private String bigBookSearchUrl(final String value, final int page) {
        return String.format(location, value, page);
    }

    private String tokenize(final String value) {
        return value.replace(" ", "+");
    }
}
