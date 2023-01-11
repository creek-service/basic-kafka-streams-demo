---
title: Add a microservice.
permalink: /add-service
layout: single
---

With a fresh new repository ready for use, the next step is to add a service module to the repository 

**ProTip:** Each microservice has its own Gradle subproject a.k.a. module.
{: .notice--info}

Setting up a new service manually can be a lengthy process. Luckily, the `aggregate-tempalate` makes it simple!

1. Go to the `Actions` tab of the new repository on GitHub.
2. Select `Add service module` from the list of available workflows on the left.
3. Click the `Run workflow ▾` button and enter the service name as `reverse-service`: 
   <figure>
     <img src="{{ '/assets/images/creek-add-service.png' | relative_url }}" alt="Add new service">
   </figure>

   **Note:** Service names must be lowercase. Only alphanumerics and dashes are supported.
   {: .notice--warning}

   **ProTip:** End your service names with `-service` to make it clear the module contains a microservice.
   {: .notice--info}
4. Click [Run workflow](){: .btn .btn--small .btn--disabled .btn--success} button below the service name.

This will kick off a workflow that adds the new module containing the boilerplate code for a new service,
though you may need to refresh the page to view it.
