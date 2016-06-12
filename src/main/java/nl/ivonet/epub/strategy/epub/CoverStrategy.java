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
import nl.ivonet.epub.metadata.BigBookSearch;
import nl.ivonet.epub.metadata.MetadataFactory;
import nl.siegmann.epublib.domain.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This strategy will try to find a cover if the {@link NoCoverDetectionStrategy} dropped out with a no cover found.
 *
 * The ordering of this strategy needs to be higher than the {@link NoCoverDetectionStrategy} for it to have the
 * greatest chance to work.
 *
 * @author Ivo Woltring
 */
@ConcreteEpubStrategy(order = 100)
public class CoverStrategy implements EpubStrategy {
    private static final Logger LOG = LoggerFactory.getLogger(CoverStrategy.class);
    private final MetadataFactory metadataFactory;

    public CoverStrategy() {
        metadataFactory = new MetadataFactory();
    }

    @Override
    public void execute(final Epub epub) {
        LOG.debug("Applying {} on [{}]", getClass().getSimpleName(), epub.getOrigionalFilename());
        if (!epub.isUniqueDropout(Dropout.COVER)) {
            LOG.debug("No cover needs to be retrieved, because it seems to have one");
            return;
        }

        final BigBookSearch bigBookSearch = metadataFactory.getBigBookSearchInstance();
        final String firstAuthor = epub.getFirstAuthor();
        final String firstTitle = epub.getFirstTitle();
        LOG.debug("Searching cover for [{}] with title [{}].", firstAuthor, firstTitle);
        final Resource byAutorAndTitle = bigBookSearch.findByAutorAndTitle(firstAuthor, firstTitle);
        if (byAutorAndTitle == null) {
            LOG.debug("No cover for [{}] with title [{}].", firstAuthor, firstTitle);
            return;
        }
        LOG.debug("Found cover for [{}] with title [{}].", firstAuthor, firstTitle);
        epub.setCoverImage(byAutorAndTitle);
        epub.removeDropout(Dropout.COVER); //if cover found

    }
}
