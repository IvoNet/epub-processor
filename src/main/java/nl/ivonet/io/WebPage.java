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

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import us.codecraft.xsoup.Xsoup;

import java.io.IOException;
import java.util.List;

/**
 * @author Ivo Woltring
 */
public class WebPage {

    private WebPage() {
    }

    /**
     * A document representation of the given url or an empty document if not found.
     */
    public Document get(final String url) {
        try {
            return Jsoup.connect(url)
                        .timeout(5000)
                        .get();
        } catch (final IOException ignored) {
            return Jsoup.parse("");
        }
    }

    public Document parse(final String html) {
        return Jsoup.parse(html);
    }

    public List<String> evaluateXpath(final String url, final String xpath) {
        return evaluateXpath(get(url), xpath);
    }

    public List<String> evaluateXpath(final Document document, final String xpath) {
        return Xsoup.compile(xpath)
                    .evaluate(document)
                    .list();

    }

    private static final class Instance {
        static final WebPage SINGLETON = new WebPage();
    }

    public static WebPage getInstance() {
        return Instance.SINGLETON;
    }
}