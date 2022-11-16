module example.mod.service {
    requires example.mod.services;
    requires creek.service.context;
    requires creek.kafka.streams.extension;
    requires org.apache.logging.log4j;
}
