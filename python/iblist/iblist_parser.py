__author__ = 'ivonet'

import IvoNet
import os
import re

DONE = 'Error: No author found.'
PATH = '/Users/ivonet/dev/ebook/epub-processor/python/authors-iblist/'
URL = 'http://www.iblist.com/%s'
AUTHOR = 'author%s.htm'

AUTHOR_PAT = re.compile("<h2>Author Information:[ ]*(.+)[ ]*</h2>")

AUTHORS = open("/Users/ivonet/dev/ebook/epub-processor/python/names/authors.txt", "a")


def processPage(counter, page):
    found = AUTHOR_PAT.findall(page)
    if found:
        print found[0], counter
        AUTHORS.write("%s\n" % found[0])
        AUTHORS.flush()
    # print join
    open(os.path.join(PATH, "%s.html" % counter), "w").write(page)


def getPage(counter):
    return IvoNet.open(URL % AUTHOR % counter).read()


def main():
    counter = 248
    page = getPage(counter)
    if page is not None:
        while page is not None and counter <= 22114:
            if DONE not in page:
                processPage(counter, page)
            counter += 1
            page = getPage(counter)
    AUTHORS.close()


if __name__ == '__main__':
    main()