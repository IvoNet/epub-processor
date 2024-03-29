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

package foo.strategy.epub;

import nl.ivonet.epub.annotation.ConcreteEpubStrategy;
import nl.ivonet.epub.domain.Epub;
import nl.ivonet.epub.strategy.epub.EpubStrategy;

/**
 *
 * @author Ivo Woltring
 */
@SuppressWarnings("UnusedDeclaration")
@ConcreteEpubStrategy(order = 0)
public class IAmAStrategy implements EpubStrategy {
    @Override
    public void execute(final Epub epub) {
    }
}
