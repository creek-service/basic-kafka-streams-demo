---
title: Define the service's resources
permalink: /descriptor
description: Learn how to write a Creek service descriptor, which defines metadata about a microservice and the external resources it uses.
layout: single
snippet_comment_prefix: "//"
snippet_source: "../services/src/main/java/io/github/creek/service/basic/kafka/streams/demo/services/HandleOccurrenceServiceDescriptor.java"
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

## Define the topic resources

The aggregate template used to bootstrap the repository provided a shell service descriptor in the repository named 
`HandleOccurrenceServiceDescriptor.java`.
Add the following to the class to define the service's input and output topics:

{% highlight java %}
{% include_snippet includes-1 %}

{% include_snippet includes-2 %}

{% include_snippet class-name %}

    ...

{% include_snippet topic-resources %}

    ...
}
{% endhighlight %}


The two class constants define the input and output topics the services use.
These constants will be used later when building the Kafka Streams topology.

Each topic definition includes the topic name, the types stored in the topic's records' key and value,
and the topic config.

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
