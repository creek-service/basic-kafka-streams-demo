---
title: System test coverage
permalink: /system-testing-coverage
description: Learn how Creek captures code coverage metrics when running system test, which test your service running in a Docker container
layout: single
---

Creek captures code coverage metrics from your service, running inside the Docker container, when running the system
tests. It does this by leveraging the [JaCoCo Gradle plugin][JaCoCoPlugin] and some smarts to configure the service
running in the Docker container.

Test coverage is calculated by running the following Gradle command:

```
./gradlew coverage
```

The human-readable coverage report is saved to `build/reports/jacoco/coverage/html/index.html`.

{% include figure image_path="/assets/images/creek-system-test-coverage.png" alt="System test coverage" %}

With just the system tests, the test coverage is pretty good for our new service. 
The only think really missing is coverage of the `io.github.creek.service.basic.kafka.streams.demo.api` package,
and that is because we've not yet looked at utilising the aggregate descriptor defined in that package.
The aggregate descriptor defines the API for the aggregate, one abstraction level up from services, and will be covered in a later tutorial.

[todo]: which tutorial?

In the next step, we will add unit tests. However, with good system test coverage we recommend that unit testing
is limited to testing edge cases that are hard, or impossible, to test using the system tests. Use system testing
to test both the happy and error paths of your business logic.

[JaCoCoPlugin]: https://docs.gradle.org/current/userguide/jacoco_plugin.html
 
