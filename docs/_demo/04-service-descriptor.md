---
title: Define the service resources
permalink: /descriptor
layout: single
snippet_comment_prefix: "//"
snippet_source: "../services/src/main/java/io/github/creek/service/basic/kafka/streams/demo/services/ReverseServiceDescriptor.java"
---

Each service within an aggregate defines a _service descriptor_ in the `services` module of the repository.

A _service descriptor_ defines the external resources a service uses and the api it exposes. 
The types of resources a descriptor can reference depends on the installed [Creek extensions][creekExts].

**ProTip:** Service descriptors are accessible by other services within the aggregate, but not by those outside.
Services from other aggregates should only use the aggregate's public API defined in an [aggregate descriptor][aggDescriptor].
{: .notice--info}

This demo will use the [Kafka Streams extension][ksExt], and the service descriptor will include an _input topic_, 
which the service will consume, and an _output topic_, which the service will produces to.

## Define the topic resources

The aggregate template provided a shell service descriptor in the repository named `ReverseServiceDescriptor.java`.
Add the following to `ReverseServiceDescriptor.java` to define the service's input and output topic:

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

The definition includes the topic name, the type stored in each Kafka record's key and value in the topic,
and the topic config, which in this case is just the number of partitions.

The `register` method wrapping each resource descriptor ensures they are registered with the outer service descriptor.

**Note:** The [system tests]({{ "/system-testing" | relative_url}}) we'll define later will use the service descriptor 
to provide the service metadata required to run the service, pipe in inputs and read outputs.
{: .notice--warning}

**ProTip:** It's unusual for a service to _own_ its input topics. 
Input topic descriptors are normally obtained from the owned output topic of another service or aggregate.
It's used here to keep things simple for this introductory demo. 
{: .notice--info}

[todo]: Add link to service chaining demo to the tip above once it's available. 

[creekExts]: https://www.creekservice.org/extensions/
[ksExt]: https://github.com/creek-service/creek-kafka
[aggDescriptor]: https://www.creekservice.org/docs/descriptors/#aggregate-descriptor
[todo]: switch about links to proper creekservice.org links once each repo publishes docs.
