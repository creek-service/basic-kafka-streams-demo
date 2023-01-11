---
title: Basic Kafka Streams demo
permalink: /
layout: single
toc: true
---

Build and test a single Kafka Streams based microservice, introducing the core of Creek's functionality.

The service consumes a Kafka topic, performs a simple transformation on each record, and 
produces the result to another topic.

## Features covered

Features covered in this demo:
 * Bootstrapping a new repo from the [aggregate-template][aggTemp] repository.
 * Defining a service descriptor: metadata about the API of a service.
 * Obtaining `kafka` topic serde for use in Kafka Streams topologies.
 * Building and executing a Kafka Streams topology.
 * Black-box system testing of the service's docker image.
 * Unit testing of the service's topology.
 * Debugging services when things go wrong.

## Prerequisites

The tutorial requires the following:

* A [GitHub](https://github.com/join) account.
* [Git](https://git-scm.com/book/en/v2/Getting-Started-Installing-Git) installed for source code control.
* [Docker desktop](https://docs.docker.com/desktop/) installed for running containerised system tests.
* (Optional) [IntelliJ IDE](https://www.jetbrains.com/help/idea/installation-guide.html) installed for code development.
* (Optional) [AttachMe IntelliJ plugin](https://plugins.jetbrains.com/plugin/13263-attachme)  installed for debugging containerised services.

## Complete solution

The completed tutorial can be viewed [on GitHub](https://github.com/creek-service/basic-kafka-streams-demo).

[<i class="fab fa-fw fa-github"/>&nbsp; View on GitHub][aggTemp]{: .btn .btn--success}

 
[aggTemp]: https://github.com/creek-service/aggregate-template
