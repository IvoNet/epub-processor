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

package nl.ivonet.epub.processor.epub;


import nl.ivonet.epub.domain.Epub;
import nl.ivonet.epub.processor.Queue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import static java.nio.file.FileVisitResult.CONTINUE;

/**
 * Produces epub files by walking over a directory structure and
 * putting them on a {@link nl.ivonet.epub.processor.Queue}.
 */
public class EpubProducer implements Runnable {
    private static final Logger LOG = LoggerFactory.getLogger(EpubProducer.class);
    private final Queue<Epub> queue;
    private final Path startingDir;
    private final EpubFileFinder epubFileFinder;


    public EpubProducer(final String loc, final Queue<Epub> queue) {
        this.queue = queue;
        this.startingDir = Paths.get(loc);
        epubFileFinder = new EpubFileFinder();
    }

    @Override
    public void run() {
        try {
//            sleep(100);
            Files.walkFileTree(startingDir, epubFileFinder);
            epubFileFinder.done();
            stopQueueSign();
        } catch (final IOException e) {
            LOG.error(e.getMessage());

        }
    }

    private void stopQueueSign() {
        queue.put(Epub.getEofInstance());
    }

    class EpubFileFinder extends SimpleFileVisitor<Path> {
        private static final String EPUB = "glob:*.epub";
        private final PathMatcher matcher;

        EpubFileFinder() {
            this.matcher = FileSystems.getDefault()
                                      .getPathMatcher(EPUB);
        }

        void find(final Path file) {
            final Path name = file.getFileName();
            if (notEmptyAndMatches(name)) {
                LOG.info("Queueing [{}] ", name);
                queue.put(new Epub(file));
            }
        }

        private boolean notEmptyAndMatches(final Path name) {
            return (name != null) && this.matcher
                    .matches(name);
        }

        void done() {
            LOG.info("Finished queueing.");
        }

        @Override
        public FileVisitResult visitFile(final Path file, final BasicFileAttributes attributes) {
            find(file);
            return CONTINUE;
        }
    }
}
