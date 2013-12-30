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

import nl.ivonet.epub.domain.Epub;
import nl.ivonet.epub.strategy.epub.EpubStrategy;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author Ivo Woltring
 */
public class AnnotationCheckerTest {


    @Test
    public void testIsEpubStrategy() throws Exception {
        final AnnotationChecker checker = new AnnotationChecker(ARealStrategy.class);
        assertTrue(checker.isEpubStrategy());

    }

    @Test
    public void testIsEpubStrategyNotReal() throws Exception {
        final AnnotationChecker checker = new AnnotationChecker(NotARealStratagy.class);
        assertFalse(checker.isEpubStrategy());
    }


    @SuppressWarnings("EmptyClass")
    @ConcreteEpubStrategy
    class NotARealStratagy {
        //I do not implement the EpubStrategy interface
    }

    @ConcreteEpubStrategy
    class ARealStrategy implements EpubStrategy {
        @Override
        public void execute(final Epub epub) {

        }
    }


}
