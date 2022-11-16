module example.mod.api {
    requires transitive creek.kafka.metadata;

    exports org.acme.example.api;
    exports org.acme.example.internal to
            example.mod.services,
            example.mod.service;
}
