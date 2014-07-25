# WTF

This repo is a work in progress version of McIDAS-V built via Maven (and/or Gradle). There is no history preservation going on, and things will be out of date (relative to the actual McV repo).

In order for this to work, you'll need to make sure Nexus is running on kraken.ssec.wisc.edu (expect this server to change if we adopt this or a similar build system). You can check by visiting http://kraken.ssec.wisc.edu:8081/nexus/

## WHY ARE YOU CONTINUALLY COMPLICATING THINGS

I'm hoping to get the entirety of the McV build process down to something like:

1. `mvn package`  
Download the deps, compile, and JAR things up!
2. `mvn eclipse:eclipse`  
Create an Eclipse project with McV ready to go.

## HOW

Maven is available via Homebrew or http://maven.apache.org/

Then just clone this repo and run stuff like:

* `mvn compile`  
* `mvn package`
* `mvn package dependency:copy-dependencies`

