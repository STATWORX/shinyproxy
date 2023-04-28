<pre>
               _____ _     _             _____
              / ____| |   (_)           |  __ \
             | (___ | |__  _ _ __  _   _| |__) | __ _____  ___   _
              \___ \| '_ \| | '_ \| | | |  ___/ '__/ _ \ \/ / | | |
              ____) | | | | | | | | |_| | |   | | | (_) >  <| |_| |
             |_____/|_| |_|_|_| |_|\__, |_|   |_|  \___/_/\_\\__, |
                                    __/ |                     __/ |
                                   |___/                     |___/

</pre>

[![Build Status](https://travis-ci.org/openanalytics/shinyproxy.svg?branch=master)](https://travis-ci.org/openanalytics/shinyproxy)

# ShinyProxy

Open Source Enterprise Deployment for Shiny Apps

Learn more at https://shinyproxy.io

#### (c) Copyright Open Analytics NV, 2016-2021 - Apache License 2.0

## Building from source

Clone this repository and install the build dependencies. Build Dependencies you will need:

### 1. JDK 8

Note that you **need** to have JDK8 installed, newer versions of the JDK won't work. On MacOS use: 

```
brew install openjdk@8
```

Oracle makes it very difficult to install Java on MacOS, so here's the link:
https://www.oracle.com/de/java/technologies/javase/javase8u211-later-archive-downloads.html.

Pick one of the development kits, **not** the runtimes.

To check, which versions of Java are installed on your system run 

```
/usr/libexec/java_home -V
``` 

You should see something like this:

```
Matching Java Virtual Machines (1):
    1.8.0_251 (x86_64) "Oracle Corporation" - "Java SE 8" /Library/Java/JavaVirtualMachines/jdk1.8.0_251.jdk/Contents/Home
/Library/Java/JavaVirtualMachines/jdk1.8.0_251.jdk/Contents/Home
```

If you have multiple versions of Java installed, set your JAVA_HOME environment variable to point to version 1.8:
```
export JAVA_HOME="$(/usr/libexec/java_home -v 1.8)"
```

### 2. Maven

```
brew install maven
mvn --version
```

### 3. Openanalytics Container Proxy (STATWORX fork)

After having installed Java and Maven, you'll need to build the STATWORX fork of Openanalytics Containerproxy:

```
git clone https://github.com/STATWORX/containerproxy.git
cd containerproxy
mvn -U clean install -DskipTests
```

## Build ShinyProxy
```
mvn -U clean install
```

or use the Makefile: `make build_jar`

The build will result in a single `.jar` file that is made available in the `target` directory.


## Running the application

```
java -jar target/shinyproxy-2.6.1.jar
```

or use `make run_jar` after you've built the application. If you want to build and run in sequence, just type in `make`.

Navigate to http://localhost:8080 to access the application.  If the default configuration is used, authentication will be done against the LDAP server at *ldap.forumsys.com*; to log in one can use the user name "tesla" and password "password".


## Further information

https://shinyproxy.io

