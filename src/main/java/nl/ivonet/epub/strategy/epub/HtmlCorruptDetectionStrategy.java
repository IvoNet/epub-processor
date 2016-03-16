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

package nl.ivonet.epub.strategy.epub;

import nl.ivonet.epub.annotation.ConcreteEpubStrategy;
import nl.ivonet.epub.domain.Dropout;
import nl.ivonet.epub.domain.Epub;
import nl.siegmann.epublib.domain.MediaType;
import nl.siegmann.epublib.domain.Resource;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Strategy to detect corrupt html.
 *
 * Corrupt html is often an indication for a bad book or encrypted book. These books need to be identified
 *
 * @author Ivo Woltring
 */
@ConcreteEpubStrategy
public class HtmlCorruptDetectionStrategy implements EpubStrategy {


    // adapted from post by Phil Haack and modified to match better
    public static final String tagStart =
            "\\<\\w+((\\s+\\w+(\\s*\\=\\s*(?:\".*?\"|'.*?'|[^'\"\\>\\s]+))?)+\\s*|\\s*)" + "\\>";
    public static final String tagEnd = "\\</\\w+\\>";
    public static final String tagSelfClosing =
            "\\<\\w+((\\s+\\w+(\\s*\\=\\s*(?:\".*?\"|'.*?'|[^'\"\\>\\s]+))?)" + "+\\s*|\\s*)/\\>";
    public static final String htmlEntity = "&[a-zA-Z][a-zA-Z0-9]+;";
    public static final Pattern htmlPattern = Pattern.compile(
            "(" + tagStart + ".*" + tagEnd + ")|(" + tagSelfClosing + ")|(" + htmlEntity + ")", Pattern.DOTALL);


    @Override
    public void execute(final Epub epub) {

        try {
            for (final Resource content : getHtmlContents(epub)) {
                final String html = IOUtils.toString(content.getReader());
                if (!isContentHtml(html)) {
                    epub.addDropout(Dropout.CORRUPT_HTML);
                    break;
                }
            }
        } catch (IOException e) {
            epub.addDropout(Dropout.READ_ERROR);
        }

    }

    public static List<Resource> getHtmlContents(Epub epub) {
        return epub.getContents()
                   .stream()
                   .filter(HtmlCorruptDetectionStrategy::isHtml)
                   .collect(Collectors.toList());
    }


    public static boolean isContentHtml(String s) {
        return (s != null) && htmlPattern.matcher(s)
                                         .find();
    }

    public static boolean isHtml(final Resource content) {
        final MediaType mediaType = content.getMediaType();
        return (mediaType != null) && "application/xhtml+xml".equals(mediaType.toString());
    }

}
