import io.github.creek.service.basic.kafka.streams.demo.services.HandleOccurrenceServiceDescriptor;
import org.creekservice.api.platform.metadata.ComponentDescriptor;

module basic.kafka.streams.demo.services {
    requires transitive basic.kafka.streams.demo.api;
    // begin-snippet: requires-annotations
    requires static com.fasterxml.jackson.annotation;
    requires static creek.base.annotation;
    requires static io.swagger.v3.oas.annotations;

    // end-snippet

    exports io.github.creek.service.basic.kafka.streams.demo.services;
    exports io.github.creek.service.basic.kafka.streams.demo.services.model;

    // begin-snippet: open-to-jackson
    // Required so Jackson can access the JSON types at runtime.
    opens io.github.creek.service.basic.kafka.streams.demo.services.model;

    // end-snippet

    provides ComponentDescriptor with
            HandleOccurrenceServiceDescriptor;
}
