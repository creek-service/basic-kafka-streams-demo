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

The payloads used in this tutorial were simple types like `Integer` and `String`.  
Obviously, this massively limits Creek's utility and is why Creek is still in alpha release.
Work to extend this to more complex types using, schema validated, JSON serialization, will be
[starting soon <i class="fas fa-external-link-alt"></i>](https://github.com/creek-service/creek-kafka/issues/25){:target="_blank"}.
{: .notice--info}