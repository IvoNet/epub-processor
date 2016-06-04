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
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.domain.Resources;
import org.apache.commons.io.IOUtils;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.codecraft.xsoup.Xsoup;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;


// TODO: 01-06-2016 if no cover is found it might be nifty to see if we can get a suitable cover based on the
// metadata of the book

/**
 * Tries to determine if a book has a cover.
 * <pre>
 * - First it tries to determine if the default cover image is available.
 * - if it is available see if it matches one of the wrong (not allowed) covers and dropout if it is wrong
 * - if not found try to find the image in the cover page and then do the same.
 * - if a correct cover image is found in the cover page it sets it as the cover image directly too.
 * </pre>
 *
 * @author Ivo Woltring
 */
@ConcreteEpubStrategy
public class NoCoverDetectionStrategy implements EpubStrategy {
    private static final Logger LOG = LoggerFactory.getLogger(NoCoverDetectionStrategy.class);

    @Override
    public void execute(final Epub epub) {
        LOG.debug("Applying {} on [{}]", getClass().getSimpleName(), epub.getOrigionalFilename());

        int idx = 1;
        URL resource;
        final byte[] coverContent = getCoverContent(epub);
        if (coverContent == null) {
            epub.addDropout(Dropout.COVER);
            return;
        }
        while ((resource = EpubStrategy.class.getResource(String.format("/wrongCover/cover_%s.jpeg", idx))) != null) {
            if (Arrays.equals(retrieveWrongCover(Paths.get(resource.getFile())), coverContent)) {
                epub.addDropout(Dropout.COVER);
                break;
            }
            idx++;
        }
    }

    private byte[] getCoverContent(final Epub epub) {
        final Resource coverImage = epub.getCoverImage();
        if (coverImage != null) {
            return getBytes(coverImage);
        }
        return getCoverPageContentImage(epub);
    }

    private byte[] getCoverPageContentImage(final Epub epub) {
        final String src = getImgTagSrcReference(epub);
        if (src == null) {
            return null;
        }
        final Resources resources = epub.getResources();
        final Resource cover = resources.getAll()
                                        .stream()
                                        .filter(resource -> src.contains(resource.getHref()))
                                        .findFirst()
                                        .orElse(null);
        if (cover != null) {
            LOG.info("Found cover in cover page [{}]", epub.getOrigionalFilename());
            epub.setCoverImage(cover);
            return getBytes(cover);
        }
        return null;
    }

    private byte[] getBytes(final Resource resource) {
        try {
            return IOUtils.toByteArray(resource.getReader());
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String getImgTagSrcReference(final Epub epub) {
        final Resource coverPage = epub.getCoverPage();
        if (coverPage == null) {
            return null;
        }
        try {
            return retrieveXpath(coverPage.getInputStream(), "//img/@src");
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String retrieveXpath(final InputStream inputStream, final String xpath) {
        try {
            return Xsoup.compile(xpath)
                        .evaluate(Jsoup.parse(IOUtils.toString(inputStream)))
                        .get();
        } catch (final IOException e) {
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
