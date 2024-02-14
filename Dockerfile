FROM openjdk:17
LABEL maintainer="BlueHydrogen"
ADD target/vitals-test-0.0.1-SNAPSHOT.jar vitals
ENTRYPOINT ["java","-jar","vitals"]