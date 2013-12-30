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

import org.junit.Test;

import java.io.File;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author Ivo Woltring
 */
public class FileLoaderTest {
    @Test
    public void testGetFileList() throws Exception {

        final FileLoader fileLoader = FileLoader.getInstance("foo.io");
        final List<File> fileList = fileLoader.getFileList();
        assertNotNull(fileList);
        assertEquals(1, fileList.size());
        assertEquals("io", fileList.get(0)
                                   .getName());

    }
}
