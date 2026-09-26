---
title: Further reading
permalink: /further-reading
description: Recommended reading for once you've completed this Creek tutorial.
layout: single
---

This tutorial has given a high-level view of a lot of the core features and concepts in Creek.
The [next tutorial]({{ site.url | append: "/ks-connected-services-demo/" }}) in the quick-start series covers 
adding a second service and linking services together.
The [third, and final, tutorial]({{ site.url | append: "/ks-aggregate-api-demo/" }}) in the series covers defining an 
aggregate's api, and how to use Creek to interact with parts of a system that predate or don't use Creek.

Additional tutorials will be added over time. These can be found on the [tutorials page]({{ site.url | append: "/tutorials/" }}).

This tutorial's topic values use schema-validated JSON, via the [Creek Kafka JSON serde <i class="fas fa-external-link-alt" aria-hidden="true"></i>][jsonSerde]{:target="_blank"},
with the schemas generated automatically from the `TweetData` and `HandleUsage` Java records defined
[earlier](/descriptor). See that page, and the linked docs, for more on how it works, including how
Creek uses a Schema Registry to share and validate schemas between services.
{: .notice--info}

[jsonSerde]: https://www.creekservice.org/creek-kafka/#json-schema-format