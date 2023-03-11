---
title: Write the business logic
permalink: /business-logic
description: Learn how to add your business logic to Kafka Streams microservices created using the Creek aggregate template
layout: single
snippet_comment_prefix: "//"
snippet_source: "../handle-occurrence-service/src/main/java/io/github/creek/service/basic/kafka/streams/demo/service/kafka/streams/TopologyBuilder.java"
---

With the topic resources defined in the last step, it's time to write a simple [Kafka Streams][kafkaStreams] 
topology to perform the business logic of this service. 

The service will search each tweets text for occurrences of Twitter handles, e.g. `@katyperry`.
For each handle found, it will produce a record mapping the Twitter handle to its number of occurrences.
For example, it a tweet contained the handle `@katyperry` twice, then it would produce a record
with a key of `@katyperry` and a value of `2`.

## Define the stream topology

The aggregate template provided a shell `TopologyBuilder` class.
Flesh out the class's `build` method to match what's below:

{% highlight java %}
{% include_snippet build-method %}
{% endhighlight %}

**ProTip:** The example code _deliberately_ names each step in the topology. This is good practice.
Relying on default naming can result in topology evolution issues in the future.
Internal store and topic names incorporate the step name. With default naming, the name of a step, and hence the store or topic, 
can change if steps are added or removed.
This can lead to unintentional changes in internal topic names.
If such a change was deployed, any unprocessed messages in the old topics would be skipped.
{: .notice--info}

The above topology consumes `TweetTextStream` we defined in the service's descriptor, 
transforms it in the `extractHandles` method, and produces any output to the `TweetHandleUsageStream`.

The [Creek Kafka Streams extension][ksExt] provides type-safe access to the input and output topic metadata, serde, 
and Kafka cluster properties, allowing engineers and the code to focus on the business logic.

As a single input record can result in zero or more output records, depending on the occurrences of Twitter handles in the tweet text,
we use the `flatMap` method to invoke the `extractHandles` method.

The details of the `extractHandles` method isn't particularly important in the context of demonstrating Creek's functionality.
A simple solution might look like this:

{% highlight java %}
{% include_snippet extract-method %}
{% endhighlight %}

...and that's the production code of the service complete!

**ProTip:** The `Name` instance defined in the `TopologyBuilder` doesn't add much in this example, but as topologies 
get more complex, getting broken down into multiple builder classes, it really comes into its own. 
Check out [its JavaDoc <i class="fas fa-external-link-alt" aria-hidden="true"></i>][nameJavaDocs]{:target="_blank"} to see how it can be used to help avoid topology node name clashes.
{: .notice--info}

[nameJavaDocs]: https://javadoc.io/doc/org.creekservice/creek-kafka-streams-extension/latest/creek.kafka.streams.extension/org/creekservice/api/kafka/streams/extension/util/Name.html
[kafkaStreams]: https://kafka.apache.org/documentation/streams/
[ksExt]: https://github.com/creek-service/creek-kafka
[todo]: switch about links to proper creekservice.org links once each repo publishes docs.