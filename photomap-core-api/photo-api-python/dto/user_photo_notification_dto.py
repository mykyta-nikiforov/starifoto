from dataclasses import dataclass

@dataclass
class UserPhotoNotificationDTO:
    photoId: int
    userId: int
    type: str 