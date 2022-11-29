---
title: Bootstrap a new aggregate
permalink: /bootstrap
layout: single
---

Step 1 is to create a new GitHub aggregate repository, into which we'll add our service.

**ProTip:** An _aggregate_ is simply a logical grouping of services that, together, provide some business function via a defined api.
{: .notice--info}

Rather than manually creating a new repository and performing all the lengthy project setup, 
we can use the [aggregate-template][aggTemp] GitHub repository. 
This will create a new repository with all the plumbing and boilerplate code in place.

1. Follow the instructions for [bootstrapping a new aggregate repository][bootstrapTemplate] from the aggregate-template.
   You can name your repository what every you like, for example `basic-kafak-streams-demo`.
3. Follow the instructions for [adding a new service][addNewService] called `reverse-service`.
   
The repository is now ready to start developing your new microservice. 
More information about the features and structure of the repository can be found
in the [aggregate template documentation][templateDocs].

[aggTemp]: https://github.com/creek-service/aggregate-template
[bootstrapTemplate]: https://www.creekservice.org/aggregate-template/bootstrap
[addNewService]: https://www.creekservice.org/aggregate-template/add-service
[templateDocs]: https://www.creekservice.org/aggregate-template
