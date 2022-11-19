---
title: Basic Kafka Streams demo
permalink: /
layout: single
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
 
[aggTemp]: https://github.com/creek-service/aggregate-template
