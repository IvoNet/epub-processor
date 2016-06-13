
def read_authors_file():
    fi = open("./authors.txt", 'r')
    authors = fi.readlines()
    fi.close()
    return authors


def write_authors(authors):
    fo = open("./authors.txt", 'w')
    a = authors.pop().strip()
    authors.append(a)
    fo.write("".join(authors))
    fo.close()


if __name__ == '__main__':
    # read
    authors = read_authors_file()
    print "removing doubles..."
    authors = list(set(authors))
    print "Sorting authors.txt file..."
    authors.sort()
    # write
    write_authors(authors)
    print "Done"
