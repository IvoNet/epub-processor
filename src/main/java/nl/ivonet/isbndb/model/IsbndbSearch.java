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

package nl.ivonet.isbndb.model;

import java.util.List;

/**
 * @author Ivo Woltring
 */
public class IsbndbSearch {
    private String indexSearched;
    private int pageCount;
    private int resultCount;
    private int currentPage;
    private Keystats keystats;
    private List<IsbndbAuthor> data;

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("IsbndbSearch{");
        sb.append("indexSearched='")
          .append(indexSearched)
          .append('\'');
        sb.append(", pageCount='")
          .append(pageCount)
          .append('\'');
        sb.append(", resultCount='")
          .append(resultCount)
          .append('\'');
        sb.append(", currentPage='")
          .append(currentPage)
          .append('\'');
        sb.append('}');
        return sb.toString();
    }
}
