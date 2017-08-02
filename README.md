# Epub processor #

This epub processor is an attempt at creating a multi threaded batch application
for processing, indexing, beautifying, transforming, completing epub books.

It is a work in progress and way not ready for production but maybe some of you have
the same idea and like to play around with it.

It is written with Java SE 8 and I'm at least trying to use its new features, but If
U have suggestions please let me know.

So follow the progress on this repository or become a contributor.
I would like the help...

[Ivo](http://www.ivonet.nl/contact)

# Prerequisites #
* Maven 3x
* **Java SE 8**
* [detect-language](https://github.com/IvoNet/language-detection) (snapshot dependency)
* Python 2.7 (if you want to use the scripts)
* The IvoNet [epublib](https://github.com/IvoNet/epublib) fork. It has minor adjustments to the origional. Credits all to the [Siegmann](http://www.siegmann.nl/epublib)

# Usage #
goto project folder in a terminal

```sh
mvn package
java -d64 -Xms1g -Xmx8g -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=./Heap.dmp -jar artifact/epub-jar-with-dependencies.jar
```

read the explanation

# License #

> [Apache 2.0](http://www.apache.org/licenses/LICENSE-2.0)

# Resources

* [IDPF](http://idpf.org/epub/30)

# Thanks

* Some of the authors have been verified as correct by using Goodreads.com as a source. Thanks!
* 

# Backlog #


AuthorStrategy:
* Too many dropouts on "No Author". Need to try to get the author name from the filename/path if the metadata list is empty. -> IN PROGRESS
* If an author is names like editor, publisher, various, etc it should be stripped from the author list
* if the end list of authors is empty I should try to get it from the path or filename -> IN PROGRESS
* what about names that are sometimes with initials and sometimes completely written?

Other:
* Look at the errors created by the epublib and if I can distill rules / strategies from those errors. -> IN PROGRESS
* See TODO's in the code! -> IN PROGRESS
* Add Epub strategies for all the other items in the Epubs -> OPEN
* Create a searcheable database? -> elasticsearch -> IN PROGRESS
* Remove iTunes data from books as it has no function
* Add StyleStrategy for replacing the current style with an IvoNet style or remove all styles?!
* metadata based on isbn


# Done #
√ Add a real Language detection Strategy!
√ Add a KepubStrategy for making the epubs kobo ready.
√ Convert epub to kepub for kobo readers (like me)
√ Remove watermark strategy.


