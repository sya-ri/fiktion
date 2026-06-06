# Fiktion Examples

These examples are standalone Gradle projects that use the published Fiktion artifacts.

Run the JVM basic example:

```shell
./gradlew -p examples/jvm-basic test
```

Run the Ktor layered example:

```shell
./gradlew -p examples/ktor-layered test
```

The examples intentionally stay outside the root Gradle build so they model a consumer project rather than another
module in this repository.
