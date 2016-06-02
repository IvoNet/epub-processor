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

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Ivo Woltring
 */
public class BigBookSearch {

    private static final String location =
            "http://bigbooksearch.com/query" + ".php?SearchIndex=books&Keywords=%s&ItemPage=1";
    private final WebPage html;

    public BigBookSearch(final WebPage html) {
        this.html = html;
    }

    public Map<String, String> retrievePossibles(final String search) {
        final String tokens = tokenize(search);
        final Document document = html.get(bigBookSearchUrl(tokens));
        final Map<String, String> pictures = new HashMap<>();
        document.body()
                .select("img")
                .stream()
                .forEach(element -> pictures.put(element.attr("alt"), element.attr("src")));
        return pictures;
    }


    public Resource findByAutorAndTitle(final String author, final String title) {
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
            return new Resource(bytes, cover);
        } catch (final IOException e) {
            return null;
        }
    }

    private String bigBookSearchUrl(final String value) {
        return String.format(location, value);
    }

    private String tokenize(final String value) {
        return value.replace(" ", "+");
    }
}
