FROM openjdk:17-alpine

RUN apk update && apk upgrade && apk add maven wget

RUN adduser -S awesomenetwork
USER awesomenetwork
WORKDIR /home/awesomenetwork

COPY --chown=awesomenetwork:nogroup server server
RUN mkdir build
COPY --chown=awesomenetwork:nogroup src build/src
COPY --chown=awesomenetwork:nogroup pom.xml build/pom.xml
COPY --chown=awesomenetwork:nogroup scripts build/scripts
COPY --chown=awesomenetwork:nogroup plugin-dependencies.txt build/plugin-dependencies.txt

# Add user's settings.xml because they're logged in
COPY --chown=awesomenetwork:nogroup settings.xml /home/awesomenetwork/.m2/settings.xml

# Download plugin dependencies
RUN sh ~/build/scripts/download-dependencies.sh

# Build this plugin into the server
RUN cd ~/build && mvn package && cp ~/build/target/*.jar ~/server/plugins/

# Download paper
RUN wget https://papermc.io/api/v2/projects/paper/versions/1.18.2/builds/283/downloads/paper-1.18.2-283.jar -O ~/server/server.jar

RUN cd ~/server && java -XX:MaxRAMPercentage=85.0 -jar server.jar
RUN rm -rf ~/server/plugins/autostop*.jar ~/build

RUN apk del maven wget

WORKDIR /home/awesomenetwork/server
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=85.0", "-jar", "server.jar"]
