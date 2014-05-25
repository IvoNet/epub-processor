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

/**
 * This class checks finds all supported annotations in a given Class.
 */
class AnnotationChecker {
    private static final String EPUB_STRATEGY_NAME = EpubStrategy.class.getName();
    private final Class<?> aClass;

    public AnnotationChecker(final Class<?> aClass) {
        assert aClass != null : "the provided class may not be null!";
        this.aClass = aClass;
    }

    public boolean isEpubStrategy() {
        if (this.aClass
                .isAnnotationPresent(ConcreteEpubStrategy.class)) {
            final Class<?>[] interfaces = aClass.getInterfaces();
            if (interfaces == null) {
                return false;
            }
            for (final Class<?> anInterface : interfaces) {
                if (epubStrategy(anInterface)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean epubStrategy(final Class<?> anInterface) {
        return EPUB_STRATEGY_NAME.equals(anInterface.getName());
    }
}
