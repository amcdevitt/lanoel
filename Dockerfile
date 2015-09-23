FROM java:8
MAINTAINER amcdevitt@gmail.com

ENV DEBIAN_FRONTEND noninteractive

RUN apt-get update && apt-get install -y maven
	
WORKDIR /code
COPY . /code/

RUN mvn package

RUN mv /code/target/*.jar .

EXPOSE 80

ENTRYPOINT ["java", "-jar", "lanoel-0.1.0.jar"]