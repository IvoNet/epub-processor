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
import nl.siegmann.epublib.domain.MediaType;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.service.MediatypeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * This strategy will remove all current style formatting and add the IvoNet style formatting.
 *
 * @author Ivo Woltring
 */
//@ConcreteEpubStrategy //FIXME this Strategy is disabled at this time
public class StyleStrategy implements EpubStrategy {
    private static final Logger LOG = LoggerFactory.getLogger(StyleStrategy.class);

    @Override
    public void execute(final Epub epub) {
        LOG.debug("Applying {} on [{}]", getClass().getSimpleName(), epub.getOrigionalFilename());

        final List<Resource> resources = epub.data()
                                             .getResources()
                                             .getResourcesByMediaType(MediatypeService.CSS);

        for (final Resource resource : resources) {
//            System.out.println("resource.getMediaType().toString() = " + resource.getMediaType()
//                                                                                 .toString());
            //This will keep the file but empty it of styles, so structure is kept.
            resource.setData("".getBytes());

        }
    }

    private boolean isNotHtml(final Resource content) {
        final MediaType mediaType = content.getMediaType();
        return (mediaType != null) && !"application/xhtml+xml".equals(mediaType.toString());
    }

}


