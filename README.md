[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Coverage Status](https://coveralls.io/repos/github/creek-service/aggregate-template/badge.svg?branch=main)](https://coveralls.io/github/creek-service/aggregate-template?branch=main)
[![build](https://github.com/creek-service/aggregate-template/actions/workflows/build.yml/badge.svg)](https://github.com/creek-service/aggregate-template/actions/workflows/build.yml)
[![CodeQL](https://github.com/creek-service/aggregate-template/actions/workflows/codeql.yml/badge.svg)](https://github.com/creek-service/aggregate-template/actions/workflows/codeql.yml)

# Creek aggregate repo starter

Click the [Use this template][useThisTemplate] button above and [initialize your repo](#initialize-your-repo) 
for the quickest method of getting started with Creek microservices.

Your new repo will contain the following features:

* A bare [Kafka Streams][kafkaSteams] microservice, utilising [Creek](https://www.creekservice.org).
* Unit testing of the Streams topology.
* Black-box [system-testing][systemTest] of Docker container based microservices.
* Code coverage analysis by [Jacoco][5], including coverage during [system-testing][systemTest] execution.
* Code coverage tracking by [Coveralls.io][coveralls].
* Release versioning by the [Axion-release-plugin][4], with each commit creating a unique patch version.
* Code formatting by [Spotless][1].
* Static code analysis by [Spotbugs][2], [Checkstyle][3] and [CodeQl][codeQL]
* Jars publishing to [GitHub Packages][ghPackages].
* Default set of test dependencies:
    * [Unit5][7]
    * [Mockito][8]
    * [Hamcrest][9]
    * [Guava TestLib][10]
    * [Log4J 2.x][11]
* The following [GitHub build workflow][12]:
    * [CI build](.github/workflows/build.yml)
    * [CodeQl][codeQL] [build](.github/workflows/codeql.yml)
    * [Workflow to manually set the next version](.github/workflows/version.yml)

The template comes with the following modules:

* **[api](api)**: defines the public api of the aggregate, i.e. the service descriptor and associated types.
* **[services](services)**: defines all the services in the aggregate, i.e. service descriptors and their associated types.
* **[example-service](example-service)**: an example Kafka Streams microservice.

Replace sample content with your own and get writing business logic, not boilerplate.

## Initialize your repo

After clicking the [Use this template][useThisTemplate] button above, you can clone your repo locally
and initialize it by running the `init.sh` script in the root.  This will clean up some code specific 
to this template and also give you the opportunity to customise the service module. 

1. Click [Use this template][useThisTemplate] and chose where your new repo should live.
2. [Clone the repo locally](https://docs.github.com/en/repositories/creating-and-managing-repositories/cloning-a-repository).
3. Initialise the new repo:
    1. Run the `init.sh` script to initialize the repo:
       Note: if on OS X, then first run `brew install gnu-sed` and update the path as indicated in the output 'caveats'.

         ```shell
         ./init.sh
         ``` 
    2. Replace/remove this and other README.md files
    3. Commit changes.
4. Customise services:
    4. The `init.sh` will have renamed the example service,
       now duplicate the module to create any other services you need.
       One module for each service this aggregate will contain.
    5. Add new modules to `settings.grable.kts`.
    6. Search for `ChangeMe` comments, following the instructions.
    7. Commit changes.
5. If you would like to use [Coveralls.io][coveralls] to track your code coverage,
   then you'll need to set up an account, import your new repo, and store its Coveralls repo token 
   in a [repo secret][ghSecret] named `COVERALLS_REPO_TOKEN`. 

## Creating your own template repo

Teams utilising Creek to build an ecosystem of microservices will probably want to clone this template
to create the basis of their own template. This can then be customised to meet your needs and simplify
the creation of new team repos.

If creating a template repo, take a look at the [bootstrap workflow](.github/workflows/bootstrap.yml), 
which runs when a new repo is created from this template. You may want something similar in your template.  

### Gradle commands

* `./gradlew` should be the go-to local command to run when developing.
              It will run `./gradlew format`, `./gradlew static` and `./gradlew check`.
* `./gradlew format` will format the code using [Spotless][1].
* `./gradlew static` will run static code analysis, i.e. [Spotbugs][2] and [Checkstyle][3].
* `./gradlew check` will run all checks and tests.
* `./gradlew coverage` will generate a cross-module [Jacoco][5] coverage report.
* `./gradlew cV` to log out the repo's current version.

[1]: https://github.com/diffplug/spotless
[2]: https://spotbugs.github.io/
[3]: https://checkstyle.sourceforge.io/
[4]: https://github.com/allegro/axion-release-plugin
[5]: https://www.jacoco.org/jacoco/trunk/doc/
[coveralls]: https://coveralls.io/
[7]: https://junit.org/junit5/docs/current/user-guide/
[8]: https://site.mockito.org/
[9]: http://hamcrest.org/JavaHamcrest/index
[10]: https://github.com/google/guava/tree/master/guava-testlib
[11]: https://logging.apache.org/log4j/2.x/
[12]: .github/workflows/build.yml
[systemTest]: https://github.com/creek-service/creek-system-test
[kafkaSteams]: https://kafka.apache.org/documentation/streams/
[codeQL]: https://codeql.github.com/
[ghPackages]: https://docs.github.com/en/packages/learn-github-packages/introduction-to-github-packages
[useThisTemplate]: https://github.com/creek-service/aggregate-template/generate
[ghSecret]: https://docs.github.com/en/codespaces/managing-codespaces-for-your-organization/managing-encrypted-secrets-for-your-repository-and-organization-for-github-codespaces