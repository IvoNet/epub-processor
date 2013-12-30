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

import nl.ivonet.epub.annotation.EpubStrategyDependencyFinder;
import nl.ivonet.epub.domain.Epub;
import nl.ivonet.epub.processor.Queue;
import nl.ivonet.epub.strategy.epub.EpubStrategy;
import nl.siegmann.epublib.epub.EpubWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.List;
import java.util.UUID;

/**
 * Consumes Files from the provided {@link nl.ivonet.epub.processor.Queue} and
 * creates a {@link nl.siegmann.epublib.domain.Book} from it.
 *
 * This consumer is also the Context to the strategies applied to the Epub Files.
 * This consumer can be provided with strategies to be applied to the files to be consumed.
 *
 * @author Ivo Woltring
 */
public class EpubConsumer implements Runnable {
    private static final Logger LOG = LoggerFactory.getLogger(EpubConsumer.class);
    private static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private final String id;
    private final Queue<Epub> queue;
    private final List<EpubStrategy> epubStrategies;
    private final String outputLocation;


    public EpubConsumer(final Queue<Epub> queue, final String output) {
        outputLocation = endslash(output);
        this.id = UUID.randomUUID()
                      .toString();
        this.queue = queue;
        this.epubStrategies = EpubStrategyDependencyFinder.getInstance("nl.ivonet.epub.strategy.epub")
                                                          .load();
    }

    private String endslash(final String output) {
        return output.endsWith("/") ? output : String.format("%s/", output);
    }

    @Override
    public void run() {
        Epub epub = retrieveEpubFromQueue();
        while (epub.notEof()) {
            LOG.trace("Consumer {} is consuming {}.", id, epub.getOrigionalFilename());

            for (final EpubStrategy epubStrategy : epubStrategies) {
                epubStrategy.execute(epub);
            }
//            write(epub); //FIXME temprarily disabled for testing
            print(epub);
            epub = retrieveEpubFromQueue();
        }

        stopQueues();

        LOG.debug("Consumer {} finished its job; terminating.", id);
    }

    private void print(final Epub epub) {
        LOG.info(epub.toString());
    }

    private void write(final Epub epub) {
        if (epub.isDropout()) {
            LOG.error("Dropout ebook [{}] with reason [{}]", epub.getOrigionalFilename(), epub.dropoutReasons());
            //TODO move the dropped out book to a separate folder for testing
            copyErrorFile(epub.getOrigionalPath(), epub.getOrigionalFilename(), epub.dropoutFolder());
            return;
        }
        final String folder = makeDirectoryInOutputLocation(epub);
        final String filename = epub.createFilename();

        LOG.info("Writing [{}{}]", folder, filename);

        try (FileOutputStream out = new FileOutputStream(folder + filename)) {
            new EpubWriter().write(epub.data(), out);
        } catch (final IOException e) {
            logError(e);
        }
    }

    private String makeDirectoryInOutputLocation(final Epub epub) {
        final String foldername = epub.createFoldername();
        final String firstLetter = foldername.substring(0, 1)
                                             .toUpperCase();
        final String language = endslash(epub.getLanguage()
                                             .toUpperCase());
        final String alphabet = ALPHABET.contains(firstLetter) ? (endslash(firstLetter)) : endslash("[]");

        final String directories = language + alphabet + foldername;

        final File directory = new File(outputLocation, directories);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        return endslash(directory.getAbsolutePath());
    }

    //TODO move responsibility for copying the epub to Epub
    private void copyErrorFile(final String sourceFile, final String destFile, final String dropoutFolder) {
        final File outputLoc = new File(outputLocation + "[ERROR]/" + dropoutFolder);
        if (!outputLoc.exists()) {
            outputLoc.mkdirs();
        }
        copyFile(sourceFile, endslash(outputLoc.getAbsolutePath()) + destFile);
    }

    private void copyFile(final String sourceFile, final String destFile) {
        copyFile(new File(sourceFile), new File(destFile));
    }

    private void copyFile(final File sourceFile, final File destFile) {
        if (!destFile.exists()) {
            try {
                destFile.createNewFile();
            } catch (final IOException e) {
                logError(e);
                return;
            }
        }

        try (FileChannel source = new FileInputStream(sourceFile).getChannel();
             FileChannel destination = new FileOutputStream(destFile).getChannel()) {
            destination.transferFrom(source, 0, source.size());
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }


    private void stopQueues() {
        LOG.info("Stopping queues");
        queue.put(Epub.getEofInstance());
    }

    private void logError(final Exception e) {
        LOG.error("[ERROR] occurred in class {} with message {}.", getClass(), e.getMessage());
    }

    private Epub retrieveEpubFromQueue() {
        try {
            return queue.get();
        } catch (final InterruptedException e) {
            logError(e);
        }
        return Epub.getEofInstance();
    }
}
