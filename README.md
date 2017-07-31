# How to build and run
Build application
if you just want to try application you can use H2 file DB. Enable h2-file profile for that:
```
mvn clean install -Ph2-file
```
or just build
```
mvn clean install
```

Run using command line like (H2 file DB) from target directory
```
java -jar evotor-test-1.0-SNAPSHOT.jar ../src/main/resources/application.properties
```

Or configure configure application.properties choosing appropriate jdbc settings and add jdbc driver to classpath
