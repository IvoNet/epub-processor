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

package nl.ivonet.epub.annotation;

import nl.ivonet.epub.strategy.epub.EpubStrategy;
import nl.ivonet.io.FileLoader;
import nl.ivonet.io.FileSystemClassLoader;
import nl.ivonet.io.JarLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Ivo Woltring
 */
public class EpubStrategyDependencyFinder {
    private static final Logger LOG = LoggerFactory.getLogger(EpubStrategyDependencyFinder.class);

    private final String packageName;

    public EpubStrategyDependencyFinder(final String packageName) {
        assert packageName != null : "packageName parameter may not be null";
        this.packageName = packageName;
    }

    public List<EpubStrategy> load() {
        try {
            final List<EpubStrategy> strategies = new ArrayList<>();
            final List<Class<?>> classes = loadClasses();
            for (final Class<?> aClass : classes) {
                final AnnotationChecker checker = new AnnotationChecker(aClass);
                if (checker.isEpubStrategy()) {
                    try {
                        strategies.add((EpubStrategy) aClass.newInstance());
                    } catch (InstantiationException | IllegalAccessException e) {
                        logError(e);
                    }
                }
            }
            Collections.sort(strategies, (o1, o2) -> {
                final Integer order1 = o1.getClass()
                                         .getDeclaredAnnotation(ConcreteEpubStrategy.class)
                                         .order();
                final Integer order2 = o2.getClass()
                                         .getDeclaredAnnotation(ConcreteEpubStrategy.class)
                                         .order();
                return order1.compareTo(order2);
            });
            return strategies;
        } catch (IOException | ClassNotFoundException e) {
            logError(e);
            return Collections.emptyList();
        }
    }

    public List<Class<?>> loadClasses() throws ClassNotFoundException, IOException {
        final List<File> directories = getDirectories();
        final List<Class<?>> classes = new ArrayList<>();

        for (final File directory : directories) {

            if (isJarFile(directory)) {
                classes.addAll(loadFromJarFile(directory));
            } else {
                classes.addAll(loadFromFileSystem(directory));
            }
        }
        return classes;
    }

    private List<File> getDirectories() throws IOException {
        return FileLoader.getInstance(this.packageName)
                         .getFileList();
    }

    private boolean isJarFile(final File directory) {
        return directory.getName()
                        .endsWith(".jar");
    }

    private List<Class<?>> loadFromJarFile(final File directory) throws ClassNotFoundException, IOException {
        return JarLoader.getInstance(directory, this.packageName)
                        .getClasses();
    }

    private List<Class<?>> loadFromFileSystem(final File directory) throws ClassNotFoundException, IOException {
        return FileSystemClassLoader.getInstance(directory, this.packageName)
                                    .getClasses();
    }

    private void logError(final Exception e) {
        LOG.error("[ERROR] occurred in class {} with message {}.", getClass(), e.getMessage());
    }

    public static EpubStrategyDependencyFinder getInstance(final String packageName) {
        return new EpubStrategyDependencyFinder(packageName);
    }

}
