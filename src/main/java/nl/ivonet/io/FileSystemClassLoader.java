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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FileSystemClassLoader {

    private final List<File> directories;

    private final String packageName;

    private final List<Class<?>> classes;

    public FileSystemClassLoader(final List<File> directories, final String packageName) throws ClassNotFoundException {
        this.directories = new ArrayList<>(directories);
        this.packageName = packageName;
        this.classes = findAllClasses();
    }

    private List<Class<?>> findAllClasses() throws ClassNotFoundException {

        final ArrayList<Class<?>> classes = new ArrayList<>();
        for (final File directory : this.directories) {
            classes.addAll(findClasses(directory, this.packageName));
        }
        return classes;
    }

    private List<Class<?>> findClasses(final File directory, final String packageName) throws ClassNotFoundException {
        final List<Class<?>> classes = new ArrayList<>();

        if (directory.exists()) {
            final File[] files = directory.listFiles();
            assert files != null;
            for (final File file : files) {
                final String fileName = file.getName();
                if (file.isDirectory()) {
                    classes.addAll(findClasses(file, getClassName(packageName, fileName)));
                } else if (isValidClassName(fileName)) {
                    classes.add(createClass(packageName, fileName));
                }
            }
        }

        return classes;
    }

    private String getClassName(final String packageName, final String fileName) {

        return packageName.isEmpty() ? fileName : (packageName + "." + fileName);
    }

    private boolean isValidClassName(final String fileName) {
        return fileName.endsWith(".class") && !fileName.contains("$");
    }

    private Class<?> createClass(final String packageName, final String fileName) throws ClassNotFoundException {

        final String className = getClassName(packageName, fileName.substring(0, fileName.length() - 6));
        return Class.forName(className);
    }

    public List<Class<?>> getClasses() {
        return Collections.unmodifiableList(this.classes);
    }

    public String getPackageName() {
        return this.packageName;
    }

    public static FileSystemClassLoader getInstance(final List<File> directories, final String packageName)
            throws ClassNotFoundException {

        return new FileSystemClassLoader(directories, packageName);
    }

    public static FileSystemClassLoader getInstance(final File directory, final String packageName)
            throws ClassNotFoundException {

        final List<File> list = new ArrayList<>();
        list.add(directory);
        return getInstance(list, packageName);
    }

    public static FileSystemClassLoader getInstance(final String packageName)
            throws IOException, ClassNotFoundException {
        return getInstance(FileLoader.getInstance(packageName)
                                     .getFileList(), packageName);
    }

}
