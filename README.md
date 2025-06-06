# Chunker

**Convert Minecraft worlds between Java Edition and Bedrock Edition**

Chunker is a Java application which allows you to convert Java and Bedrock Minecraft worlds. It provides a simple
interface for converting worlds and allows you to upgrade and downgrade worlds between different versions of the
game.

Supported Formats:

- Bedrock
    - 1.12.0
    - 1.13.0
    - 1.14.0 - 1.14.60
    - 1.16.0 - 1.16.220
    - 1.17.0 - 1.17.40
    - 1.18.0 - 1.18.30
    - 1.19.0 - 1.19.80
    - 1.20.0 - 1.20.80
    - 1.21.0 - 1.21.90
- Java
    - 1.8.8
    - 1.9.0 - 1.9.3
    - 1.10.0 - 1.10.2
    - 1.11.0 - 1.11.2
    - 1.12.0 - 1.12.2
    - 1.13.0 - 1.13.2
    - 1.14.0 - 1.14.4
    - 1.15.0 - 1.15.2
    - 1.16.0 - 1.16.5
    - 1.17.0 - 1.17.1
    - 1.18.0 - 1.18.2
    - 1.19.0 - 1.19.4
    - 1.20.0 - 1.20.6
    - 1.21.0 - 1.21.6

**Microsoft Creator Docs:**
https://learn.microsoft.com/en-us/minecraft/creator/documents/chunkeroverview?view=minecraft-bedrock-stable

App Usage
--------

You can find pre-built copies of Chunker in the [releases section](https://github.com/HiveGamesOSS/Chunker/releases).
Otherwise, see the building section on how to build Chunker yourself.

Download the appropriate version of the application depending on your operating system and then you will be able to run
the electron based frontend for Chunker.

Chunker defaults to a maximum of 75% of available memory, you can customise this by specifying the amount when launching
Chunker e.g. `Chunker.exe -Xmx8G` for 8 gigabytes.

Chunker forwards `-Xmx` and `-Xms` to the backing JVM, if you wish to supply other options use `--java-options="..."`.

CLI Usage
--------

**Requirements**

- Java 17 or higher

You can find pre-built copies of Chunker in the [releases section](https://github.com/HiveGamesOSS/Chunker/releases).
Otherwise, see the building section on how to build Chunker yourself.

Chunker can be run as a command-line application or as a UI, to use Chunker as a command line application run it as so:

`java -jar chunker-cli-VERSION.jar -i "my_world" -f BEDROCK_1_20_80 -o output`

The following parameters are required:

- `-i` / `--inputDirectory` - the path relative to the application which should be used as the input directory.
- `-o` / `--outputDirectory` - the path relative to the application which should be used as the output directory.
- `-f` / `--outputFormat` - the output format to convert the world to in the form `EDITION_X_Y_Z`,
  e.g. `JAVA_1_20_5`, `JAVA_1_20`, `BEDROCK_1_19_30`.

Additionally, the following parameters are supported:

- `-m` / `--blockMappings` - a path to a json file or a json object containing block mappings.
- `-s` / `--worldSettings` - a path to a json file or a json object containing world settings.
- `-p` / `--pruning` - a path to a json file or a json object containing pruning settings.
- `-c` / `--converterSettings` - a path to a json file or a json object containing converter settings.
- `-d` / `--dimensionMappings` - a path to a json file or a json object containing dimension mappings.
- `-k` / `--keepOriginalNBT` - indicates that NBT should be copied from the input to output where processed by Chunker,
  this is only supported where the output format is the same as the input and for optimal results you will want to copy
  the input world to the output folder prior to conversion.

You can export settings for your world by using the web interface on `https://chunker.app` through the Advanced
Settings -> Converter Settings tab, the CLI also supports preloading settings from the input directory.

You can also get Chunker to list available formats by providing an incorrect input,
e.g. `java -jar chunker-VERSION.jar -f ?`.

Building
--------

**Requirements**

- Git
- Java 17 or higher
- Gradle (Optional)

**Note:** Chunker is split into `app` and `cli`, the app provides an electron frontend for the application and the cli
is a pure java application which can be used for conversion / integrating conversion.

**Steps**

1. Clone this repository via `git clone git://github.com/HiveGamesOSS/Chunker.git`.
2. Build the project via `./gradlew build`.
3. Obtain the binary from `build/libs/` (either as a CLI jar, native CLI executable or with the electron frontend).

Chunker also uses its own fork of a Java LevelDB implementation, https://github.com/HiveGamesOSS/leveldb-mcpe-java/.

Testing
--------

Chunker attempts to do automated testing where possible to validate data, an example of this is block identifiers are
validated against the palette of the Bedrock and Java, this allows issues with faulty mappings to be identified in the
build process. You can skip tests in the build process by appending `-x test` to the `./gradlew build` command.

Some tests have been excluded from the default test suite marked with the "LongRunning" tag, this is because they can
take several minutes to fully complete.


Currently unsupported features
--------
The following features do not convert (or have limited conversion) when using Chunker:

- Entities (excluding paintings / item frames).
- Structure data (e.g. Villages / Strongholds).

License and Legal
--------

The project is MIT licensed you can find details in the [LICENSE](LICENSE).

This project is maintained by Hive Games. This project receives funding from Mojang Studios. Mojang Studios and it's
parent company Microsoft assume no responsibility for the contents of this project.

We're hiring!
--------

Join Hive Games, the company that maintains Chunker, 'The Hive' Minecraft featured server, and more!
[Check out our hiring page.](https://jobs.playhive.com/software-engineer-java-186860/)
