---
title: Writing system tests
permalink: /system-testing
layout: single
toc: true
---

With the production code complete, let's look at adding some tests.

Creek [system-tests][systemTests] perform black-box testing of your service or services.
They execute test suites against complete services, running in a Docker containers.

The Docker images being run are the same images you'll deploy into your environments.
By testing the actual Docker image, you can be confident the service does what is intended, 
always assuming that your test coverage is sufficient.

## Write a system test

System tests are written in YAML files, and executed as part of the Gradle build using the
[system test plugin][testPlugin]. The repo already has a `system-tests` module, with the plugin applied.
Follow these steps to add a test suite to the module:

**ProTip:** The `systemm-test` plugin can be applied to individual service modules, allowing for targeted testing 
of a single service, or more normally applied to a `system-tests` module, where the functionality of the aggregate 
as a whole can be tested.
{: .notice--info}

### Define test inputs

Start by defining the input to send to the service, i.e. the records to produce to the `input` Kafka topic,
including some null key and values.  These will be produced to the topic _after_ the service has started up.

**ProTip:** Input data can also be seeded into the test environment _before_ services are started, by placing
the input file in the `seed` directory, rather than the `inputs` directory. 
{: .notice--info}

Create a file at `system-tests/src/system-test/example-suite/inputs/input.yml` with the following content:

{% highlight yaml %}
{% include_snippet all from ../system-tests/src/system-test/example-suite/inputs/input.yml %}
{% endhighlight %}

The `!creek/kafka-topic@1` at the top of the file tells the system test parser how to parse this file.
This particular input type is registered by the Creek Kafka extension. 
The number after the `@` symbol is a version number, allowing the type to evolve without breaking existing tests.

### Define expected outputs

Given the input above, define the expected output, i.e. the records we expect the service to produce to the
`output` topic.

Create a file at `system-tests/src/system-test/example-suite/expectations/output.yml` with the following content:

{% highlight yaml %}
{% include_snippet all from ../system-tests/src/system-test/example-suite/expectations/output.yml %}
{% endhighlight %}

This file follows a similar format to the _input_ file. However, whereas a missing record key or value property 
in the _input_ file is interpreted as a `null` key or value, a missing property in the _expectation_ file means
the property is ignored by the system-tests, i.e. it can have any value.

[todo]: add protip about customising additional record handling, ordering etc, using `creek/kafka-options@1`.

### Add a test suite file

With the test input and expected output defined, it's time to define the test suite.

Create a file at `system-tests/src/system-test/example-suite/suite.yml` with the following content:

{% highlight yaml %}
{% include_snippet all from ../system-tests/src/system-test/example-suite/suite.yml %}
{% endhighlight %}

This defines a very basic test suite, which starts our `reverse-service` and executes a single test case.
That test case feeds in the records in the `input` YAML file, and expects the output in the `output` YAML file. 

**Note:** The `.yml` extension of files listed under a test's `inputs` and `outputs` property is optional.
{: .notice--warning}

## Running the system tests

The system tests can be executed with the following Gradle command:

```
./gradlew systemTest 
```

The system tests will start up a Kafka broker, and the `reverse-service`, in Docker containers. Once running,
it will produce the records defined in the `input.yml` to Kafka and listen for the expected output defined 
in the `output.yml` file.  

**Note:** All being well the tests will pass! Why not try changing the expected output and re-running to see what
happens when tests fail.  The system test outputs a lot of information about failures.
{: .notice--success}

## Test coverage

Creek captures code coverage metrics from your service, running inside the Docker container, when running the system
tests, as the [JaCoCo Gradle plugin][JaCoCoPlugin] is applied.

Test coverage is calculated by running the following Gradle command:

```
./gradlew coverage
```

The coverage report is saved at `build/reports/jacoco/coverage/html/index.html`.

<figure>
 <img src="{{ '/assets/images/creek-system-test-coverage.png' | relative_url }}" alt="System test coverage">
</figure> 

With just the system tests, the test coverage is pretty good for our new service. 
The only think really missing is coverage of the `io.github.creek.service.basic.kafka.streams.demo.api` package,
which is because we've not yet looked at utilising the `BasicKafkaStreamsDemoAggregateDescriptor` class.
This will be covered in a later tutorial.
[todo]: which tutorial?

In the next step, we will add unit tests. However, with good system test coverage we recommend that unit testing
is limited to testing edge cases that are hard, or impossible, to test using the system tests. Use system testing
for testing both the happy and sad paths of your business logic.

## How do the system tests work?

We believe that being able to test the business functionality of a service, or services, this quickly and easily is
both pretty cool and a big part of what drove us to develop Creek, but how does it work?

Very briefly, the system tests work by discovering the `reverse-service`'s service descriptor on the class path.
The service descriptor includes the definitions of the two topic resources, hence the system tests, with the help of
the installed [Creek Kafka system-test extension][kafkaTestExt], knows to start a Kafka broker and create
any required topics. 
The Kafka test extension also knows how to produce inputs, consume outputs and check outputs against the expectations.

[systemTests]:https://github.com/creek-service/creek-system-test
[testPlugin]: https://github.com/creek-service/creek-system-test-gradle-plugin
[kafkaTestExt]: https://github.com/creek-service/creek-kafka/tree/main/test-extension
[JaCoCoPlugin]: https://docs.gradle.org/current/userguide/jacoco_plugin.html
[todo]: switch about links to proper creekservice.org links once each repo publishes docs. 
