import logging
from kafka import KafkaProducer

logger = logging.getLogger(__name__)

def string_serializer(data):
    return str(data).encode('utf-8')

def create_kafka_producer(bootstrap_servers: str) -> KafkaProducer:
    try:
        producer = KafkaProducer(
            bootstrap_servers=[bootstrap_servers],
            key_serializer=string_serializer,
            value_serializer=string_serializer
        )
        logger.info(f"Kafka producer created for: {bootstrap_servers}")
        return producer
    except Exception as e:
        logger.exception("Error creating Kafka producer")
        raise
