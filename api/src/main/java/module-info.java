import io.github.creek.service.basic.kafka.streams.demo.api.BasicKafkaStreamsDemoAggregateDescriptor;
import org.creekservice.api.platform.metadata.ComponentDescriptor;

module basic.kafka.streams.demo.api {
    requires transitive creek.kafka.metadata;
    requires com.fasterxml.jackson.annotation;
    // begin-snippet: requires-static-swagger
    requires static io.swagger.v3.oas.annotations;
    // end-snippet
    requires creek.base.annotation;
    requires static com.github.spotbugs.annotations;

    exports io.github.creek.service.basic.kafka.streams.demo.api;
    exports io.github.creek.service.basic.kafka.streams.demo.api.model;
    exports io.github.creek.service.basic.kafka.streams.demo.internal to
            basic.kafka.streams.demo.services,
            basic.kafka.streams.demo.handle.occurrence.service;

    // Required so Jackson (used by the JSON serde) can reflectively access the record's canonical
    // constructor and component accessors at runtime.
    opens io.github.creek.service.basic.kafka.streams.demo.api.model;

    provides ComponentDescriptor with
            BasicKafkaStreamsDemoAggregateDescriptor;
}
