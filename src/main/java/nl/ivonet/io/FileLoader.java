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
package nl.ivonet.io;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class that loads File objects.
 *
 * It can load object from file system (*.class) or from jar files (*.jar).
 */
public class FileLoader {

    private static final String EXCLAMATION_MARK = "!";
    private static final String FULL_COLLON = ":";
    private static final String UTF_8 = "UTF-8";
    private final String packageName;
    private final List<File> files;
    private final Pattern fileNamePattern;

    public FileLoader(final String packageName) throws IOException {
        this.packageName = packageName;
        this.fileNamePattern = Pattern.compile("file:.*!.*", Pattern.CASE_INSENSITIVE);
        this.files = getFiles();
    }

    public static FileLoader getInstance(final String packageName) throws IOException {
        return new FileLoader(convertPackageToPath(packageName));
    }

    private static String convertPackageToPath(final String packageName) {
        return packageName.replace('.', '/');
    }

    public List<File> getFileList() {
        return Collections.unmodifiableList(this.files);
    }

    private List<File> getFiles() throws IOException {
        final List<File> files = new ArrayList<>();
        final ClassLoader classLoader = getClassLoader();
        final Enumeration<URL> resources = classLoader.getResources(this.packageName);
        while (resources.hasMoreElements()) {
            final URL resource = resources.nextElement();
            final File file = getNextFile(resource);
            files.add(file);
        }
        return files;
    }

    private File getNextFile(final URL resource) throws UnsupportedEncodingException {
        String fileNameDecoded = URLDecoder.decode(resource.getFile(), UTF_8);

        final Matcher m = this.fileNamePattern
                .matcher(fileNameDecoded);
        if (m.matches()) {
            fileNameDecoded = extractFilenameFromJarNotation(fileNameDecoded);
        }
        return new File(fileNameDecoded);
    }

    private String extractFilenameFromJarNotation(final String fileNameDecoded) {
        return fileNameDecoded.substring(fileNameDecoded.indexOf(FULL_COLLON) + 1, fileNameDecoded.indexOf(
                EXCLAMATION_MARK));
    }

    private ClassLoader getClassLoader() {
        final ClassLoader classLoader = Thread.currentThread()
                                              .getContextClassLoader();
        assert classLoader != null;
        return classLoader;
    }

}
