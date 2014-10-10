/*
 * Copyright (c) 2014 Ivo Woltring
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

package nl.ivonet.epub.strategy.text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.util.stream.Stream.of;

/**
 * This {@link TextStrategy} capitalizes the input.
 *
 * Words like "a, and, de, het en een" will not be capitalized.
 *
 * @author Ivo Woltring
 */
public class CapitalizeStrategy implements TextStrategy {
    private static final List<String> LOWERCASE_WORDS = new ArrayList<>(Arrays.asList("a", "an", "as", "and",
                                                                                      "although", "at", "because",
                                                                                      "but", "by", "for", "in", "nor",
                                                                                      "of", "on", "or", "so", "the",
                                                                                      "to", "up", "yet", "de", "het",
                                                                                      "een", "van", "des", "is",
                                                                                      "dit"));

    private String capitalize(final String input) {
        if ((input == null) || (input.isEmpty())) {
            return input;
        }
        final String str = input.toLowerCase();
        final int strLen = str.length();
        final StringBuilder sentence = new StringBuilder(strLen);

        StringBuilder word = new StringBuilder(strLen);
        boolean firstWord = true;
        boolean capitalizeNext = true;
        for (int i = 0; i < strLen; i++) {
            final char ch = str.charAt(i);

            if (Character.isWhitespace(ch)) {
                final String wrd = word.toString();
                if (firstWord) {
                    firstWord = false;
                    sentence.append(wrd);
                } else {
                    sentence.append(LOWERCASE_WORDS.contains(wrd.toLowerCase()) ? wrd.toLowerCase() : wrd);
                }
                sentence.append(ch);
                word = new StringBuilder();
                capitalizeNext = true;
            } else if (capitalizeNext) {
                word.append(Character.toTitleCase(ch));
                capitalizeNext = false;
            } else {
                word.append(ch);
            }
        }
        return sentence.append(word.toString())
                       .toString();
    }

    private String capitalizeNEW(final String input) {
        if ((input == null) || (input.isEmpty())) {
            return input;
        }
        of(input).map(String::toLowerCase)
                 .map(p -> p.split("\\W+"));


        final String str = input.toLowerCase();
        final int strLen = str.length();
        final StringBuilder sentence = new StringBuilder(strLen);

        StringBuilder word = new StringBuilder(strLen);
        boolean firstWord = true;
        boolean capitalizeNext = true;
        for (int i = 0; i < strLen; i++) {
            final char ch = str.charAt(i);

            if (Character.isWhitespace(ch)) {
                final String wrd = word.toString();
                if (firstWord) {
                    firstWord = false;
                    sentence.append(wrd);
                } else {
                    sentence.append(LOWERCASE_WORDS.contains(wrd.toLowerCase()) ? wrd.toLowerCase() : wrd);
                }
                sentence.append(ch);
                word = new StringBuilder();
                capitalizeNext = true;
            } else if (capitalizeNext) {
                word.append(Character.toTitleCase(ch));
                capitalizeNext = false;
            } else {
                word.append(ch);
            }
        }
        return sentence.append(word.toString())
                       .toString();
    }

    @Override
    public String execute(final String input) {
        return capitalize(input);
    }
}
