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

package nl.ivonet.epub.domain;

import nl.siegmann.epublib.domain.Author;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Date;
import nl.siegmann.epublib.domain.Identifier;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.epub.EpubReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.lang.String.format;
import static java.util.stream.Collectors.joining;


/**
 * Represents an Epub book.
 *
 * @author Ivo Woltring
 */
public class Epub {
    private static final Logger LOG = LoggerFactory.getLogger(Epub.class);

    private static final String ET_ALL = "et al.";

    private final Path filePath;
    private final Book epub;
    private final Set<Dropout> dropouts;
    private final boolean eof;
    private final EpubReader epubReader;

    public Epub(final Path file) {
        this.dropouts = new HashSet<>();
        this.epubReader = new EpubReader();
        this.eof = false;
        this.filePath = file;
        this.epub = getBook(file.toFile());
    }

    public Epub(final Path path, final Book book) {
        this.dropouts = new HashSet<>();
        this.epubReader = new EpubReader();
        this.eof = false;
        this.filePath = path;
        this.epub = book;
    }

    /**
     * EOF constructor.
     */
    @SuppressWarnings("AssignmentToNull")
    private Epub() {
        eof = true;
        filePath = null;
        epub = null;
        dropouts = null;
        epubReader = null;
    }

    public static Epub getEofInstance() {
        return new Epub();
    }

    public boolean notEof() {
        return !eof;
    }

    private Book getBook(final File data) {
        try (FileInputStream in = new FileInputStream(data)) {
            return epubReader.readEpub(in);
        } catch (final Exception e) {
            dropouts.add(Dropout.READ_ERROR);
//            logError(e);
        }
        return new Book();
    }


    private void logError(final Exception e) {
        LOG.error("[ERROR] occurred in class {} with message {}.", getClass(), e.getMessage());
    }

    public Path path() {
        return filePath;
    }

    public Book data() {
        return epub;
    }

    public String getOrigionalPath() {
        return filePath.toAbsolutePath()
                       .toString();
    }

    public String getOrigionalFilename() {
        return filePath.getFileName()
                       .toString();
    }

    public void addDropout(final Dropout dropout) {
        dropouts.add(dropout);
    }

    public String dropoutFolder() {
        return dropouts.stream()
                       .filter(p -> !p.getValue()
                                      .isEmpty())
                       .sorted()
                       .map(Dropout::getValue)
                       .map(p -> p.replace(" ", "_"))
                       .collect(joining("_&_"));
    }

    public String dropoutReasons() {
        final StringBuilder builder = new StringBuilder();
        dropouts.stream()
                .forEach(p -> builder.append(p.getValue())
                                     .append(", "));
        final String ret = builder.toString();
        return ret.substring(0, ret.lastIndexOf(","));
    }

    public boolean isDropout() {
        return !dropouts.isEmpty();
    }


    public List<String> getDescriptions() {
        return epub.getMetadata()
                   .getDescriptions();
    }

//    private Map<QName, String> getOtherProperties() {
//        return epub.getMetadata().getOtherProperties();
//    }

    private List<Date> getDates() {
        return epub.getMetadata()
                   .getDates();
    }

    public List<String> getTypes() {
        return epub.getMetadata()
                   .getTypes();
    }

    public List<String> getRights() {
        return epub.getMetadata()
                   .getRights();
    }

    public List<String> getPublishers() {
        return epub.getMetadata()
                   .getPublishers();
    }

    public List<String> getSubjects() {
        return epub.getMetadata()
                   .getSubjects();
    }

    public void setSubjects(final List<String> subjects) {
        epub.getMetadata()
            .setSubjects(subjects);
    }

    public String getLanguage() {
        return epub.getMetadata()
                   .getLanguage();
    }

    public void setLanguage(final String language) {
        epub.getMetadata()
            .setLanguage(language);
    }

    public List<Identifier> getIdentifiers() {
        return epub.getMetadata()
                   .getIdentifiers();
    }

    public String getFormat() {
        return epub.getMetadata()
                   .getFormat();
    }

    public List<String> getTitles() {
        return epub.getMetadata()
                   .getTitles();
    }

    public void setTitles(final List<String> titles) {
        epub.getMetadata()
            .setTitles(titles);
    }

    public String getFirstTitle() {
        return epub.getMetadata()
                   .getFirstTitle();
    }

    public List<Author> getAuthors() {
        return epub.getMetadata()
                   .getAuthors();
    }

    public void setAuthors(final List<Author> authors) {
        epub.getMetadata()
            .setAuthors(authors);
    }

    public List<Resource> getContents() {
        return epub.getContents();
    }

    public Resource getCoverImage() {
        return epub.getCoverImage();
    }

    public boolean hasCover() {
        return epub.getCoverImage() == null;
    }

    private boolean isCorrectFilenameSize(final StringBuilder stringBuilder, final String item) {
        return (stringBuilder.length() + item.length() + getFirstTitle().length() + ET_ALL.length() + 13) <= 255;
    }

//    private String postProcessName(final String epubName) {
//        if (epubName.length() <= 255) {
//            return epubName;
//        }
//        final StringBuilder filename = new StringBuilder();
//        final List<Author> authors = getAuthors();
//        sortAuthors(authors);
//        boolean first = true;
//        for (final Author author : authors) {
//            if (first) {
//                first = false;
//            } else {
//                filename.append(" & ");
//            }
//            filename.append(author.getLastname()).append(", ").append(author.getFirstname().replace(".", "_"));
//        }
//        filename.append(" ~ ");
//        filename.append(getFirstTitle());
//        filename.append(".epub");
//
//
//        return filename.toString();
//    }

    public String createFilename() {
        final StringBuilder filename = createAuthorFilenamePart();
        filename.append(" - ");
        filename.append(getFirstTitle());
        filename.append(".epub");

        return filename.toString()
                       .replace("?", "")
                       .replace("/", "-")
                       .trim();
    }

    public String createFoldername() {
        return createAuthorFilenamePart().toString();
    }

    private StringBuilder createAuthorFilenamePart() {
        final StringBuilder filename = new StringBuilder();
        final List<Author> authors = getAuthors();
        sortAuthors(authors);
        boolean first = true;
        for (final Author author : authors) {
            if (first) {
                first = false;
            } else {
                filename.append(" & ");
            }
            final String formattedAuthor = format("%s, %s", author.getLastname(), author.getFirstname()
                                                                                        .replace(".", "_"));
            if (isCorrectFilenameSize(filename, formattedAuthor)) {
                filename.append(formattedAuthor);
            } else {
                filename.append(ET_ALL);
                break;
            }
        }
        return filename;
    }

    private void sortAuthors(final List<Author> authors) {
        authors.sort((o1, o2) -> {
            final int surnameCompare = o1.getLastname()
                                         .compareTo(o2.getLastname());
            if (surnameCompare == 0) {
                return o1.getFirstname()
                         .compareTo(o2.getFirstname());
            }
            return surnameCompare;
        });
    }

    private String reprEpub() {
        if (eof) {
            return "End of File marker.";
        }
        if (isDropout()) {
            return format("Epub [%s] dropped out with reason [%s]", getOrigionalFilename(), dropoutReasons());
        }
        //        sb.append(", Other properties=").append(getOtherProperties());
        return format("Epub{OrigionalFilename= %s, path=%s, Authors=%s, First title=%s, Titles=%s, Language=%s, "
                      + "Subjects=%s, Publishers=%s, Rights=%s, Identifiers=%s, Types=%s, Dates=%s, Format=%s, "
                      + "Descriptions=%s}", getOrigionalFilename(), filePath, getAuthors(), getFirstTitle(),
                      getTitles(), getLanguage(), getSubjects(), getPublishers(), getRights(), getIdentifiers(),
                      getTypes(), getDates(), getFormat(), getDescriptions()
        );
    }

    @Override
    public String toString() {

        try {
            return reprEpub();
        } catch (final Exception e) {
            return "Still being constructed.";
        }

    }
}
