# IF Map

[![Build Status][travis-image]][travis-link]

Interactive fiction mapping utility.

## Development Environment

### JDK

IF Map currently targets Java 7.  Therefore, it should be built using JDK 7 to avoid incompatible boot classpath warnings during compilation.  Ensure `JAVA_HOME` points to your JDK 7 installation, for example:

    $ export JAVA_HOME=~/Programs/jdk1.7.0_80

Also ensure this JDK appears before any other JDK in `PATH`:

    $ export PATH=$JAVA_HOME/bin:$PATH

[travis-image]: https://travis-ci.org/ssoloff/ifmap.svg?branch=master
[travis-link]: https://travis-ci.org/ssoloff/ifmap
