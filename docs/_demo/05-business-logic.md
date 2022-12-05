---
title: Write the business logic
permalink: /business-logic
layout: single
snippet_comment_prefix: "//"
snippet_source: "../reverse-service/src/main/java/io/github/creek/service/basic/kafka/streams/demo/service/kafka/streams/TopologyBuilder.java"
---

With the topic resources defined in the last step, it's time to write a simple [Kafka Streams][kafkaStreams] 
topology to perform the business logic of this service. 

To keep things super simple for this tutorial, the topology will consume records from the input topic, 
swap the key & value of each record, and produce the transformed records to the output topic.

## Define the stream topology

The aggregate template provided a shell `TopologyBuilder` class.
Flesh out the `build` function in the `TopologyBuilder.java` file to match what's below:

{% highlight java %}
{% include_snippet all %}
{% endhighlight %}

...and that's the release code of the service complete!

**ProTip:** The `Name` instance here doesn't add much in this example, but as topologies get more complex 
it really comes into its own. Check it outs JavaDoc to see how it can be used to help avoid topology node name clashes.
{: .notice--info}

[kafkaStreams]: https://kafka.apache.org/documentation/streams/