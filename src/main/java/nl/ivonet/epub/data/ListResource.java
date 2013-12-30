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

package nl.ivonet.epub.data;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.Normalizer;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author Ivo Woltring
 */
public abstract class ListResource {

    protected ListResource() {
    }

    public static String removeAccents(final String text) {
        return (text == null) ? null : Normalizer.normalize(text, Normalizer.Form.NFD)
                                                 .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
    }

    /**
     * Extracts a List of string from a file on the classpath.
     *
     * Try with resources containing a labmda expression where the lines are read in parallel and mapped to trim
     * the lines and collected into a list of strings which are not empty.
     */
    public List<String> listFromFilename(final String filename) {
        final String location = ListResource.class.getResource("/" + filename)
                                                  .toExternalForm();
        try (final InputStreamReader in = new InputStreamReader(new URL(location).openStream());
             final BufferedReader br = new BufferedReader(in)) {
            return br.lines()
                     .parallel()
                     .map(String::trim)
                     .filter(p -> !p.isEmpty())
                     .collect(Collectors.toList());
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean areInitials(final String input) {
        return input.contains(".");
    }

    public abstract boolean is(String input);
}
