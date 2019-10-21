FROM openjdk:14-jdk
MAINTAINER amcdevitt@gmail.com

ENV DEBIAN_FRONTEND noninteractive

RUN apt-get install -y maven

#RUN update-java-alternatives -s java-1.8.0-openjdk-amd64
RUN java -version
RUN which java
RUN echo JAVA_HOME = $JAVA_HOME

WORKDIR /code
COPY . /code/

RUN mvn package

RUN mv /code/target/*.jar .

EXPOSE 80

ENTRYPOINT ["java", "-jar", "lanoel-0.1.0.jar"]