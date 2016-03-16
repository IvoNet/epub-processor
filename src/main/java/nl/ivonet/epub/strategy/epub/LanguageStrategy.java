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

import com.cybozu.labs.langdetect.Detector;
import com.cybozu.labs.langdetect.DetectorFactory;
import nl.ivonet.epub.annotation.ConcreteEpubStrategy;
import nl.ivonet.epub.domain.Dropout;
import nl.ivonet.epub.domain.Epub;
import nl.siegmann.epublib.domain.MediaType;
import nl.siegmann.epublib.domain.Resource;
import org.apache.commons.io.IOUtils;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

    /*
    In order to create this strategy I used a lot of resources and some of them are listed below:
     http://en.wikipedia.org/wiki/List_of_ISO_639-1_codes
     http://www.loc.gov/standards/iso639-2/php/langcodes-search.php
     http://www.loc.gov/standards/iso639-2/ascii_8bits.html
     Language can also be determined by analyzing the contents of the description on certain common words
     The language strategy must be based on the language of the book and that seems to go wrong a lot of the time.

     http://opus.lingfil.uu.se/Europarl3.php
     http://www.statmt.org/europarl/
     http://dumps.wikimedia.org/backup-index.html
     http://en.wikipedia.org/wiki/Language_identification
     http://www.cavar.me/damir/LID/
    */

/**
 * Strategy for cleaning and converting the Language of an {@link nl.ivonet.epub.domain.Epub}.
 *
 * This strategy looks at the actual content of the book to analyze and detect the language. It should give have a very
 * high rate of precision.
 *
 * When will it probably fail? Wel that would be if the whole book is made up of pictures. The strategy can not
 * differentiate between pictures and text.
 *
 * @author Ivo Woltring
 */
@ConcreteEpubStrategy
public class LanguageStrategy implements EpubStrategy {
    private static final Logger LOG = LoggerFactory.getLogger(LanguageStrategy.class);

    private static final Map<String, String> languages = new HashMap<>();
    private static final String UNKNOWN_LANG = "unknown";
    private final List<String> isoLanguages = Arrays.asList(Locale.getISOLanguages());
    private final Detector detector;


    public LanguageStrategy() {
        detector = DetectorFactory.create();

    }

    @Override
    public void execute(final Epub epub) {
        LOG.debug("Applying {} on [{}]", getClass().getSimpleName(), epub.getOrigionalFilename());

        //TODO Determine comics and if so then only metadata language or UNKNOWN
        final List<Resource> contents = epub.getContents()
                                            .stream()
                                            .filter(this::isHtml)
                                            .collect(Collectors.toList());
        final String contentText = getContentText(contents);
        if (contentText.isEmpty()) {
            if (!contents.isEmpty() && isImage(contents.get(0)
                                                       .getMediaType())) {
                if (!epub.getLanguage()
                         .isEmpty()) {
                    epub.setLanguage(getLanguage(epub.getLanguage()));
                }
            } else {
                epub.addDropout(Dropout.LANGUAGE);
            }
        } else {
            epub.setLanguage(detector.detect(contentText));
        }
    }

    private String getLanguage(final String language) {
        final String lowerLang = language.toLowerCase(Locale.US)
                                         .trim();
        if (lowerLang.isEmpty()) {
            return UNKNOWN_LANG;
        }
        final String converted = languages.get(lowerLang);
        if (converted == null) {
            if (isoLanguages.contains(lowerLang)) {
                LOG.debug("Language found in the iso list {}", language);
                return lowerLang;
            }
            return UNKNOWN_LANG;
        }
        return converted;
    }

    private String getContentText(final List<Resource> contents) {

        final StringBuilder sb = new StringBuilder();
        for (final Resource content : contents) {
            if (isImage(content.getMediaType())) {
                continue;
            }
            final String text = extractText(content);
            sb.append(text);
        }
        final String ret = sb.toString();
        if (ret.isEmpty()) {
            return UNKNOWN_LANG;
        }
        return middle(ret, 10000);
    }

    private boolean isImage(final MediaType mediaType) {
        return mediaType.getName()
                        .startsWith("image/");
    }

    private String middle(final String in, final int howmuch) {
        if (in.length() >= howmuch) {
            final int start = (in.length() / 2) - (howmuch / 2);
            return in.substring(start, start + howmuch);
        }
        return in;
    }

    private String extractText(final Resource input) {
        try {
            final String html = IOUtils.toString(input.getReader());
            return Jsoup.parse(html)
                        .text();

        } catch (final IOException e) {
            return "";
        }
    }

    private boolean isHtml(final Resource content) {
        final MediaType mediaType = content.getMediaType();
        return (mediaType != null) && "application/xhtml+xml".equals(mediaType.toString());
    }

    static {
        languages.put("en-us", "en");
        languages.put("en-gb", "en");
        languages.put("en_gb", "en");
        languages.put("en-uk", "en");
        languages.put("en_uk", "en");
        languages.put("en_us", "en");
        languages.put("en-ca", "en");
        languages.put("english", "en");
        languages.put("engels", "en");
        languages.put("eng", "en");
        languages.put("en-au", "en");
        languages.put("us english (en-us)", "en");
        languages.put("us", "en");

        languages.put("nl", "nl");
        languages.put("nld", "nl");
        languages.put("nederlands", "nl");
        languages.put("nl-nl", "nl");
        languages.put("du", "nl");
        languages.put("dut", "nl");
        languages.put("dutch", "nl");
        languages.put("ned", "nl");
    }

}
