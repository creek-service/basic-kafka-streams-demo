
import org.creekservice.api.platform.metadata.ComponentDescriptor;

module example.mod.services {
    requires transitive example.mod.api;

    exports org.acme.example.services;

    provides ComponentDescriptor with
            org.acme.example.services.ExampleServiceDescriptor;
}
