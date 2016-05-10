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
import nl.siegmann.epublib.domain.Resource;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

// TODO: 10-05-2016 rename content.getId() because it could also contain an identifier

/**
 * Some companies do not do the DRM according commnents.
 *
 * @author Ivo Woltring
 */
@ConcreteEpubStrategy
public class DitigalWatermarkRemovalStrategy implements EpubStrategy {
    private static final Logger LOG = LoggerFactory.getLogger(FileSizeStrategy.class);
    private static final String WATERMARK = "Dit bestand is voorzien van een watermerk met informatie die jou "
                                            + "aanmerkt als de eigenaar van de licentie om misbruik voorkomen.";
    private static final Pattern WATERMARK_PAT = Pattern.compile(
            ">Dit eBook is voorzien van een watermerk met identificatiecode.*?: (.*?) - (.*?)</p>",
            Pattern.CASE_INSENSITIVE);
    private static final Pattern WATERMARK_PAT_2 = Pattern.compile("title=\"(.*?)\"", Pattern.CASE_INSENSITIVE);
    private static final String REMOVED_WATERMARK_TXT =
            "Het watermerk is weggehaald als bescherming van de privacy " + "van de koper.";
    private static final String IS_WATERMARK = "Het eBook is voorzien van een watermerk";
    private static final String WAS_WATERMARK = "Het eBook was voorzien van een watermerk";
    private static final Pattern JS_IMAGE = Pattern.compile("");


    @Override
    public void execute(final Epub epub) {
        LOG.debug("Applying {} on [{}]", getClass().getSimpleName(), epub.getOrigionalFilename());

        final List<Resource> htmlResources = epub.getContents()
                                                 .stream()
                                                 .filter(HtmlCorruptDetectionStrategy::isHtml)
                                                 .collect(Collectors.toList());


        for (final Resource content : htmlResources) {
            try {
                String html = IOUtils.toString(content.getReader());
                html = removeWatermark1(html);
                html = removeWatermark2(html);
                html = removeWatermark3(html);
                html = removeJavaScript(html);
                html = removeDataImg(html);
                content.setData(html.getBytes());
            } catch (IOException e) {
                epub.addDropout(Dropout.READ_ERROR);
            }

        }


    }

    /**
     * removes watermark from a simple page.
     */
    private String removeWatermark1(final String html) {
        final Matcher matcher = WATERMARK_PAT.matcher(html);
        final StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb, ">" + WAS_WATERMARK + "</p>");
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    /**
     * Removes watermarks from title attributes and replaces some strings
     */
    private String removeWatermark2(final String html) {
        final Matcher matcher = WATERMARK_PAT_2.matcher(html);
        final StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb, "title=\"Possible watermark removed\""); //you can put any text here
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    private String removeWatermark3(final String html) {
        return html.replace(IS_WATERMARK, WAS_WATERMARK)
                   .replace(WATERMARK, REMOVED_WATERMARK_TXT);
    }


    private String removeDataImg(final String html) {
        return html.replaceAll("<div class=\"dataImg\"><img src=\"data:image/png;base64,.*?=\".*?/></div>", "");
    }


    private String removeJavaScript(final String html) {
        return html.replaceAll(
                "<script type=\"text/javascript\" src=\"data:application/x-javascript;base64,.*?\".*?/script>", "");
    }
}
