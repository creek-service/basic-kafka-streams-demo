---
title: Bootstrap a new aggregate
permalink: /bootstrap
description: Learn how to use the Creek aggregate template to bootstrap your own Git repository for hosting Microservices
layout: single
---

Step 1 is to create a new GitHub aggregate repository, into which we'll add our service.

**ProTip:** An _aggregate_ is simply a logical grouping of services that, together, provide some business function 
via a defined api. i.e. An aggregate is a level of abstraction above a single microservice.
This is also known as a ['Bounded Context' in DDD nomenclature][bcDDD]. 
{: .notice--info}

Rather than manually creating a new repository and performing all the lengthy project setup,
we can use the [aggregate-template][templateDocs] repository.
This will create a new repository with all the plumbing and boilerplate code in place.

## Creating a new repository from the template

1. Click [<i class="fab fa-fw fa-github"/>&nbsp; Create new aggregate][aggTempNew]{: .btn .btn--success} and fill in the details:
   {% include figure image_path="/assets/images/creek-create-new-from-agg-template.png" alt="Create new aggregate repo" %}

2. When GitHub creates the new repo, a [boostrap workflow][bootstrapWorkflow] will run to customise the new repository.
   Wait for this workflow to complete in the _Actions_ tab:
   {% include figure image_path="/assets/images/creek-repo-bootstrap.png" alt="Wait for boostrap workflow" %}

3. [Clone the new repository][cloneRepo] locally.
4. Finish the initialisation of the repository by running the `clean_up.sh` script from the root of the repository.

   ```
   ./.creek/clean_up.sh
   ```

   The clean-up script will finish off the customisation of the new repository, removing now redundant workflows, 
   scripts and code.

5. Commit the changes back to the GitHub
   ```
   git add -A
   git commit -m "clean_up script"
   git push
   ```

The repository is now ready for services to be added, which will be covered in the next step.

More information about the features and structure of the repository can be found
in the [aggregate template documentation][templateDocs].

[aggTempNew]: https://github.com/creek-service/aggregate-template/generate
[bootstrapWorkflow]: https://github.com/creek-service/aggregate-template/blob/main/.github/workflows/bootstrap.yml
[cloneRepo]: https://docs.github.com/en/repositories/creating-and-managing-repositories/cloning-a-repository
[templateDocs]: https://www.creekservice.org/aggregate-template
[bcDDD]: https://martinfowler.com/bliki/BoundedContext.html