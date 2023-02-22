---
title: Define the service's resources
permalink: /descriptor
description: Learn how to write a Creek service descriptor, which defines metadata about a microservice and the external resources it uses.
layout: single
snippet_comment_prefix: "//"
snippet_source: "../services/src/main/java/io/github/creek/service/basic/kafka/streams/demo/services/HandleOccurrenceServiceDescriptor.java"
---

Each service within an aggregate defines a _service descriptor_ in the `services` module of the repository.

A _service descriptor_ defines the external resources a service uses and the api it exposes. 
The types of resources a descriptor can reference depends on the installed [Creek extensions][creekExts].

**ProTip:** Service descriptors are accessible by other services within the aggregate, but not by those outside.
Services from other aggregates should only use the aggregate's public API defined in an [aggregate descriptor][aggDescriptor].
{: .notice--info}

This demo will use the [Kafka Streams extension][ksExt], and the `handle-occurrence-service`'s descriptor will define a
`twitter.tweet.text` _input topic_, which the service will consume, and a `twitter.handle.usage` _output topic_, 
which the service will produces to.

**Note:** To keep this tutorial self-contained, the service's input topic is _owned_ by the service.
It would be more common for an upstream service or aggregate to own the topic and for the topic's
definition to be imported from there. This will be covered in a later tutorial. 
{: .notice--warning}

[todo]: http:// update note above with link to the tutorial on linking aggregates together.

## Define the topic resources

The aggregate template provided a shell service descriptor in the repository named `HandleOccurrenceServiceDescriptor.java`.
Add the following to the class to define the service's input and output topics:

{% highlight java %}
{% include_snippet includes-1 %}

{% include_snippet includes-2 %}

{% include_snippet class-name %}

{% include_snippet topic-resources %}

    ...
}
{% endhighlight %}


The two class constants define the input and output topics the services use.
These constants will be used later when building the Kafka Streams topology.

Each topic definition includes the topic name, the types stored in the topic's records' key and value,
and the topic config, which in this case is just the number of partitions.

The `register` method wrapping each resource descriptor ensures they are registered with the outer service descriptor.

**Note:** The [system tests]({{ "/system-testing" | relative_url}}) we'll define later will use the service descriptor 
to discover the service metadata required to run the service, pipe in inputs and read outputs.
{: .notice--warning}

[creekExts]: https://www.creekservice.org/extensions/
[ksExt]: https://github.com/creek-service/creek-kafka
[aggDescriptor]: https://www.creekservice.org/docs/descriptors/#aggregate-descriptor
[todo]: switch about links to proper creekservice.org links once each repo publishes docs.
