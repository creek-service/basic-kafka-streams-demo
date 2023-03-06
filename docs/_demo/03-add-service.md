---
title: Add a microservice
permalink: /add-service
description: Learn how to use the Creek aggregate template to quickly add new microservices to your aggregate repositories
layout: single
---

With a fresh new repository ready for use, the next step is to add a service module to the repository 

**ProTip:** Each microservice has its own Gradle subproject a.k.a. module.
{: .notice--info}

Setting up a new service manually can be a lengthy process. 
Luckily, the `aggregate-tempalate` comes with an automated way of adding a new service using a GitHub workflow:

1. Go to the `Actions` tab of the new repository on GitHub.
2. Select `Add service module` from the list of available workflows on the left.
3. Click the `Run workflow ▾` button and enter the service name as `handle-occurrence-service`: 
   {% include figure image_path="/assets/images/creek-add-service.png" alt="Add new service" %}

   **Note:** Service names must be lowercase. Only alphanumerics and dashes are supported.
   {: .notice--warning}

   **ProTip:** End your service names with `-service` to make it clear the module contains a microservice.
   {: .notice--info}
4. Click the [Run workflow](){: .btn .btn--small .btn--disabled .btn--success} button below the service name.

This will kick off a workflow that adds the new module, containing the boilerplate code for a new service,
though you may need to refresh the web page to view it. 

Wait for the workflow to complete and pull down the changes to your local machine by running:

```shell
git pull
```
