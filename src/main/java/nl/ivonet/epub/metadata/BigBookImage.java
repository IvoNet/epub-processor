/*
 * Copyright (c) 2020 Ivo Woltring
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

package nl.ivonet.epub.metadata;

/**
 * {
 * "results": [
 * {
 * "link": "https://www.amazon.com/exec/obidos/ASIN/B004LRPJ72/_0-20",
 * "title": "Magic Slays (Kate Daniels Book 5)",
 * "image": "https://m.media-amazon.com/images/I/51RUImTGIeL.jpg",
 * "width": 309,
 * "height": 500
 * },
 * {
 * "link": "https://www.amazon.com/exec/obidos/ASIN/B009RYKYM4/_0-20",
 * "title": "Magic Rises: A Kate Daniels Novel (Kate Daniels Book Book 6)",
 * "image": "https://m.media-amazon.com/images/I/515UX7obj+L.jpg",
 * "width": 310,
 * "height": 500
 * },
 * {
 * "link": "https://www.amazon.com/exec/obidos/ASIN/B017YFA9VS/_0-20",
 * "title": "Books 1-7 of Ilona Andrews Kate Daniels Magic Series (Set Includes: Magic Bites, Magic Burns, Magic Strikes, Magic Bleeds, Magic Slays, Magic Rises and Magic Breaks)",
 * "image": "https://m.media-amazon.com/images/I/51L71r+b12L.jpg",
 * "width": 500,
 * "height": 375
 * },
 * {
 * "link": "https://www.amazon.com/exec/obidos/ASIN/B01L2JRUO6/_0-20",
 * "title": "Stadt der Finsternis - Ruf der Toten (Kate-Daniels-Reihe 5) (German Edition)",
 * "image": "https://m.media-amazon.com/images/I/518rqM9b3tL.jpg",
 * "width": 314,
 * "height": 500
 * }
 * ]
 * }
 */
public class BigBookImage {
    private String link;
    private String title;
    private String image;
    private Integer width;
    private Integer height;

    public BigBookImage(final String link,
                        final String title,
                        final String image,
                        final Integer width,
                        final Integer height) {
        this.link = link;
        this.title = title;
        this.image = image;
        this.width = width;
        this.height = height;
    }

    public BigBookImage() {
    }

    public String getLink() {
        return link;
    }

    public void setLink(final String link) {
        this.link = link;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(final String image) {
        this.image = image;
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(final Integer width) {
        this.width = width;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(final Integer height) {
        this.height = height;
    }

    @Override
    public String toString() {
        return "BigBookImage{" +
              "link='" + link + '\'' +
              ", title='" + title + '\'' +
              ", image='" + image + '\'' +
              ", width=" + width +
              ", height=" + height +
              '}';
    }
}
