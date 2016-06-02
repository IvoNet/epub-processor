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

/**
 * Strategy for making epubs kobo e-reader compatible.
 *
 * I have a kobo myself and a kobo delivers better statistics if it has the right information. the beauty is that it
 * does not break the epub format. It just adds a bit more information to the html files zo that it can better track
 * progress. The code below adds that extra bit of information
 *
 * Ordering should be after the {@link HtmlCorruptDetectionStrategy}
 *
 * @author Ivo Woltring
 */
@ConcreteEpubStrategy(order = 10)
public class KepubStrategy implements EpubStrategy {
    private static final Logger LOG = LoggerFactory.getLogger(KepubStrategy.class);

    private static final Pattern PAT = Pattern.compile("<(h\\d|p)(([ ][^>]*)*)>", Pattern.CASE_INSENSITIVE);
    private static final String KOBO = " id=\"kobo.%s.1\">";

    @Override
    public void execute(final Epub epub) {
        LOG.debug("Applying {} on [{}]", getClass().getSimpleName(), epub.getOrigionalFilename());
        if (epub.hasDropout(Dropout.CORRUPT_HTML)) {
            LOG.debug("Html corruption already detected so skipping kepub processing");
            return;
        }
        final List<Resource> htmlResources = epub.getContents()
                                                 .stream()
                                                 .filter(HtmlCorruptDetectionStrategy::isHtml)
                                                 .collect(Collectors.toList());

        int idx = 1;
        for (final Resource content : htmlResources) {
            try {
                final String html = IOUtils.toString(content.getReader());
                final Matcher matcher = PAT.matcher(html);
                final StringBuffer sb = new StringBuffer();
                while (matcher.find()) {
                    final String group = matcher.group();
                    if (doNotProcess(group)) {
                        continue;
                    }
                    matcher.appendReplacement(sb, group.replace(">", String.format(KOBO, idx)));
                    idx++;
                }
                matcher.appendTail(sb);
                content.setData(sb.toString()
                                  .getBytes());

            } catch (IOException e) {
                epub.addDropout(Dropout.READ_ERROR);
            } catch (IllegalArgumentException e) {
                epub.addDropout(Dropout.KEPBUB);
            }

        }
    }

    private boolean doNotProcess(final String group) {
        return group.contains("id=");
    }

}
