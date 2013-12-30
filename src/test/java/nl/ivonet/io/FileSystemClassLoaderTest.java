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

import foo.io.Bar;
import foo.io.Foo;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 *
 * @author Ivo Woltring
 */
public class FileSystemClassLoaderTest {
    @Test
    public void testGetInstance() throws Exception {
        final FileSystemClassLoader instance = FileSystemClassLoader.getInstance("foo.io");
        final List<Class<?>> classes = instance.getClasses();
        assertEquals(3, classes.size());
        assertTrue(classes.contains(Bar.class));
        assertTrue(classes.contains(Foo.class));
        assertTrue(classes.contains(foo.io.tt.Bar.class));
        assertFalse(classes.contains(getClass()));

    }
}
