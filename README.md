# marugoto [![Build Status](https://travis-ci.org/uzh/marugoto.svg?branch=master)](https://travis-ci.org/uzh/marugoto) [![Coverage Status](https://coveralls.io/repos/github/uzh/marugoto/badge.svg?branch=master)](https://coveralls.io/github/uzh/marugoto?branch=master) [![FOSSA Status](https://app.fossa.io/api/projects/git%2Bgithub.com%2Fuzh%2Fmarugoto.svg?type=shield)](https://app.fossa.io/projects/git%2Bgithub.com%2Fuzh%2Fmarugoto?ref=badge_shield)

E-Learning Application for Story Telling.

## Documentation
[Graph Model](docs/Graph_model.pdf)

## Creating Shell CLI build

To create a build of the shell-project, execute the following command in the projects' root directory:

```console
mvnw package -Dmaven.test.skip=true
```

The packaged ``*.jar``-file can be found unter:

```console
shell/target/shell-1.0-SNAPSHOT.jar
```

To run the ``jar``-file, execute the following command:

```console
java -jar shell-1.0-SNAPSHOT.jar
```

Type ``help`` into the shell-terminal to get a list of available commands.


## License
[![FOSSA Status](https://app.fossa.io/api/projects/git%2Bgithub.com%2Fuzh%2Fmarugoto.svg?type=large)](https://app.fossa.io/projects/git%2Bgithub.com%2Fuzh%2Fmarugoto?ref=badge_large)