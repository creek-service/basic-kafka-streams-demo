module basic.kafka.streams.demo.api {
    requires transitive creek.kafka.metadata;

    exports io.github.creek.service.basic.kafka.streams.demo.api;
    exports io.github.creek.service.basic.kafka.streams.demo.internal to
            basic.kafka.streams.demo.services,
            basic.kafka.streams.demo.service;
}
