---
title: Basic Kafka Streams demo
permalink: /
layout: single
toc: true
---

This tutorial will lead you through building a simple Kafka Streams based microservice that consumes a
Kafka topic containing tweets and extracts the usage of Twitter handles, e.g. `@elonmusk`, from the Tweet text, 
producing usage counts to another topic.

**Note:** This is a deliberately simplistic service, allowing the tutorial to focus on demonstrating Creek's core features.
{: .notice--warning}

## Features covered

By the end of this tutorial you should know:
 * How to bootstrap a new repo from the [aggregate-template][aggTemp] repository.
 * How to define a service descriptor: metadata about the API of a service.
 * How to obtain a `kafka` topic's serde, for use in a Kafka Streams topologies.
 * How to build and execute a Kafka Streams topology, using Creek.
 * How to write black-box system tests of the service's Docker image.
 * How to write unit tests of the service's topology.
 * How to debug a service, running in a Docker container, when things aren't working as expected.
 * How to capture code-coverage metrics.
 * How Creek enables all of the above to accelerate Microservice development, 
   allowing you to focus on business logic, not boilerplate.

## Prerequisites

The tutorial requires the following:

* A [GitHub](https://github.com/join) account.
* [Git](https://git-scm.com/book/en/v2/Getting-Started-Installing-Git) installed for source code control.
* [Docker desktop](https://docs.docker.com/desktop/) installed for running containerised system tests.
* (Optional) [IntelliJ IDE](https://www.jetbrains.com/help/idea/installation-guide.html) installed for code development.
* (Optional) [AttachMe IntelliJ plugin](https://plugins.jetbrains.com/plugin/13263-attachme)  installed for debugging containerised services.

## Design

To keep things simple, this example design assumes an upstream gateway service is consuming tweets from the Twitter api,
and producing records to a Kafka topic named `twitter.tweet.text`. 
The produced records have the tweet id in the key and the tweet text in the value.

In a normal system, the upstream gateway service would likely _own_ its `twitter.tweet.text` output topic.
To keep this tutorial self-contained, the tutorial's service will _own_ its `twitter.tweet.text` input topic.

**ProTip:** The concept of topic _ownership_ defines which service / aggregate, and hence team, within an organisation
is responsible for the topic, its configuration, and the data it contains.
{: .notice--info}

{% include figure image_path="/assets/images/creek-demo-design.svg" alt="Service design" %}

The service will search each tweet for Twitter handles, e.g. `@BarackObama`. For each handle, the service will produce
a record to the `twitter.handle.usage` Kafka topic. 
The produced records have the Twitter handle in the key and the number of occurrences in the value.

## Complete solution

The completed tutorial can be viewed [on GitHub][demoOnGh].

[<i class="fab fa-fw fa-github"/>&nbsp; View on GitHub][demoOnGh]{: .btn .btn--success}

 
[aggTemp]: https://github.com/creek-service/aggregate-template
[demoOnGh]: https://github.com/creek-service/basic-kafka-streams-demo