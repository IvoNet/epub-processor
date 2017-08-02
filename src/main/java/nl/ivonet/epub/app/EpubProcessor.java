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

package nl.ivonet.epub.app;

import nl.ivonet.epub.domain.Epub;
import nl.ivonet.epub.processor.Queue;
import nl.ivonet.epub.processor.epub.EpubConsumer;
import nl.ivonet.epub.processor.epub.EpubProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * @author Ivo Woltring
 */
public class EpubProcessor {
    private static final Logger LOG = LoggerFactory.getLogger(EpubProcessor.class);
    private final EpubProducer producer;
    private final Queue<Epub> epubQueue;
    private final String outputLocation;

    public EpubProcessor(final String input, final String output) {

        if (!isDirectory(input) || !createDir(output)) {
            help();
        }
        outputLocation = endslash(output);
//        ElasticsearchFactory.getInstance()
//                            .elasticsearchServer(outputLocation + "elasticsearch", "epubs");
        epubQueue = new Queue<>();
        producer = new EpubProducer(input, epubQueue);
    }

    private static void help() {
        LOG.error("Syntaxis: java -jar ebook-processor <directory> <output>");
        LOG.error("directory - The place where your ebooks reside");
        LOG.error("output    - The place where your processed ebooks will go to");
        LOG.error("Both directories must exist at this time.");
        System.exit(1);
    }

    public EpubConsumer createEpubConsumer() {
        return new EpubConsumer(epubQueue, outputLocation);
    }

    private String endslash(final String output) {
        return output.endsWith("/") ? output : String.format("%s/", output);
    }

    private boolean createDir(final String directoryName) {
        final File theDir = new File(directoryName);

        return theDir.exists() || theDir.mkdir();
    }

    private boolean isDirectory(final String folder) {
        return Files.isDirectory(Paths.get(folder));
    }

    private void work() {
        final long startTime = System.nanoTime();

        final ExecutorService threadPool = Executors.newFixedThreadPool(9);
        threadPool.execute(createEpubConsumer());
        threadPool.execute(createEpubConsumer());
        threadPool.execute(createEpubConsumer());
        threadPool.execute(createEpubConsumer());
        threadPool.execute(createEpubConsumer());
        threadPool.execute(createEpubConsumer());
        threadPool.execute(createEpubConsumer());
        threadPool.execute(createEpubConsumer());
        final Future producerStatus = threadPool.submit(producer);

        try {
            producerStatus.get();
        } catch (InterruptedException | ExecutionException e) {
            LOG.error("ProcessStatus: ", e.getMessage());
            epubQueue.put(Epub.getEofInstance());
        }

        threadPool.shutdown();
//        ElasticsearchFactory.getInstance()
//                            .shutdown();

        LOG.info("Processing took {} ms.", ((System.nanoTime() - startTime) / 1000));
    }

    public static void main(final String[] args) throws IOException {
        if (args.length <= 1) {
            help();
        }
        final EpubProcessor epubProcessor = new EpubProcessor(args[0], args[1]);
        epubProcessor.work();
    }
}
