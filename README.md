# ifmap

[![Build Status][travis-image]][travis-link]

Interactive fiction mapping utility.

## Development Environment

### JDK

IFMap currently targets Java 6.  Therefore, it should be built using JDK 6 to avoid incompatible boot classpath warnings during compilation.  Ensure `JAVA_HOME` points to your JDK 6 installation, for example:

    $ export JAVA_HOME=~/Programs/jdk1.6.0_45

Also ensure this JDK appears before any other JDK in `PATH`:

    $ export PATH=$JAVA_HOME/bin:$PATH

### Eclipse

Recent versions of Eclipse (e.g. Mars) require Java 7 or later.  To run Eclipse with a specific version of Java, specify it on the command line when starting Eclipse, for example:

    $ eclipse -vm /usr/bin &

[travis-image]: https://travis-ci.org/ssoloff/ifmap.svg?branch=master
[travis-link]: https://travis-ci.org/ssoloff/ifmap
