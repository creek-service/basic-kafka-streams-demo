import io.github.creek.service.basic.kafka.streams.demo.services.HandleOccurrenceServiceDescriptor;
import org.creekservice.api.platform.metadata.ComponentDescriptor;

module basic.kafka.streams.demo.services {
    requires transitive basic.kafka.streams.demo.api;
    requires transitive com.fasterxml.jackson.annotation;
    requires creek.base.annotation;
    // begin-snippet: requires-static-swagger
    requires static io.swagger.v3.oas.annotations;

    // end-snippet

    exports io.github.creek.service.basic.kafka.streams.demo.services;
    exports io.github.creek.service.basic.kafka.streams.demo.services.model;

    // Required so Jackson can access the JSON payload record at runtime.
    opens io.github.creek.service.basic.kafka.streams.demo.services.model;

    provides ComponentDescriptor with
            HandleOccurrenceServiceDescriptor;
}
