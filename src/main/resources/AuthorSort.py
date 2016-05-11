def main():
    fi = open("./authors.txt", 'r')
    authors = fi.readlines()
    fi.close()
    authors.sort()
    fo = open("./authors.txt", 'w')
    fo.write("".join(authors))
    fo.close()


if __name__ == '__main__':
    print "Sorting authors.txt file..."
    main()
    print "Done"
