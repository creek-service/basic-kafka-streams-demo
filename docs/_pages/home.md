---
layout: single
permalink: /
title: Basic Kafka Streams demo
---

Get your head around the basics with this basic Kafka Streams based microservice demo.

## Features covered

Features covered in this demo:
 * Bootstrapping a new repo from the [aggregate-template][aggTemp] repository.
 * todo

## Demo Steps

Follow the steps below to be introduced to the core features of Creek and the [Kafka extension][kafkaExt].

### Bootstrap a new repo

Create a new repository using the [aggregate-template][aggTemp] repository:

1. [Create new aggregate][aggTempNew]{: .btn .btn--success} using the [aggregate-template][aggTemp] repository.

   **ProTip:** An 'aggregate' is simply a logical grouping of services that, together, provide some business function via a defined api.
   {: .notice--info}
   
   <figure>
     <img src="{{ '/assets/images/creek-create-new-from-agg-template.png' | relative_url }}" alt="Create new aggregate repo">
   </figure>

2. Upon creating the repo, the [boostrap workflow][bootstrapWorkflow] will run to customise the new repository. 
   Wait for this repository to complete.

   <figure>
     <img src="{{ '/assets/images/creek-repo-bootstrap.png' | relative_url }}" alt="Wait for boostrap workflow">
   </figure>

3. [Clone the new repository][cloneRepo] locally.
4. Finish the initialisation of the repository by running the `init.sh` script.

   **Note:** The scripts require `zsh` to be installed.
   {: .notice--warning}

   ```
   ./init.sh
   ```
   
   As well as cleaning up some Creek specific code, the script will provide an opportunity to rename the only service currently defined in the repository.
   When prompted
   1. Enter the _service name_ as `reverse-service`.
   2. Leave the _service descriptor class name_ as the default by pressing `return`.
   3. Enter `y` to confirm

   ```
   ./init.sh --succinct
   Service name [example-service]: reverse-service
   Service descriptor class name [ReverseServiceDescriptor]: 
   
   About to customise repository using:
   Service name: reverse-service
   Service descriptor class name: ReverseServiceDescriptor
   
   Are you sure? (y/n) y
   ```

5. Commit the changes back to the GitHub
   ```
   git add -A
   git commit -m "init script"
   git push
   ```

### Define service api



[aggTemp]: https://github.com/creek-service/aggregate-template
[aggTempNew]: https://github.com/creek-service/aggregate-template/generate
[kafkaExt]: https://www.creekservice.org/creek-kafka
[bootstrapWorkflow]: https://github.com/creek-service/aggregate-template/blob/main/.github/workflows/bootstrap.yml
[cloneRepo]: https://docs.github.com/en/repositories/creating-and-managing-repositories/cloning-a-repository