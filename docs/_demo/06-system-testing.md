---
title: Writing system tests
permalink: /system-testing
description: Learn how to write YAML based system tests that test the functionality of your microservice running in Docker containers
layout: single
toc: true
---

With the production code complete, let's look at adding some tests.

Creek [system-tests][systemTests] perform black-box testing of your service or services.
It executes test suites against your complete services, and any required third-party services, running in a Docker containers.

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

Start by defining the input to send to the service, i.e. the records to produce to the `twitter.tweet.text` Kafka topic.
These will be produced to the topic _after_ the service has started up.

**ProTip:** Input data can also be seeded into the test environment _before_ services are started, by placing
the input file in the `seed` directory, rather than the `inputs` directory. 
{: .notice--info}

Create a file at `system-tests/src/system-test/example-suite/inputs/twitter.tweet.text.yml` with the following content:

{% highlight yaml %}
{% include_snippet all from ../system-tests/src/system-test/example-suite/inputs/twitter.tweet.text.yml %}
{% endhighlight %}

**ProTip:** The name of the file does not need to match the name of the topic, it can be anything.
Name the file to make the test cases easy to understand.
{: .notice--info}

The `!creek/kafka-topic@1` at the top of the file tells the system test parser how to parse this file.
This particular input type is registered by the [Creek Kafka test extension][kafkaTestExt]. 
The number after the `@` symbol is a version number, allowing the type to evolve without breaking existing tests.

The `records` property defines the list of records the system tests will produce to Kafka, with each record's `key` and `value` defined.

**ProTip:** You can define records with `null` keys and values implicitly by excluding the `key` and/or `value` property,
or explicitly by setting the `key` and/or `value` property to `~`.
{: .notice--info}

### Define expected outputs

Given the input above, define the expected output, i.e. the records we expect the service to produce to the
`twitter.handle.usage` topic.

Create a file at `system-tests/src/system-test/example-suite/expectations/twitter.handle.usage.yml` with the following content:

{% highlight yaml %}
{% include_snippet all from ../system-tests/src/system-test/example-suite/expectations/twitter.handle.usage.yml %}
{% endhighlight %}

This file follows a similar format to the _input_ file, defining the type the file should be parsed as, and
the exact records to expect.  

**ProTip:** You can define expectations of records with `null` keys and values by setting the `key` and/or `value` property to `~`.
Unlike for inputs and seeds, not defining the `key` or `value` property in an expectation file means the property is ignored.
Any value is accepted as valid, though it will still need to deserialise correctly.
{: .notice--info}

### Add a test suite file

With the test input and expected output defined, it's time to define the test suite.

Create a file at `system-tests/src/system-test/example-suite/suite.yml` with the following content:

{% highlight yaml %}
{% include_snippet all from ../system-tests/src/system-test/example-suite/suite.yml %}
{% endhighlight %}

This defines a very basic test suite, which starts our `handle-occurrence-service` and executes a single test case.
That test case feeds in the records in the `twitter.tweet.text` YAML file, and expects the output in the `twitter.handle.usage` YAML file. 

**Protip:** The `.yml` extension of files listed under a test's `inputs` and `outputs` property is optional.
{: .notice--info}

**Protip:** A test suite can define `options` to customise the test suite. For example, the Kafka test extension defines
a [`creek/kafka-options`][kafkaOptions] type.
{: .notice--info}

[todo]: add protip about customising additional record handling, ordering etc, using `creek/kafka-options@1`.


## Running the system tests

The system tests can be executed with the following Gradle command:

```
./gradlew systemTest 
```

The system tests will start up a Kafka broker, and the `handle-occurrence-service`, in Docker containers. Once running,
it will produce the records defined in the `twitter.tweet.text.yml` to Kafka and listen for the expected output defined 
in the `twitter.handle.usage.yml` file.  

**Note:** All being well the tests will pass! Why not try changing the expected output and re-running to see what
happens when tests fail.  The system tests output a lot of information about failures.
{: .notice--success}

## How do the system tests work?

We believe that being able to test the business functionality of a service, or services, this quickly and easily is
both pretty cool and a big part of what drove us to develop Creek, but how does it work?

Very briefly, the system tests work by discovering the `handle-occurrence-service`'s service descriptor on the class path.
The system tests inspect the service descriptor. 

As the descriptor defines Kafka based resources, the system tests, with the help of the installed [Creek Kafka system-test extension][kafkaTestExt], 
knows to start a Kafka broker and create any unowned topics.

The service descriptor also defines the name of the service's Docker container, allowing the system tests to start the service.

Finally, the Kafka topic descriptors exposed by the service descriptor provide the information the system tests, and its extensions, 
need to be able to serialize inputs and deserialize outputs.

More information about the system tests can be found [here][systemTests].

[systemTests]:https://github.com/creek-service/creek-system-test
[testPlugin]: https://github.com/creek-service/creek-system-test-gradle-plugin
[kafkaTestExt]: https://github.com/creek-service/creek-kafka/tree/main/test-extension
[kafkaOptions]: https://github.com/creek-service/creek-kafka/tree/main/test-extension#option-model-extensions
[todo]: switch about links to proper creekservice.org links once each repo publishes docs. 
