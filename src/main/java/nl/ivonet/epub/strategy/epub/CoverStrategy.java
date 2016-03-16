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

import nl.ivonet.epub.domain.Dropout;
import nl.ivonet.epub.domain.Epub;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Ivo Woltring
 */
//@ConcreteEpubStrategy //FIXME this Strategy is disabled at this time
public class CoverStrategy implements EpubStrategy {
    private static final Logger LOG = LoggerFactory.getLogger(CoverStrategy.class);

    @Override
    public void execute(final Epub epub) {
        LOG.debug("Applying {} on [{}]", getClass().getSimpleName(), epub.getOrigionalFilename());

        //TODO I'm just curious how many of my books lack a cover. This strategy is not finished at all!

        if (!epub.hasCover()) {
            epub.addDropout(Dropout.COVER);
        }
    }
}
