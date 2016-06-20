/*
 * Copyright (c) 2016 Ivo Woltring
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

package nl.ivonet.isbndb.data;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * @author Ivo Woltring
 */
public class AccessKeyResourceTest {

    private AccessKeyResource accessKeyResource;

    @Before
    public void setUp() throws Exception {
        accessKeyResource = new AccessKeyResource();
    }

    @Test
    public void hasMore() throws Exception {
        assertTrue(accessKeyResource.hasMore());
    }

    @Test
    public void next() throws Exception {
        assertThat(accessKeyResource.next(), is("FOOKEY"));
        assertThat(accessKeyResource.next(), is("BARKEY"));
        assertThat(accessKeyResource.next(), is("BAZKEY"));
    }

    @Test(expected = NoMoreException.class)
    public void error() throws Exception {
        accessKeyResource.next();
        accessKeyResource.next();
        accessKeyResource.next();
        accessKeyResource.next();
    }


}