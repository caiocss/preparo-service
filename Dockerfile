FROM clojure:temurin-17-alpine AS builder
ENV CLOJURE_VERSION=1.11.1.1182
RUN mkdir -p /build
WORKDIR /build
COPY ./ /build/

RUN clojure -T:build ci

FROM eclipse-temurin:17-alpine AS runner
RUN addgroup -S preparo-service && adduser -S preparo-service -G preparo-service
RUN mkdir -p /service && chown -R preparo-service. /service
USER preparo-service

RUN mkdir -p /service
WORKDIR /service
ENV HTTP_PORT=8080
EXPOSE 8080
COPY --from=builder /build/target/net.clojars.mba-fiap/preparo-service-0.1.0-SNAPSHOT.jar /service/preparo-service.jar
ENTRYPOINT ["java", "-jar", "/service/preparo-service.jar"]
