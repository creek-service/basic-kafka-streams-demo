module basic.kafka.streams.demo.service {
    requires basic.kafka.streams.demo.services;
    requires creek.service.context;
    requires creek.kafka.streams.extension;
    requires creek.kafka.serde.json.schema;
    requires org.apache.logging.log4j;
}
