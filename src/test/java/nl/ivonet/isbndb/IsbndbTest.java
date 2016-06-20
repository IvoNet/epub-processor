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

package nl.ivonet.isbndb;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import nl.ivonet.isbndb.model.IsbndbSearch;
import nl.ivonet.isbndb.model.Keystats;
import org.hamcrest.core.Is;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertThat;

/**
 * @author Ivo Woltring
 */
public class IsbndbTest {
    private Isbndb isbndb;

    @Before
    public void setUp() throws Exception {
        isbndb = new Isbndb();
    }

    @Test(expected = InvalidApiKeyException.class)
//    @Test
    public void getStats() throws Exception {
        final String key = ""; //if you out a good one here you can test the actual workings
        isbndb.overrideKey(key);
        final Keystats stats = isbndb.stats();
        assertThat(stats.getKeyId(), Is.is(key));
    }

    @Test
    public void broadSearch() throws Exception {
        final String json = readTestResourceAsString("isbndb_author_search.json");
        final Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                                           .create();
        final IsbndbSearch isbndbSearch = gson.fromJson(json, IsbndbSearch.class);
        System.out.println("isbndbSearch = " + isbndbSearch);
        final String s = gson.toJson(isbndbSearch);
        System.out.println("s = " + s);

    }


    public static String readTestResourceAsString(final String fileName) {
        final String abspath = new File(".").getAbsolutePath();
        final String absolutePath = new File(
                abspath.substring(0, abspath.length() - 1) + "src/test/resources/" + fileName).getAbsolutePath();
        try {
            return new String(Files.readAllBytes(Paths.get(absolutePath)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}