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

package nl.ivonet.epub.processor;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 *
 * @author Ivo Woltring
 */
public class QueueTest {

    @Test
    public void testPutWithBehaviorAsSet() throws Exception {
        final Queue<String> queue = new Queue<>(); //As Set
        queue.put("Ivo");
        queue.put("Ivo");
        queue.put("Ivo");
        queue.put("Ivo");
        queue.put("Ivo");
        assertEquals(5, queue.size());
        final String s = queue.get();
        assertEquals("Ivo", s);
        assertEquals(4, queue.size());
        queue.put("Ivo");
        assertEquals(5, queue.size());
        queue.put("Ivo Woltring");
        assertEquals(6, queue.size());
        queue.put("Ivo Woltring");
        assertEquals(7, queue.size());
        queue.put("Ivo");
        assertEquals(8, queue.size());
    }

    @Test
    public void testPut() throws Exception {
        final Queue<String> queue = new Queue<>(); //As normal Array
        queue.put("Ivo");
        queue.put("Ivo");
        queue.put("Ivo");
        queue.put("Ivo");
        queue.put("Ivo");
        assertEquals(5, queue.size());
        queue.get();
        assertEquals(4, queue.size());
        queue.put("Ivo");
        assertEquals(5, queue.size());


    }
}
