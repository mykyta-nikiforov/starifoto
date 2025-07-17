import logging
from concurrent.futures import ThreadPoolExecutor
from typing import TYPE_CHECKING

from configuration.kafka_topic_config import KafkaTopicConfig
from dto.photo_geojson_data_dto import PhotoGeoJsonDataDTO

if TYPE_CHECKING:
    from kafka import KafkaProducer

logger = logging.getLogger(__name__)


class KafkaService:
    def __init__(self, kafka_producer: 'KafkaProducer', thread_pool: ThreadPoolExecutor):
        self.kafka_producer = kafka_producer
        self.thread_pool = thread_pool
    
    def push_new_photo_event(self, photo_dto: PhotoGeoJsonDataDTO) -> None:
        logger.info(f"Pushing new photo event to Kafka. Photo Id: {photo_dto.photoId}")
        try:
            photo_message = photo_dto.model_dump_json()
            self._send_message(KafkaTopicConfig.PHOTO_ADD_TOPIC, photo_message)
        except Exception as e:
            logger.error(f"Error while preparing message for Kafka. Photo object: {photo_dto}", exc_info=True)
        
    def push_update_photo_event(self, photo_dto: PhotoGeoJsonDataDTO) -> None:
        logger.info(f"Pushing update photo event to Kafka. Photo Id: {photo_dto.photoId}")
        try:
            photo_message = photo_dto.model_dump_json()
            self._send_message(KafkaTopicConfig.PHOTO_UPDATE_TOPIC, photo_message)
        except Exception as e:
            logger.error(f"Error while preparing message for Kafka. Photo object: {photo_dto}", exc_info=True)
    
    def push_delete_photo_event(self, photo_id: int) -> None:
        logger.info(f"Pushing delete photo event to Kafka. Photo Id: {photo_id}")
        self._send_message(KafkaTopicConfig.PHOTO_DELETE_TOPIC, str(photo_id))
    
    def _send_message(self, topic: str, message_body: str) -> None:
        def send_async():
            logger.info(f"Sending message to Kafka. Topic: {topic}, message: {message_body}")
            self.kafka_producer.send(topic, value=message_body)
        
        self.thread_pool.submit(send_async)