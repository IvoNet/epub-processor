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
import nl.siegmann.epublib.domain.Resource;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

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

        epub.getStyles()
            .stream()
            .forEach(resource -> {
//                writeStyles(epub.getOrigionalFilename(), resource); // TODO: 20-03-2016 Temporary!
                epub.remove(resource.getHref());
            });

        epub.getContents()
            .stream()
            .forEach(resource -> {
                try {
                    final String html = IOUtils.toString(resource.getReader());
                    String ret = html.replaceAll(" class=\"[A-Za-z0-9_-]+(\\s+[A-Za-z0-9_-]+)*\"", "");
//                    ret = html.replaceAll("<link href=\"../stylesheet.css\" rel=\"stylesheet\" type=\"text/css\"/>",
//                                          "");
                    resource.setData(ret.getBytes());
                } catch (IOException e) {
                    //ignore
                }
            });

    }

    // TODO: 20-03-2016 Temp code for analysis
    private void writeStyles(final String origionalFilename, final Resource resource) {
        final String finalname = origionalFilename + "-" + resource.getId() + ".css";
        try {
            final String css = IOUtils.toString(resource.getReader());

            final String folder = "/Users/ivonet/dev/ebook/output/css/";
            final File file = new File(folder);
            if (!file.exists()) {
                //noinspection ResultOfMethodCallIgnored
                file.mkdirs();
            }
            Files.write(Paths.get(folder, finalname), css.getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}


