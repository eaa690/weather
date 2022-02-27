FROM

ARG VERSION

ENV CUCUMBER_GLUE='--glue org.eaa690.weather' \
    CUCUMBER_TAGS='--tags ~@disabled'

ADD target/weather-tests-${VERSION}.jar application.jar
