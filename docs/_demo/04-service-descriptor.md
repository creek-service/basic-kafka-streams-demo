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

This demo will use the [Kafka Streams extension][ksExt], and the `handle-occurrence-service`'s descriptor will define a
`twitter.tweet.text` _input topic_, which the service will consume, and a `twitter.handle.usage` _output topic_, 
which the service will produce to.

**Note:** To keep this tutorial self-contained, the service's input topic is _owned_ by the service.
It would be more common for an upstream service or aggregate to own the topic and for the topic's
definition to be imported from there.
{: .notice--warning}

## Define the JSON output payload type

The input topic carries raw tweet text as a Kafka-native `String`. The output topic uses schema-validated JSON,
via the [Creek Kafka JSON serde][jsonSerde]. Its Java type lives in the repository's `services` module,
where services in this aggregate can share the payload type.

Add the following record to `services/src/main/java/io/github/creek/service/basic/kafka/streams/demo/services/model/HandleUsage.java`:

{% highlight java %}
{% include_snippet handle-usage from ../services/src/main/java/io/github/creek/service/basic/kafka/streams/demo/services/model/HandleUsage.java %}
{% endhighlight %}

The `@GeneratesSchema` annotation tells Creek's [JSON schema Gradle plugin][jsonSchemaPlugin] to generate a
JSON schema for the type. The plugin does not infer the compact constructor's validation rules automatically,
so they're expressed explicitly: `@JsonProperty(required = true)` marks a field as non-optional, and
Swagger's `@Schema(minLength = ...)`/`@Schema(minimum = ...)` annotations capture the non-empty `handle`
and positive `count` constraints in the generated schema.

To allow Jackson to do its magic, as we're running under JPMS, we need to open the model package so Jackson can access the types at runtime.

Add the following to `services/src/main/java/module-info.java`:

{% highlight java %}
{% include_snippet open-to-jackson from ../services/src/main/java/module-info.java %}
{% endhighlight %}

**ProTip:** Run `./gradlew :services:generateJsonSchema` to generate the schema without running a full build.
The generated schema file is written under `services/build/generated/resources/schema/main/`.
{: .notice--info}

### JSON Schema generation

The `aggregate-template` repo supplies the necessary configuration and code to make generating a JSON schema easy.

The `services` module's `build.gradle.kts` applies the plugin and adds the required annotation dependencies.
The plugin automatically scans the `services` module for `@GeneratesSchema` annotated types:

{% highlight kotlin %}
{% include_snippet plugins from ../services/build.gradle.kts %}

{% include_snippet dependencies from ../services/build.gradle.kts %}

{% endhighlight %}

The `services` module's `module-info.java` includes the necessary annotations dependencies:

{% highlight java %}
{% include_snippet requires-annotations from ../services/src/main/java/module-info.java %}
{% endhighlight %}

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
and the topic config. Both definitions specify the serialization formats explicitly:
`RawTweetStream` uses Kafka's native format for its `Long` key and `String` tweet-text value,
so no JSON schema is needed for the input. `TweetHandleUsageStream` uses a Kafka-native `String`
key and a schema-validated JSON `HandleUsage` value as defined above.

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
