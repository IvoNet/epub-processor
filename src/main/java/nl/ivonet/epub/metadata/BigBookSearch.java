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

import nl.ivonet.io.JsonResource;
import nl.siegmann.epublib.domain.Resource;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;

/**
 * @author Ivo Woltring
 */
public class BigBookSearch {
    private static final Logger LOG = LoggerFactory.getLogger(BigBookSearch.class);

    private static final String NO_RESULTS = "no_results";

    private static final String location = "https://bigbooksearch.com/please-dont-scrape-my-site-you-will-put-my-api-key-over-the-usage-limit-and-the-site-will-break/books/%s";
    private final JsonResource<BigBookResults> jsonResource;


    public BigBookSearch(final JsonResource<BigBookResults> jsonResource) {
        this.jsonResource = jsonResource;
    }

    BigBookResults retrievePossibles(final String search) {
        final String tokens = tokenize(search);
        return jsonResource.get(bigBookSearchUrl(tokens));
    }

    public Resource findByAutorAndThenTitle(final String author,
                                            final String title) {
        final String tokens = tokenize(author + " " + title);
        final BigBookResults covers = retrievePossibles(tokens.toLowerCase());
        final String cover = covers.getResults()
                                   .stream()
                                   .filter(s -> s.getTitle()
                                                 .toLowerCase()
                                                 .startsWith(title.toLowerCase()))
                                   .map(BigBookImage::getImage)
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

    private String bigBookSearchUrl(final String value) {
        return String.format(location, value);
    }

    private String tokenize(final String value) {
        return value.replace(" ", "%20");
    }
}
