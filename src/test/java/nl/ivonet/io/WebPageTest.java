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

package nl.ivonet.io;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

/**
 * @author Ivo Woltring
 */
public class WebPageTest {

    private static final String HELLO_WORLD = "<html><head></head><body><h1>Hello World</h1></body></html>";
    private WebPage webPage;

    @Before
    public void setUp() throws Exception {
        webPage = WebPage.getInstance();
    }


    @Test
    public void retrieveWebDocument() throws Exception {
        final Document doc = webPage.get("http://google.com");
        assertNotNull(doc);
        final Elements select = doc.body()
                                   .select("input[value*=Google]");
        assertThat(select.size(), is(1));
    }

    @Test
    public void retrieveWebDocumentNotExisting() throws Exception {
        final Document doc = webPage.get("http://blablba.iDoNotExist");
        assertNotNull(doc);
        assertThat(doc.toString(), is("<html>\n <head></head>\n <body></body>\n</html>"));
    }

    @Test
    public void parseWebDocument() throws Exception {
        final Document doc = webPage.parse(HELLO_WORLD);
        assertNotNull(doc);
        assertThat(doc.body()
                      .select("h1")
                      .text(), is("Hello World"));
    }

    @Test
    public void evaluateXpath() throws Exception {
        final Document doc = webPage.parse(HELLO_WORLD);
        assertThat(webPage.evaluateXpath(doc, "//h1/text()"), is(Arrays.asList(new String[]{"Hello World"})));
    }

    @Test
    public void evaluateXpathUrl() throws Exception {
        assertThat(webPage.evaluateXpath("http://www.watbenjedan.nl", "//tbody/tr/td/font/b/text()"),
                   is(Arrays.asList(new String[]{"Een prutser !!!"})));
    }

    @Test
    public void getListfromDoc() throws Exception {
        final Document doc = webPage.parse(
                "<html><head></head><body><ul><li><a href='/one'>one</a></li><li><a href='/two'>two</a></li><li"
                + "><a href='/three'>three</a></li></ul></body></html>");

        final Elements li = doc.select("a");
        assertThat(li.size(), is(3));
        assertThat(li.get(0)
                     .attr("href"), is("/one"));
        assertThat(li.get(0)
                     .text(), is("one"));
    }

    @Test
    public void getListFromXpath() throws Exception {
        final Document doc = webPage.parse(
                "<html><head></head><body><ul><li><a href='/one'>one</a></li><li><a href='/two'>two</a></li><li"
                + "><a href='/three'>three</a></li></ul></body></html>");
        final List<String> strings = webPage.evaluateXpath(doc, "//a/@href");
        assertNotNull(strings);
        assertThat(strings.size(), is(3));

    }
}