# Hoplite Docker Swarm preprocessor

Allows fetching Docker Swarm secrets with Hoplite.

Only packaged on GitHub packages, for now.

```kts
repositories {
    maven("https://maven.pkg.github.com/stashymane/hoplite-swarm")
}

dependencies {
    implementation("dev.stashy.hoplite.swarm:hoplite-swarm:<version>")
}
```

## Usage

To allow fetching Swarm secrets, just add the `SwarmSecretPreprocessor()` to your config loader.

```kotlin
val config = ConfigLoaderBuilder.default()
    .addPreprocessor(SwarmSecretPreprocessor())
    // ...
    .build()
    .loadConfigOrThrow<YourConfig>()
```

Then, to use your secret, prefix your secret name with `swarm://`.

```yaml
services:
  hello-world:
    image: hello-world:latest
    secrets:
      - SECRET1
    environment:
      secret1: "swarm://SECRET1"
```

Keep in mind that this does not support the `_FILE` environment name convention common in other projects.
