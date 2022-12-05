---
title: Writing unit tests
permalink: /unit-testing
layout: single
snippet_comment_prefix: "//"
snippet_source: "../reverse-service/src/test/java/io/github/creek/service/basic/kafka/streams/demo/service/kafka/streams/TopologyBuilderTest.java"
toc: true
---

Secondary to system testing, it can be useful to unit test a topology. With good system test coverage this may
not be necessary. If it is needed, then Creek can help.

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

The above adds a single unit test for our simple topology. More extensive testing would probably be advisable in a real-world project.

## Topology test

The eagle-eyed of you may have noticed an existing test at the bottom of `TopologyBuilderTest` called `shouldNotChangeTheTopologyUnintentionally`.
The intent of this test is to detect any unintentional changes to the Kafka Streams topology.

**Warning:** There are certain categories of topology changes that are not backwards compatible with earlier versions
of a deployed service, e.g. those that change topic names.
Creek recommends always naming operators in the Kafka Streams DSL. (See the [Kafka Streams docs][kafkaStreams] for more information).
{: .notice--warning}

The test compares the topology with the last know topology and fails if they differ.
If the change is intentional, then the `reverse-service/src/test/resources/kafka/streams/expected_topology.txt` 
file can be updated to reflect the latest topology.  For this tutorial, the file should be updated to contain:

```
Topologies:
   Sub-topology: 0
    Source: ingest-input (topics: [input])
      --> switch
    Processor: switch (stores: [])
      --> egress-output
      <-- ingest-input
    Sink: egress-output (topic: output)
      <-- switch
```
[todo]: update the above to %include% the actual expectations file, once include_raw exists. 

If you find this test more of a hindrance than a help... delete it! :smile:

## Test Coverage

Test coverage is calculated by running the following Gradle command:

```
./gradlew coverage
```

This will execute the unit tests and use [JaCoCo][JaCoCo] to calculate the test coverage. 
The coverage report is saved at `build/reports/jacoco/coverage/html/index.html`.

<figure>
 <img src="{{ '/assets/images/creek-unit-test-coverage.png' | relative_url }}" alt="Unit test coverage">
</figure>

The test coverage is pretty good for our new service. The main thing missing is the entry point to the service, 
which is tested by the system-tests, and will be included in the test coverage report once
[creek-system-test issue #172](https://github.com/creek-service/creek-system-test/issues/172) is complete.

**ProTip:** The repository also has a Gradle task and GitHub workflow step to upload the coverage report to 
[coveralls.io][coveralls], should this be something you need, or the project can be customised to publish
coverage reports elsewhere.
{: .notice--info}

[coveralls]: https://coveralls.io/
[kafkaStreams]: https://kafka.apache.org/33/documentation/streams/developer-guide/dsl-topology-naming.html
[JaCoCo]: https://github.com/jacoco/jacoco
[ksTest]: https://github.com/creek-service/creek-kafka/tree/main/streams-test
[todo]: update links above as docs move onto creekservice.org