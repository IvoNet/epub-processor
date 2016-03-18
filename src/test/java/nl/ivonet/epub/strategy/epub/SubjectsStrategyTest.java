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

package nl.ivonet.epub.strategy.epub;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

import static java.util.stream.Collectors.toList;

/**
 *
 * @author Ivo Woltring
 */
public class SubjectsStrategyTest {
    private static final List<String> REMOVE_WORDS = Arrays.asList("foo", "me", "jij");

    @Test
    public void testName() throws Exception {
        /*
        Deze zin moet:
        - Getrimt worden op \n
        - Gesplitst worden op -> (wordt de key van de map)
        - Gesplitst woren op ; (Wordt de List van strings in de map )
        - de values moeten toLowerCased worden
        - de values meoten getrimt worden
         */
        final String str = "Science Fiction;sf;scifi;science fiction;Science Fiction & Fantasy;SciFi-Futuristic\n";

        final List<String> strings = Arrays.asList(str.trim()
                                                      .split(";"));
//        strings.stream().map(String::trim).collect(Collectors.groupingBy(strings.get(0), () -> ));
        //.stream().forEach(System.out::println);

    }

//    @Test
//    public void testMapTo() throws Exception {
//        final Map<String, List<String>> mapTo = retrieveMapTo("/SubjectsMapTo.txt");
//
//    }

//    private Map<String, List<String>> retrieveMapTo(final String searchQuery) {
//        try (BufferedReader br = new BufferedReader(new InputStreamReader(new URL(searchQuery).openStream()))) {
//            return br.lines().parallel().map(String::trim).collect(Collectors.toMap((t) -> ));
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }

    private String resource() {
        return this.getClass()
                   .getResource("/SubjectsRemove.txt")
                   .toExternalForm();
    }

    @Test
    public void testExecute() throws Exception {

        final List<String> subjectsIn = new ArrayList<>();
        subjectsIn.add("Should be here");
        subjectsIn.add("Shout me two");
        subjectsIn.add("Removed foo");
        subjectsIn.add("Jij ook");
        subjectsIn.add("me too");
        subjectsIn.add("Should be here");
        subjectsIn.add("Shout me two");
        subjectsIn.add("Removed foo");
        subjectsIn.add("Jij ook");
        subjectsIn.add("me too");
        subjectsIn.add("Should be here");
        subjectsIn.add("Shout me two");
        subjectsIn.add("Removed foo");
        subjectsIn.add("Jij ook");
        subjectsIn.add("me too");
        subjectsIn.add("Should be here");
        subjectsIn.add("Shout me two");
        subjectsIn.add("Removed foo");
        subjectsIn.add("Jij ook");
        subjectsIn.add("me too");
        subjectsIn.add("Should be here");
        subjectsIn.add("Shout me two");
        subjectsIn.add("Removed foo");
        subjectsIn.add("Jij ook");
        subjectsIn.add("me too");
        subjectsIn.add("Should be here");
        subjectsIn.add("Shout me two");
        subjectsIn.add("Removed foo");
        subjectsIn.add("Jij ook");
        subjectsIn.add("me too");
        subjectsIn.add("Should be here");
        subjectsIn.add("Shout me two");
        subjectsIn.add("Removed foo");
        subjectsIn.add("Jij ook");
        subjectsIn.add("me too");
        subjectsIn.add("Should be here");
        subjectsIn.add("Shout me two");
        subjectsIn.add("Removed foo");
        subjectsIn.add("Jij ook");
        subjectsIn.add("me too");
        subjectsIn.add("Should be here");
        subjectsIn.add("Shout me two");
        subjectsIn.add("Removed foo");
        subjectsIn.add("Jij ook");
        subjectsIn.add("me too");
        subjectsIn.add("Should be here");
        subjectsIn.add("Shout me two");
        subjectsIn.add("Removed foo");
        subjectsIn.add("Jij ook");
        subjectsIn.add("me too");
        subjectsIn.add("Should be here");
        subjectsIn.add("Shout me two");
        subjectsIn.add("Removed foo");
        subjectsIn.add("Jij ook");
        subjectsIn.add("me too");
        subjectsIn.add("Should be here");
        subjectsIn.add("Shout me two");
        subjectsIn.add("Removed foo");
        subjectsIn.add("Jij ook");
        subjectsIn.add("me too");
        subjectsIn.add("Should be here");
        subjectsIn.add("Shout me two");
        subjectsIn.add("Removed foo");
        subjectsIn.add("Jij ook");
        subjectsIn.add("me too");
        subjectsIn.add("Should be here");
        subjectsIn.add("Shout me two");
        subjectsIn.add("Removed foo");
        subjectsIn.add("Jij ook");
        subjectsIn.add("me too");
        subjectsIn.add("Should be here");
        subjectsIn.add("Shout me two");
        subjectsIn.add("Removed foo");
        subjectsIn.add("Jij ook");
        subjectsIn.add("me too");
        subjectsIn.add("Should be here");
        subjectsIn.add("Shout me two");
        subjectsIn.add("Removed foo");
        subjectsIn.add("Jij ook");
        subjectsIn.add("me too");
        subjectsIn.add("Should be here");
        subjectsIn.add("Shout me two");
        subjectsIn.add("Removed foo");
        subjectsIn.add("Jij ook");
        subjectsIn.add("me too");
        subjectsIn.add("Should be here");
        subjectsIn.add("Shout me two");
        subjectsIn.add("Removed foo");
        subjectsIn.add("Jij ook");
        subjectsIn.add("me too");
        subjectsIn.add("Should be here");
        subjectsIn.add("Shout me two");
        subjectsIn.add("Removed foo");
        subjectsIn.add("Jij ook");
        subjectsIn.add("me too");
        subjectsIn.add("Should be here");
        subjectsIn.add("Shout me two");
        subjectsIn.add("Removed foo");
        subjectsIn.add("Jij ook");
        subjectsIn.add("me too");
        subjectsIn.add("Should be here");
        subjectsIn.add("Shout me two");
        subjectsIn.add("Removed foo");
        subjectsIn.add("Jij ook");
        subjectsIn.add("me too");
        subjectsIn.add("Should be here");
        subjectsIn.add("Shout me two");
        subjectsIn.add("Removed foo");
        subjectsIn.add("Jij ook");
        subjectsIn.add("me too");

//        subjectsIn.stream().filter(p -> {
//            for (final String removeWord : REMOVE_WORDS) {
//                if (p.toLowerCase().is(removeWord)) {
//                    return false;
//                }
//            }
//            return true;
//        }).forEach(p -> System.two.println("p1 = " + p));


        final List<String> lines = measure(() -> subjectsIn.stream()
                                                           .filter(p -> !REMOVE_WORDS.stream()
                                                                                     .filter(r -> p.toLowerCase()
                                                                                                   .contains(r))
                                                                                     .findAny()
                                                                                     .isPresent())
                                                           .collect(toList()));

        final List<String> collect = measure(() -> subjectsIn.stream()
                                                             .map(String::toLowerCase)
                                                             .filter(REMOVE_WORDS::contains)
                                                             .collect(toList()));

//        lines.stream().forEach(System.out::println);

//        final List<String> two = subjectsIn.stream()
//                                           .filter(p -> !REMOVE_WORDS.stream().filter(r -> p.toLowerCase().is
// (r))
//                                                                     .findAny().isPresent())
//                                           .collect(Collectors.toList());
//        two.stream().forEach(System.out::println);
//
//
//        final List<String> three = subjectsIn.stream().filter(p -> !REMOVE_WORDS.stream().filter(r -> p.toLowerCase()
//                                                                                                       .is(r))
//                                                                                .findAny().isPresent())
//                                             .collect(Collectors.toList());
//        three.stream().forEach(System.out::println);

//        measure(() -> subjectsIn.stream()
//                                .filter(p -> !REMOVE_WORDS.stream().filter(r -> p.toLowerCase().is(r)).findAny()
//                                                          .isPresent()).collect(Collectors.toList()));


    }

    public static <T> T measure(final Supplier<T> code) {
        final long current = System.currentTimeMillis();
        final T result = code.get();
        final long elapsed = System.currentTimeMillis() - current;
//        System.out.println(elapsed);
        return result;
    }
}
