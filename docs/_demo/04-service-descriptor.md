---
title: Define the service's resources
permalink: /descriptor
description: Learn how to write a Creek service descriptor, which defines metadata about a microservice and the external resources it uses.
layout: single
snippet_comment_prefix: "//"
---

Each service within an aggregate defines a _service descriptor_ in the repository's `services` module.

A _service descriptor_ defines the external resources a service uses and the api it exposes. 
The types of resources a descriptor can reference depends on the installed [Creek extensions][creekExts].

**ProTip:** Service descriptors are accessible by other services within the aggregate, but not by those outside.
Services from other aggregates should only use the aggregate's public API defined in its aggregate descriptor.
More information on aggregate APIs and descriptors can be found in the [Kafka Streams: aggregate API tutorial](/ks-aggregate-api-demo/).
{: .notice--info}

This demo will use the [Kafka Streams extension][ksExt], and the `handle-occurrence-service`'s descriptor will define a
`twitter.tweet.text` _input topic_, which the service will consume, and a `twitter.handle.usage` _output topic_, 
which the service will produces to.

**Note:** To keep this tutorial self-contained, the service's input topic is _owned_ by the service.
It would be more common for an upstream service or aggregate to own the topic and for the topic's
definition to be imported from there.
The [Kafka Streams: aggregate API tutorial](/ks-aggregate-api-demo/) covers how to define an aggregate descriptor to allow 
interacting with parts of an architecture that don't use Creek. 
{: .notice--warning}

[todo]: http:// update note above with link to the tutorial on linking aggregates together.

## Define the JSON payload types

Rather than using primitive Kafka types for topic values, this demo uses schema-validated JSON, via the
[Creek Kafka JSON serde][jsonSerde]. The Java types used for topic values live in the repository's `api`
module, so they can be shared with, and their schema understood by, any other service or aggregate that
consumes the topic.

Add the following record to `api/src/main/java/io/github/creek/service/basic/kafka/streams/demo/api/model/TweetData.java`:

{% highlight java %}
{% include_snippet tweet-data from ../api/src/main/java/io/github/creek/service/basic/kafka/streams/demo/api/model/TweetData.java %}
{% endhighlight %}

...and the following to `api/src/main/java/io/github/creek/service/basic/kafka/streams/demo/api/model/HandleUsage.java`:

{% highlight java %}
{% include_snippet handle-usage from ../api/src/main/java/io/github/creek/service/basic/kafka/streams/demo/api/model/HandleUsage.java %}
{% endhighlight %}

The `@GeneratesSchema` annotation tells Creek's [JSON schema Gradle plugin][jsonSchemaPlugin] to generate a
JSON schema for the type. The plugin introspects the type, including its compact constructor, so the
constraints enforced there — a required, non-empty `text`/`handle`, and a `count` greater than zero — are
reflected in the generated schema too.

The `api` module's `build.gradle.kts` applies the plugin and tells it which module to scan for annotated types:

{% highlight kotlin %}
plugins {
    `java-library`
    id("org.creekservice.schema.json")
}

dependencies {
    // ...
    jsonSchemaGenerator("org.creekservice:creek-json-schema-generator:$creekVersion")
}

creek.schema.json {
    typeScanning.moduleWhiteList(moduleName)
    subTypeScanning.moduleWhiteList(moduleName)
}
{% endhighlight %}

**Note:** The `api` module's `module-info.java` also needs to `opens` the package containing these types,
so that Jackson, which the JSON serde uses under the hood, can reflectively access the record's canonical
constructor and component accessors at runtime.
{: .notice--info}

**ProTip:** Run `./gradlew :api:generateJsonSchema` to generate the schemas without running a full build.
The generated schema files are written under `api/build/generated/resources/schema/main/`.
{: .notice--info}

## Define the topic resources

The aggregate template used to bootstrap the repository provided a shell service descriptor in the repository named 
`HandleOccurrenceServiceDescriptor.java`.
Add the following to the class to define the service's input and output topics:

{% highlight java %}
{% include_snippet includes-1 from ../services/src/main/java/io/github/creek/service/basic/kafka/streams/demo/services/HandleOccurrenceServiceDescriptor.java %}

{% include_snippet includes-2 from ../services/src/main/java/io/github/creek/service/basic/kafka/streams/demo/services/HandleOccurrenceServiceDescriptor.java %}

{% include_snippet class-name from ../services/src/main/java/io/github/creek/service/basic/kafka/streams/demo/services/HandleOccurrenceServiceDescriptor.java %}

    ...

{% include_snippet topic-resources from ../services/src/main/java/io/github/creek/service/basic/kafka/streams/demo/services/HandleOccurrenceServiceDescriptor.java %}

    ...
}
{% endhighlight %}


The two class constants define the input and output topics the services use.
These constants will be used later when building the Kafka Streams topology.

Each topic definition includes the topic name, the types stored in the topic's records' key and value,
and the topic config. The `inputTopicWithJsonValue`/`outputTopicWithJsonValue` factory methods used here
declare a topic whose key uses Kafka's native format (`Long`/`String`, in this case) and whose value is
the JSON type defined in the previous step. The `TopicDescriptors` helper class, generated for you when
the repo was bootstrapped, also has plain `inputTopic`/`outputTopic` methods for topics that should use
Kafka's native format for both key and value.

**Note:** A topic's JSON schema is a resource, just like the topic itself, and is _owned_ by whichever
service owns the topic. If another service later consumes this topic as an input, by calling
`toInput()` on `TweetHandleUsageStream` (see the [next tutorial](/ks-connected-services-demo/)), the
schema remains owned by _this_ service — the consuming service only gets an _unowned_ reference to it.
{: .notice--info}

In this instance, the topic config defines the number of partitions and, for one topic, the retention time for 
records in the topic. If no retention time was set, the cluster default would be used.

**ProTip:** Defaulting to the cluster's default topic retention time can be useful as it allows different clusters
to define different defaults. For example, development, QA and Staging environments can have much shorter times
than production.
{: .notice--info}

**ProTip:** The `TopicConfigBuilder` class, which defines the `withPartitions` and `withRetentionTime` methods
used above, is part of the Git repository. It can be customised as your use-case requires.
{: .notice--info}

The `register` method wrapping each resource descriptor ensures they are registered with the outer service descriptor.

**Note:** The [system tests]({{ "/system-testing" | relative_url}}) we'll define later will use the service descriptor 
to discover the service metadata required to run the service, pipe in inputs and read outputs.
{: .notice--warning}

[creekExts]: https://www.creekservice.org/extensions/
[ksExt]: https://www.creekservice.org/creek-kafka
[jsonSerde]: https://www.creekservice.org/creek-kafka/#json-schema-format
[jsonSchemaPlugin]: https://github.com/creek-service/creek-json-schema-gradle-plugin
