FROM navikt/java:18

COPY build/libs/*.jar ./
COPY build/libs/arbeidssoker-situasjon-all.jar /app/app.jar
