FROM gradle:6.3 as builder

USER root

ARG artifactory_contextUrl
ARG artifactory_user
ARG artifactory_password

ENV ORG_GRADLE_PROJECT_artifactory_user=$artifactory_user
ENV ORG_GRADLE_PROJECT_artifactory_password=$artifactory_password
ENV ORG_GRADLE_PROJECT_artifactory_contextUrl=$artifactory_contextUrl

ENV APP_DIR /app
WORKDIR $APP_DIR

COPY build.gradle.kts $APP_DIR/
COPY settings.gradle.kts $APP_DIR/

RUN gradle dependencies

RUN wget "http://download.newrelic.com/newrelic/java-agent/newrelic-agent/current/newrelic-java.zip" && \
    unzip newrelic-java.zip

COPY . $APP_DIR

RUN gradle build -x test

USER guest

# -----------------------------------------------------------------------------

FROM openjdk:13-slim-buster

WORKDIR /app

COPY --from=builder /app/init.sh /app
COPY --from=builder /app/build/libs/atwork-api-*.jar /app/
COPY --from=builder /app/newrelic/newrelic.jar /app/
COPY --from=builder /app/newrelic/newrelic.yml /app/

EXPOSE 8000

ENTRYPOINT ["sh", "init.sh"]
