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

import nl.ivonet.epub.domain.Epub;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// TODO: 21-08-2016 Write this Strategy

/**
 * This strategy element "img" required attribute "alt"'.
 * Fix if not exists...
 *
 * @author Ivo Woltring
 */
//@ConcreteEpubStrategy
public class ImCorrectStrategy implements EpubStrategy {
    private static final Logger LOG = LoggerFactory.getLogger(ImCorrectStrategy.class);

    @Override
    public void execute(final Epub epub) {
        LOG.debug("Applying {} on [{}]", getClass().getSimpleName(), epub.getOrigionalFilename());


    }
}
