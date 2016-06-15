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

/**
 * This strategy will remove all iTunes resources.
 *
 * As iTunes files have no special meaning in an epub file they can be counted as garbage and this strategy will treat
 * them as such.
 *
 * @author Ivo Woltring
 */
//@ConcreteEpubStrategy
public class iTunesRemovalStrategy implements EpubStrategy {
    private static final Logger LOG = LoggerFactory.getLogger(iTunesRemovalStrategy.class);

    @Override
    public void execute(final Epub epub) {
        LOG.debug("Applying {} on [{}]", getClass().getSimpleName(), epub.getOrigionalFilename());

        //At this time nothing is needed because iTunes files will removed automatically by converting from epub to
        // epub3

        // TODO: 22-03-2016 Remove all iTunes files in this strategy.
//        epub.getContents().forEach(p -> System.out.println(p.getHref()));


    }
}
