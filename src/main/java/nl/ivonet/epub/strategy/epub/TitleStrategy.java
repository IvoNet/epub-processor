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
import nl.ivonet.epub.strategy.text.CapitalizeStrategy;
import nl.ivonet.epub.strategy.text.TextStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

/**
 * This Strategy works on the Title(s) of the {@link nl.ivonet.epub.domain.Epub}.
 * - Cleans up whitespace
 * - Capitalizes words in the title
 *
 * @author Ivo Woltring
 */
@ConcreteEpubStrategy
public class TitleStrategy implements EpubStrategy {
    private static final Logger LOG = LoggerFactory.getLogger(TitleStrategy.class);

    private final TextStrategy capitalizeStrategy;

    public TitleStrategy() {
        capitalizeStrategy = new CapitalizeStrategy();
    }

    @Override
    public void execute(final Epub epub) {
        LOG.debug("Applying {} on [{}]", getClass().getSimpleName(), epub.getOrigionalFilename());

        final List<String> titles = epub.getTitles()
                                        .stream()
                                        .map(p -> p.replace(" / ", " - ")
                                                   .replace("/ ", " - ")
                                                   .replace("/", " - ")
                                                   .replace(":", "-"))
                                        .map(capitalizeStrategy::execute)
                                        .map(String::trim)
                                        .collect(Collectors.toList());

        if (titles.isEmpty()) {
            epub.addDropout(Dropout.TITLE);
        }
        epub.setTitles(titles);
    }


}
