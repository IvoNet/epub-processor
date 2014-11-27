/*
 * Copyright (c) 2014 Ivo Woltring
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

package authors;

import org.junit.Ignore;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Converts a name with combination "firstname+ Lastname" to "Lastname, firstname+".
 *
 * @author Ivo Woltring
 */
public class LastNameFirstConverterTest {

    private String lastFirst(final String[] names) {
        final int k = names.length;
        if (k == 1) {
            return names[0];
        }
        final StringBuilder out = new StringBuilder();
        out.append(names[k - 1].trim())
           .append(",");
        for (int x = 0; x < (k - 1); x++) {
            out.append(" ")
               .append(names[x].trim());
        }
        return out.toString()
                  .trim();
    }

    @Ignore
    @Test
    public void testLastFirst() throws Exception {
        try (final InputStreamReader in = new InputStreamReader(new FileInputStream(
                "/Users/ivonet/dev/ebook/epub-processor/python/names/authors.txt"));
             final BufferedReader br = new BufferedReader(in)) {

            final String name = br.lines()
                                  .filter(p -> !(p.isEmpty()))
                                  .map(p -> p.replaceAll("\\(.+\\)", "")
                                             .replaceAll("\\.[ ]+", ".")
                                             .replace(".", ". "))
                                  .map(p -> p.split(" "))
                                  .filter(p -> p.length > 0)
                                  .map(this::lastFirst)
                                  .sorted()
                                  .reduce((s, s2) -> s + "\n" + s2)
                                  .orElse("");
            System.out.println(name);
//              .collect(Collectors.toList());

            Files.write(Paths.get("/Users/ivonet/dev/ebook/epub-processor/python/names/authorsLastFirst.txt"),
                        name.getBytes());

        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }


}
