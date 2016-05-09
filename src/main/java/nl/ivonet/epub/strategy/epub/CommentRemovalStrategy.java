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
 * HTML comments are not shown in e-books and are therefore not needed. Also sometimes digital signing is done in
 * commnents.
 *
 * @author Ivo Woltring
 */
@ConcreteEpubStrategy
public class CommentRemovalStrategy implements EpubStrategy {
    private static final Logger LOG = LoggerFactory.getLogger(FileSizeStrategy.class);
    private static final Pattern PAT = Pattern.compile("<!--(.*?)-->", Pattern.CASE_INSENSITIVE);


    @Override
    public void execute(final Epub epub) {
        LOG.debug("Applying {} on [{}]", getClass().getSimpleName(), epub.getOrigionalFilename());

        final List<Resource> htmlResources = epub.getContents()
                                                 .stream()
                                                 .filter(HtmlCorruptDetectionStrategy::isHtml)
                                                 .collect(Collectors.toList());

        for (final Resource content : htmlResources) {
            try {
                final String html = IOUtils.toString(content.getReader());
                final Matcher matcher = PAT.matcher(html);
                final StringBuffer sb = new StringBuffer();
                while (matcher.find()) {
                    final String group = matcher.group();
                    System.out.println("group = " + group);
                    matcher.appendReplacement(sb, "");
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
}
