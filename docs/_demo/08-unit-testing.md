---
title: Writing unit tests
permalink: /unit-testing
description: Learn how Creek simplifies writing Kafka Streams topology unit tests.
layout: single
snippet_comment_prefix: "//"
snippet_source: "../handle-occurrence-service/src/test/java/io/github/creek/service/basic/kafka/streams/demo/service/kafka/streams/TopologyBuilderTest.java"
toc: true
---

Secondary to system testing, it can be useful to unit test a topology. 
With good system test coverage this may not be necessary. If it is needed, then Creek can help.

The [creek-kafka-streams-test][ksTest] library provides topology test helpers: `TestKafkaStreamsExtensionOptions` 
can be used to initialise Creek without a real Kafka cluster to talk to, and `TestTopics` provides some factory 
methods for creating the test topic instances used in the unit test.

## Add a unit test

The aggregate template provided a shell `TopologyBuilderTest` class.
To add a basic unit test, add the code below to the class: 

{% highlight java %}
{% include_snippet includes %}
// ...

{% include_snippet class-declaration %}
    // ...

{% include_snippet topic-declarations %}
    // ...

{% include_snippet setUp %}

{% include_snippet unit-test %}
    // ...
}
{% endhighlight %}

The above adds a single `shouldOutputHandleOccurrences` unit test for our simple topology, 
which produces a single record to the topologies input topic, and asserts the output is correct.

## Topology test

The eagle-eyed of you may have noticed an existing test at the bottom of `TopologyBuilderTest` called `shouldNotChangeTheTopologyUnintentionally`.
The intent of this test is to detect any unintentional changes to the Kafka Streams topology.

**Warning:** There are certain categories of topology changes that are not backwards compatible with earlier versions
of a deployed service, e.g. those that change topic names.
Creek recommends always naming operators in the Kafka Streams DSL to minimise the chance of unintentional changes to internal topics. 
See the [Kafka Streams docs][kafkaStreams] for more information.
{: .notice--warning}

The test compares the topology with the last know topology and fails if they differ.
If the change is intentional, then the `handle-occurrence-service/src/test/resources/kafka/streams/expected_topology.txt` 
file can be updated to reflect the latest topology.  For this tutorial, the file should be updated to contain:

```
Topologies:
   Sub-topology: 0
    Source: ingest-twitter.tweet.text (topics: [twitter.tweet.text])
      --> extract-handles
    Processor: extract-handles (stores: [])
      --> egress-twitter.handle.usage
      <-- ingest-twitter.tweet.text
    Sink: egress-twitter.handle.usage (topic: twitter.handle.usage)
      <-- extract-handles
```
[todo]: update the above to %include% the actual expectations file, once include_raw exists. 

If you find this test more of a hindrance than a help... delete it! :smile:

## Test Coverage

As before, test coverage can be calculated by running the following Gradle command:

```
./gradlew coverage
```

This will execute the unit and system tests and use [JaCoCo][JaCoCo] to calculate the test coverage. 
The human-readable coverage report is saved at `build/reports/jacoco/coverage/html/index.html`.

{% include figure image_path="/assets/images/creek-unit-test-coverage.png" alt="Unit test coverage" %}

In this case, the test coverage hasn't improved by adding unit tests. Meaning, arguably, this test is superfluous.

While Creek recommends using system tests for functional testing, more complex real-world solutions often still 
benefit from unit testing of the topology, to cover branches that are hard to reach with system tests.  

**ProTip:** The repository also has a Gradle task and GitHub workflow step to upload the coverage report to 
[coveralls.io][coveralls], should this be something you need, or the project can be customised to publish
coverage reports elsewhere.
{: .notice--info}

[coveralls]: https://coveralls.io/
[kafkaStreams]: https://kafka.apache.org/33/documentation/streams/developer-guide/dsl-topology-naming.html
[JaCoCo]: https://github.com/jacoco/jacoco
[ksTest]: https://github.com/creek-service/creek-kafka/tree/main/streams-test
[todo]: update links above as docs move onto creekservice.org