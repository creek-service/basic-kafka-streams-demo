FROM amazoncorretto:22@sha256:cca97a4ea528ec48182d7969695a50535b70864c532ac3f95986436753808f23
ARG APP_NAME
ARG APP_VERSION
ENV VERSION=$APP_VERSION

LABEL org.opencontainers.image.source=https://github.com/creek-service/basic-kafka-streams-demo/tree/main/handle-occurrence-service

RUN mkdir -p /opt/creek

COPY bin /bin
COPY log4j /log

COPY ${APP_NAME}-${APP_VERSION} /opt/creek/${APP_NAME}-${APP_VERSION}
WORKDIR /opt/creek
RUN ln -s ${APP_NAME}-${APP_VERSION} service

ENTRYPOINT ["/bin/run.sh"]