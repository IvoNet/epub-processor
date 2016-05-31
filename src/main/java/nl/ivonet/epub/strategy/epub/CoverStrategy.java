/*
 * Copyright (c) 2013 Ivo Woltring
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
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

/**
 * @author Ivo Woltring
 */
@ConcreteEpubStrategy
public class CoverStrategy implements EpubStrategy {
    private static final Logger LOG = LoggerFactory.getLogger(CoverStrategy.class);

    @Override
    public void execute(final Epub epub) {
        LOG.debug("Applying {} on [{}]", getClass().getSimpleName(), epub.getOrigionalFilename());

        int idx = 1;
        URL resource;
        while ((resource = EpubStrategy.class.getResource(String.format("/wrongCover/cover_%s.jpeg", idx))) != null) {
            if (Arrays.equals(retrieveWrongCover(Paths.get(resource.getFile())), getCoverContent(epub))) {
                epub.addDropout(Dropout.COVER);
            }
            idx++;
        }

        if ((epub.getCoverPage() == null) || (epub.getCoverImage() == null)) {
            LOG.error("Book with title {}] seems to have no cover.", epub.getOrigionalFilename());
        }

    }

    private byte[] getCoverContent(final Epub epub) {
        try {
            return IOUtils.toByteArray(epub.getCoverImage()
                                           .getReader());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private byte[] retrieveWrongCover(final Path location) {
        final byte[] noCover;
        try (final InputStreamReader in = new InputStreamReader(Files.newInputStream(location));
             final BufferedReader br = new BufferedReader(in)) {
            noCover = IOUtils.toByteArray(br);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
        return noCover;
    }
}
