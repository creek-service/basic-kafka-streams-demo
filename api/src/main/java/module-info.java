import io.github.creek.service.basic.kafka.streams.demo.api.BasicKafkaStreamsDemoAggregateDescriptor;
import org.creekservice.api.platform.metadata.ComponentDescriptor;

module basic.kafka.streams.demo.api {
    requires transitive creek.kafka.metadata;
    requires com.fasterxml.jackson.annotation;
    requires creek.base.annotation;

    exports io.github.creek.service.basic.kafka.streams.demo.api;
    exports io.github.creek.service.basic.kafka.streams.demo.api.model;
    exports io.github.creek.service.basic.kafka.streams.demo.internal to
            basic.kafka.streams.demo.services,
            basic.kafka.streams.demo.service;

    // Required so Jackson (used by the JSON serde) can reflectively access the record's canonical
    // constructor and component accessors at runtime.
    opens io.github.creek.service.basic.kafka.streams.demo.api.model;

    provides ComponentDescriptor with
            BasicKafkaStreamsDemoAggregateDescriptor;
}
