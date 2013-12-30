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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Loads classes from a JAR.
 */
public class JarLoader {
    private static final Logger LOG = LoggerFactory.getLogger(JarLoader.class);

    private final List<File> directories;

    private final String packageName;

    private final List<Class<?>> classes;

    public JarLoader(final List<File> directories, final String packageName)
            throws ClassNotFoundException, IOException {
        this.directories = new ArrayList<>(directories);
        this.packageName = packageName;
        this.classes = findAllClasses();
    }

    public static JarLoader getInstance(final List<File> directories, final String packageName)
            throws ClassNotFoundException, IOException {

        return new JarLoader(directories, packageName);
    }

    public static JarLoader getInstance(final File directory, final String packageName)
            throws ClassNotFoundException, IOException {

        final List<File> list = new ArrayList<>();
        list.add(directory);
        return getInstance(list, packageName);
    }

    public List<Class<?>> findAllClasses() throws IOException, ClassNotFoundException {

        final List<Class<?>> classes = new ArrayList<>();
        for (final File directory : this.directories) {
            classes.addAll(walkJar(directory));
        }

        return classes;
    }

    private List<Class<?>> walkJar(final File directory) throws IOException, ClassNotFoundException {

        final List<Class<?>> classes = new ArrayList<>();
        final JarFile jarFile = new JarFile(directory);

        final Enumeration<JarEntry> jarEntries = jarFile.entries();

        while (jarEntries.hasMoreElements()) {
            final JarEntry jarEntry = jarEntries.nextElement();
            addClassFromJar(jarEntry, classes);
        }

        return classes;
    }

    private void addClassFromJar(final JarEntry jarEntry, final List<Class<?>> classes) {
        if (isMatchingClass(jarEntry)) {
            final String fileName = jarEntry.getName();
            if (isValidClassName(fileName)) {
                final Class<?> clazz = createClass(fileName);
                if (isNotNull(clazz)) {
                    classes.add(clazz);
                }
            }
        }
    }

    private boolean isMatchingClass(final JarEntry jarEntry) {

        boolean retVal = false;
        if (!jarEntry.isDirectory()) {
            final String name = jarEntry.getName();
            // TODO Fix this
            // Matcher matcher = pattern.matcher(name);
            retVal = true;
            // retVal = matcher.matches();
        }
        return retVal;
    }

    private boolean isValidClassName(final String fileName) {
        return fileName.endsWith(".class") && !fileName.contains("$");
    }

    private Class<?> createClass(final String fileName) {

        try {
            final String className = getClassName(fileName);
            return Class.forName(className);
        } catch (final Throwable e) {
            LOG.error(e.getMessage());
            return null;
        }
    }

    private String getClassName(final String fileName) {

        String retVal = fileName.substring(0, fileName.length() - 6);
        retVal = retVal.replaceAll("/", ".");

        return retVal;
    }

    private boolean isNotNull(final Object obj) {
        return obj != null;
    }

    public List<Class<?>> getClasses() {
        return Collections.unmodifiableList(this.classes);
    }

    public String getPackageName() {

        return this.packageName;
    }
}
