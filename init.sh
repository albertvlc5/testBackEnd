#!/usr/bin/env bash

set -e

ENVIRONMENT_NAME="${ENVIRONMENT_NAME:-"dev"}"
JVM_OPS="${JVM_OPS:-""}"
NEWRELIC_APP_NAME="${NEWRELIC_APP_NAME:-"atwork-api"}"
APPLICATION_PORT="${PORT:-"8000"}"

COMMAND=${1:-"web"}
echo $COMMAND


case "$COMMAND" in
  migrate|web)
    # NOTE: NewRelic won't work unless -javaagent is the first argument
    exec java ${JVM_OPS} -Djava.security.egd=file:/dev/./urandom \
      -javaagent:/app/newrelic.jar \
      -Duser.Timezone=America/Sao_Paulo \
      -Dnewrelic.config.license_key=${NEWRELIC_TOKEN} \
      -Dnewrelic.config.app_name=${NEWRELIC_APP_NAME} \
      -Dnewrelic.config.distributed_tracing.enabled=true \
      -Dnewrelic.config.log_level=info \
      -Dnewrelic.config.agent_enabled=true \
      -Dnewrelic.config.log_file_name=STDOUT \
      -Dspring.profiles.active=${ENVIRONMENT_NAME} \
      -Dreactor.netty.pool.maxIdleTime=60000 \
      -jar /app/atwork-api-*.jar \
      $COMMAND
    ;;
  *)
    exec sh -c "$*"
    ;;
esac
