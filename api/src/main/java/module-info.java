import io.github.creek.service.basic.kafka.streams.demo.api.BasicKafkaStreamsDemoAggregateDescriptor;
import org.creekservice.api.platform.metadata.ComponentDescriptor;

module basic.kafka.streams.demo.api {
    requires transitive creek.kafka.metadata;
    requires static com.github.spotbugs.annotations;

    exports io.github.creek.service.basic.kafka.streams.demo.api;
    exports io.github.creek.service.basic.kafka.streams.demo.internal to
            basic.kafka.streams.demo.services,
            basic.kafka.streams.demo.service;

    provides ComponentDescriptor with
            BasicKafkaStreamsDemoAggregateDescriptor;
}
