import logging
import requests
from dataclasses import asdict
from dto.user_photo_notification_dto import UserPhotoNotificationDTO

logger = logging.getLogger(__name__)

class NotificationSender:
    
    def __init__(self, notification_api_base_url: str, timeout: int):
        self.notification_api_base_url = notification_api_base_url
        self.timeout = timeout
    
    def send_user_photo_notification(self, message: UserPhotoNotificationDTO):
        endpoint = f"{self.notification_api_base_url}/ws/user/photo"
        
        response = requests.post(
            endpoint,
            json=asdict(message),
            timeout=self.timeout
        )
        
        logger.info(f"Notification sent. Response: {response.status_code} - {response.text}")
        response.raise_for_status() 