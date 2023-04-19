---
title: Add a microservice
permalink: /add-service
description: Learn how to use the Creek aggregate template to quickly add new microservices to your aggregate repositories.
layout: single
---

With a fresh new repository ready for use, the next step is to add a service module to the repository.

**ProTip:** Each microservice has its own Gradle subproject a.k.a. module.
{: .notice--info}

Setting up a new service manually can be a lengthy process. 
Luckily, the `aggregate-tempalate` comes with automated ways of adding a new services:

1. Go to the `Actions` tab of the new repository on GitHub.
2. Select `Add service module` from the list of available workflows on the left.
   {% include figure image_path="/assets/images/creek-add-service-workflow.png" alt="Add new service module workflow" %}
3. Click the `Run workflow ▾` button and enter the service name as `handle-occurrence-service`: 
   {% include figure image_path="/assets/images/creek-add-service.png" alt="Add new service" %}

   **Note:** Service names must be lowercase. Only alphanumerics and dashes are supported.
   {: .notice--warning}

   **ProTip:** End your service names with `-service` to make it clear the module contains a microservice.
   {: .notice--info}
4. Click the [Run workflow](){: .btn .btn--small .btn--disabled .btn--success} button below the service name.

This will kick off a workflow that adds the new module, containing the boilerplate code for a new service,
though the page may need refreshing to view it.

{% include figure image_path="/assets/images/creek-add-service-workflow-running.png" alt="Running workflow" %}

**ProTip:** This workflow run will commit the new service's code directly to the `main` branch.
This is incompatible with [GitHub branch protect rules <i class="fas fa-external-link-alt"></i>][ghBranchProtectionRules]{:target="_blank"}.
See the [Aggregate template's docs <i class="fas fa-external-link-alt"></i>][aggTempAddService]{:target="_blank"} 
for alternatives compatible with branch protection.
{: .notice--info}

Wait for the workflow to complete and pull down the changes to your local machine by running:

```shell
git pull
```

[ghBranchProtectionRules]: https://docs.github.com/en/repositories/configuring-branches-and-merges-in-your-repository/defining-the-mergeability-of-pull-requests/about-protected-branches
[aggTempAddService]: https://www.creekservice.org/aggregate-template/add-service