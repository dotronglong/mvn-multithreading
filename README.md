# Maven Multi-Threading

In order to support run many commands in parallel so as to increase performance of project (testing)

## Generate distribution file
```bash
mvn package
```

## Run application
```bash
java -jar target/multithreading-1.0-SNAPSHOT.jar
```

It will look for file **mit.xml** in current directory, this is configuration file for application.

Sample of this configuration file could be seen as below

```xml
<?xml version="1.0"?>
<app>
    <config>
        <name>cli</name>
        <sh>/bin/sh</sh>
        <sleep randomSleep="true" sleepIn="2" sleepMax="10"></sleep>
    </config>
    <plugins>
        <plugin name="junit.behat" output="junit.xml">
            <file>log1.xml</file>
            <file>log2.xml</file>
        </plugin>
    </plugins>
    <content>
        <command>
            <exec><![CDATA[vendor/bin/behat -f junit -o log1.xml --tags "@dev"]]></exec>
        </command>
        <command>
            <exec><![CDATA[vendor/bin/behat -f junit -o log2.xml --tags "@staging"]]></exec>
        </command>
    </content>
</app>
```

## mit.xml

### Config section

- name: application name to run, list of support application is (will be extended):
    - cli

### Plugin section
All plugins are located under package **com.dotronglong.multithreading.plugin**, and registered plugins would run right
after all tasks completed

- junit.behat: is a plugin which concat all junit reports into one

### Content section

This is the section which uses by main application
