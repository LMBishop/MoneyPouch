<p align="center">
<img src="https://leonardobishop.com/artwork/MoneyPouch%20+%20Guide.png" width="200" height="200"><br>
<img src="http://isitmaintained.com/badge/resolution/LMBishop/MoneyPouch.svg">
<img src="http://isitmaintained.com/badge/open/LMBishop/MoneyPouch.svg">
<img src="https://mc-download-badges.herokuapp.com/services/spigotsongoda/downloads.php?spigot=21905&songoda=moneypouch-moneypouch"><br>
<h1 align="center">MoneyPouch</h1>
</p>

## About
This plugin allows you to create pouches, which contain a random amount of money.

## Downloads/Building
The latest release version of MoneyPouch can be found on either
* [SpigotMC](https://www.spigotmc.org/resources/21905/)
* [Songoda](https://songoda.com/marketplace/product/540)

Alternatively, you can build MoneyPouch via Gradle from source using ``gradlew shadowJar``.

You can include MoneyPouch in your project using [JitPack](https://jitpack.io/#LMBishop/MoneyPouch):
### Maven
```xml
<repository>
    <id>jitpack.io</id>
    <url>https://jitpack.io</url>
</repository>

<!-- MoneyPouch -->
<dependency>
    <groupId>com.github.LMBishop</groupId>
    <artifactId>MoneyPouch</artifactId>
    <version>master-SNAPSHOT</version>
<dependency>
```

### Gradle
```groovy
repositories {
    maven { url = 'https://jitpack.io' }
}  
dependencies {
    // MoneyPouch
    compileOnly 'com.github.LMBishop:MoneyPouch:master-SNAPSHOT'
}
```

## Contributors
See https://github.com/LMBishop/MoneyPouch/graphs/contributors

## Support
For support please open a [GitHub issue](https://github.com/LMBishop/MoneyPouch/issues) or join our [Discord server](https://discord.gg/mQ2RcJC). Please provide information of the issue, any errors that may come up and make sure you are using the latest version of the plugin.

### Issue Tracker
**This is the preferred method of bug reporting & feature requests**. Please use one of the two templates which are provided. If it is neither a bug report or a feature request and is a question, Discord would be a better place to asked this instead. **Follow the template in the issue tracker**. There is nothing more frustrating than people not reporting a bug correctly by missing out vital steps to reproduce the bug or an incomplete description. If the issue is not correctly formatted, it will be closed and ignored.

### Discord
**This is the preferred method for general questions about MoneyPouch or the development of the project**.

### Language
Please speak English and do not use any vulgar or harmful language. We work on this project in our free time, getting mad at us if things do not work will not achieve anything.

## License
The **source code** for MoneyPouch is licensed under the GNU General Public License v3.0, to view the license click [here](https://github.com/LMBishop/MoneyPouch/blob/master/LICENSE.txt).

The **artwork** for MoneyPouch is licensed under the Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License ![](https://i.creativecommons.org/l/by-nc-sa/4.0/80x15.png), to learn more click [here](https://creativecommons.org/licenses/by-nc-sa/4.0/).

## Configuration Assistance
The configuration documentation can be found at the [wiki](https://github.com/LMBishop/MoneyPouch/wiki/Config).

## Contributing
We welcome all contributions, we will check out all pull requests and determine if it should be added to Quests
### Guidance
* ensure Java 8 is installed on your machine
* fork this repository and clone it
* edit the source code as your please
* run ``gradlew shadowJar`` in the base directory to build Quests
* push to your fork when ready & submit a pull request

### Contribution Guidelines
If you plan on contributing upstream please note the following:
* discuss large changes first
* indent the file with **4 spaces**
* take a look at how the rest of the project is formatted and follow that
* do not alter the version number in ``build.gradle``, that will be done when the release version is ready
* limit the first line of commit messages to ~50 chars and leave a space below that
* **test your changes** on the latest Spigot version before making a pull request

By contributing to MoneyPouch you agree to license your code under the [GNU General Public License v3.0](https://github.com/LMBishop/MoneyPouch/blob/master/LICENSE.txt).
