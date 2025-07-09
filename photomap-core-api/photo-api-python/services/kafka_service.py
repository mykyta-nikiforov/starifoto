import logging
import asyncio
from concurrent.futures import ThreadPoolExecutor
from typing import TYPE_CHECKING

from configuration.kafka_topic_config import KafkaTopicConfig
from dto.photo_geojson_data_dto import PhotoGeoJsonDataDTO

if TYPE_CHECKING:
    from mappers.photo_mapper import PhotoMapper
    from models.photo import Photo
    from kafka import KafkaProducer

logger = logging.getLogger(__name__)


class KafkaService:
    def __init__(self, 
                 photo_mapper: 'PhotoMapper',
                 kafka_producer: 'KafkaProducer',
                 thread_pool: ThreadPoolExecutor):
        self.photo_mapper = photo_mapper
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
        logger.info(f"Sending message to Kafka. Topic: {topic}, message: {message_body}")
        future = self.kafka_producer.send(topic, value=message_body)
        logger.info(f"Message sent to Kafka topic {topic}")
    
    async def send_kafka_message_async(self, message_type: str, photo: 'Photo', flask_app) -> dict:
        logger.info(f"Sending Kafka message: {message_type}")
        
        def send_in_context():
            with flask_app.app_context():
                if message_type == 'photo_add':
                    photo_dto = self.photo_mapper.photo_to_geojson_data_dto(photo)
                    self.push_new_photo_event(photo_dto)
                elif message_type == 'photo_update':
                    photo_dto = self.photo_mapper.photo_to_geojson_data_dto(photo)
                    self.push_update_photo_event(photo_dto)
                elif message_type == 'photo_delete':
                    self.push_delete_photo_event(photo.id)
                else:
                    raise ValueError(f"Unknown message type: {message_type}")
                
                logger.info(f"Kafka message sent successfully: {message_type}")
                return {"status": "success", "message_type": message_type}
        
        loop = asyncio.get_event_loop()
        result = await loop.run_in_executor(self.thread_pool, send_in_context)
        return result 