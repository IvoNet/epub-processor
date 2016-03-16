/*
 * Copyright (c) 2015 Ivo Woltring
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

// FIXME: 16-03-2016 very weak strategy at this time!

/**
 * Tries to filter out epubs that can not possibly be correct based on filesize.
 *
 * Right now not a really involved strategy. Everything smaller than a certain size is duped as Dropout.
 *
 * @author Ivo Woltring
 */
@ConcreteEpubStrategy
public class FileSizeStrategy implements EpubStrategy {
    private static final Logger LOG = LoggerFactory.getLogger(FileSizeStrategy.class);

    @Override
    public void execute(final Epub epub) {
        LOG.debug("Applying {} on [{}]", getClass().getSimpleName(), epub.getOrigionalFilename());
        final String filename = epub.getOrigionalPath();
        final long length = new File(filename).length();
        if (length < (8 * 1024)) {
            LOG.error("Filesize to small: " + epub.getOrigionalFilename());
            epub.addDropout(Dropout.FILE_SIZE);
        }
    }
}
