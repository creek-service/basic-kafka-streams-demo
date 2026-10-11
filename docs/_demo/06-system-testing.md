---
title: Writing system tests
permalink: /system-testing
description: Learn how to write YAML based system tests that test the functionality of your microservice running in Docker containers
layout: single
toc: true
---

With the production code complete, let's look at adding some tests.

Unit tests are useful to a point, but ideally you should also test functionality of the complete service.
Creek [system-tests][systemTests] perform black-box testing of your service or services.
It executes test suites against your complete services, and any required third-party services, running in Docker containers.

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
the input file in the `seed` directory, rather than the `inputs` directory. See [Seed data](#seed-data) below.
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
As `twitter.tweet.text`'s value is JSON (see the [previous step](/descriptor)), each record's `value` is itself
a nested object, whose properties match the fields of the `TweetData` record, rather than a single scalar.

**ProTip:** You can define records with `null` keys and values implicitly by excluding the `key` and/or `value` property,
or explicitly by setting the `key` and/or `value` property to `~`.
{: .notice--info}

### Seed data

`twitter.tweet.text` is _owned_ by the `handle-occurrence-service` (see the [previous step](/descriptor)).
Data _seeded_ into an owned topic, rather than sent as a regular input, is special: it is produced
_before_ any service under test is started, whereas regular inputs, like the one defined above, are only
produced once every service is already running.

This matters because seeding is the only way to get records into a topic a service owns and consumes from
_before_ that service starts — for example, to test how a service behaves when it starts up with a backlog
of unprocessed messages already waiting for it.

Create a file at `system-tests/src/system-test/example-suite/seed/twitter.tweet.text.yml` with the following content:

{% highlight yaml %}
{% include_snippet all from ../system-tests/src/system-test/example-suite/seed/twitter.tweet.text.yml %}
{% endhighlight %}

This looks identical to a regular input file — the only difference is the directory it lives in. Unlike
regular inputs, seed data doesn't need to be listed under a test case's `inputs` property in `suite.yml`;
it's picked up automatically for every test case in the suite, because it needs to be in place before any
of them start.

**Note:** Creek ensures any resources the seed data needs — the topic itself, and, since this topic uses
JSON, its schema in the Schema Registry — are created before the seed data is produced, and before any
service under test starts. You don't need to do anything extra to make this work.
{: .notice--info}

### Define expected outputs

Given the input and seed data above, define the expected output, i.e. the records we expect the service
to produce to the `twitter.handle.usage` topic. This should include records derived from both the seeded
tweets and the regular input tweets.

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
a [`creek/kafka-options`][kafkaOptions] type, that can be used to control required message handling and more.
{: .notice--info}

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

**Note:** As this demo's topics use JSON values, the Kafka system-test extension also automatically starts a
Schema Registry container — no extra configuration is needed beyond installing the [`creek-kafka-json-serde`][jsonSerde] extension.
{: .notice--info}

Before any service under test is started, the system tests ensure every resource referenced by seed data
exists — creating `twitter.tweet.text` and registering its schema, in this case — even though the topic
is conceptually owned by the service that's about to start. Only then is the seed data produced, and only
after that are the services under test started. This ordering guarantee is what makes seeding into an
owned topic possible.

The service descriptor also defines the name of the service's Docker container, allowing the system tests to start the service.
Once the service is running, its own start-up creates any topics and schemas it owns that weren't already
needed by seed data — `twitter.handle.usage`, in this case.

Finally, the Kafka topic descriptors exposed by the service descriptor provide the information the system tests, and its extensions, 
need to be able to serialize inputs and deserialize outputs.

More information about the system tests can be found [here][systemTests].

[systemTests]: /creek-system-test
[testPlugin]: https://github.com/creek-service/creek-system-test-gradle-plugin
[kafkaTestExt]: /creek-kafka/#system-test-extension
[kafkaOptions]: /creek-kafka/#option-model-extensions
[jsonSerde]: https://www.creekservice.org/creek-kafka/#json-schema-format
[todo]: switch about links to proper creekservice.org links once each repo publishes docs. 
