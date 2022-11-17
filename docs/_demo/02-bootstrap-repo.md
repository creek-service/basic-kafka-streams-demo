---
title: Bootstrap a new aggregate
permalink: /bootstrap
layout: single
toc: false
---

Step 1 is to create a new GitHub aggregate repository, into which we'll add our service. 
Rather than manually creating a new repository and performing all the lengthy project setup, 
we can use the [aggregate-template][aggTemp] repository. 
This will create a new repository with all the plumbing and boilerplate code in place.

**ProTip:** An _aggregate_ is simply a logical grouping of services that, together, provide some business function via a defined api.
{: .notice--info}

## Creating a new aggregate repository

1. Click [Create new aggregate][aggTempNew]{: .btn .btn--success} and fill in the details:
   <figure>
     <img src="{{ '/assets/images/creek-create-new-from-agg-template.png' | relative_url }}" alt="Create new aggregate repo">
   </figure>

2. When GitHub creates the new repo, a [boostrap workflow][bootstrapWorkflow] will run to customise the new repository. 
   Wait for this workflow to complete in the _Actions_ tab:

   <figure>
     <img src="{{ '/assets/images/creek-repo-bootstrap.png' | relative_url }}" alt="Wait for boostrap workflow">
   </figure>

3. [Clone the new repository][cloneRepo] locally.
4. Finish the initialisation of the repository by running the `init.sh` script from the root of the repository.

   **Note:** The scripts require `zsh` to be installed.
   {: .notice--warning}

   ```
   ./init.sh
   ```
   
   As well as cleaning up some Creek specific code, the script will provide an opportunity to rename the only service currently defined in the repository.
   When prompted:
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

[aggTemp]: https://github.com/creek-service/aggregate-template
[aggTempNew]: https://github.com/creek-service/aggregate-template/generate
[kafkaExt]: https://www.creekservice.org/creek-kafka
[bootstrapWorkflow]: https://github.com/creek-service/aggregate-template/blob/main/.github/workflows/bootstrap.yml
[cloneRepo]: https://docs.github.com/en/repositories/creating-and-managing-repositories/cloning-a-repository