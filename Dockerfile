FROM adoptopenjdk/openjdk14-openj9 as builder

COPY . .

RUN ./gradlew installDist -Dorg.gradle.daemon=false


FROM adoptopenjdk/openjdk14-openj9

WORKDIR /user/app

COPY --from=builder build/install/kommunity ./

ENTRYPOINT ["/user/app/bin/kommunity"]
