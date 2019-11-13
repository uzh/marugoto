# marugoto [![Build Status](https://travis-ci.org/uzh/marugoto.svg?branch=master)](https://travis-ci.org/uzh/marugoto) 

E-Learning Application for Story Telling.

## Documentation

[Graph Model_For_Application_Entities](docs/marugoto-model-application-entities.pdf) 

[Graph Model_For_State_Entities](docs/marugoto-model-state-entities.pdf)

[Graph Model_For_Storyline_Entities](docs/marugoto-model-storyline-entities.pdf)

## Creating Backend Application build


To create a build of the backend-application, execute the following command in the projects' root directory:

```console
mvnw package -DskipTests
```

The packaged ``*.war``-file can be found under:

```console
backend/target/backend-1.0.1.war
```

To run the ``war``-file, switch to backend/target and execute the following command:

```console
java -jar backend-1.0.1.war
```
To check api-docs(swagger) type in your browser:

```console
SERVER_URL/swagger-ui.html
```

## Creating Shell CLI build

To create a build of the shell-project, execute the following command in the projects' root directory:

```console
mvnw package -Dmaven.test.skip=true
```

The packaged ``*.jar``-file can be found unter:

```console
shell/target/shell-1.0.1.jar
```

To run the ``jar``-file, execute the following command:

```console
java -jar shell-1.0.1.jar
```

Type ``help`` into the shell-terminal to get a list of available commands.


## License
[![FOSSA Status](https://app.fossa.io/api/projects/git%2Bgithub.com%2Fuzh%2Fmarugoto.svg?type=large)](https://app.fossa.io/projects/git%2Bgithub.com%2Fuzh%2Fmarugoto?ref=badge_large)
