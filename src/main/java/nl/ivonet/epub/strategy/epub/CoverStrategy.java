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
import nl.ivonet.epub.domain.Name;
import nl.ivonet.epub.metadata.BigBookSearch;
import nl.ivonet.epub.metadata.MetadataFactory;
import nl.ivonet.epub.strategy.name.FirstnameSpaceSurnameStrategy;
import nl.siegmann.epublib.domain.Author;
import nl.siegmann.epublib.domain.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * This strategy will try to find a cover if the {@link NoCoverDetectionStrategy} dropped out with a no cover found.
 *
 * The ordering of this strategy needs to be higher than the {@link NoCoverDetectionStrategy} for it to have the
 * greatest chance to work.
 *
 * @author Ivo Woltring
 */
@ConcreteEpubStrategy(order = 2000)
public class CoverStrategy implements EpubStrategy {
    private static final Logger LOG = LoggerFactory.getLogger(CoverStrategy.class);
    private final MetadataFactory metadataFactory;

    public CoverStrategy() {
        metadataFactory = new MetadataFactory();
    }

    // TODO: 12-06-2016 improvement possible by searching for complete title and author at once
    @Override
    public void execute(final Epub epub) {
        LOG.debug("Applying {} on [{}]", getClass().getSimpleName(), epub.getOrigionalFilename());
        if (!epub.isUniqueDropout(Dropout.COVER)) {
            LOG.debug("No cover needs to be retrieved, because it seems to have one");
            return;
        }

        final BigBookSearch bigBookSearch = metadataFactory.getBigBookSearchInstance();
        final String firstTitle = epub.getFirstTitle();
        final List<Author> authors = epub.getAuthors();
        Resource resource = null;
        String name = "";
        for (final Author author : authors) {
            name = new Name(author.getFirstname(), author.getLastname(), new FirstnameSpaceSurnameStrategy()).name();
            LOG.debug("Searching cover for [{}] with title [{}].", name, firstTitle);

            resource = bigBookSearch.findByAutorAndThenTitle(name, firstTitle);
            if (resource != null) {
                //early return
                break;
            }
        }
        if (resource == null) {
            return;
        }
        LOG.debug("Found cover for [{}] with title [{}].", name, firstTitle);
        epub.setCoverImage(resource);
        epub.removeDropout(Dropout.COVER);

    }
}
