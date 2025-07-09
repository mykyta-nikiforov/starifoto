import logging
import uuid
import time
import os
from google.cloud import storage
from google.cloud.storage import Blob
from google.oauth2 import service_account

logger = logging.getLogger(__name__)


class CloudStorageService:
    DEFAULT_CONTENT_TYPE = "image/jpeg"

    def __init__(self, bucket_name: str, project_id: str, credentials_path: str):
        self.bucket_name = bucket_name
        self.project_id = project_id
        self.credentials_path = credentials_path
        self.client = self._initialize_client()

    def _initialize_client(self) -> storage.Client:
        try:
            # Initialize Google Cloud Storage client with explicit parameters
            if self.credentials_path and os.path.exists(self.credentials_path):
                # Use service account credentials file
                credentials = service_account.Credentials.from_service_account_file(self.credentials_path)
                client = storage.Client(project=self.project_id, credentials=credentials)
                logger.info(f"Initialized GCS client with service account: {self.credentials_path}")
            else:
                client = storage.Client(project=self.project_id)
                logger.info("Initialized GCS client with default credentials")

            return client

        except Exception as e:
            logger.error(f"Failed to initialize Google Cloud Storage client: {str(e)}")
            raise

    def upload_file(self, file_data: bytes, file_name: str, folder: str) -> Blob:
        size_mb = self._get_size_in_mb(file_data)
        logger.info(f"Uploading to GCS Bucket: {self.bucket_name} File: {file_name} Size: {size_mb}")
        generated_file_name = self._generate_file_name(file_name)
        blob_path = self._build_new_file_blob_path(generated_file_name, folder)
        blob = self._get_blob(blob_path)

        blob.upload_from_string(file_data, content_type=self.DEFAULT_CONTENT_TYPE)

        logger.info(f"File uploaded successfully: {blob_path}")
        return blob

    def delete_file(self, file_path: str) -> bool:
        logger.info(f"Deleting file: {file_path}")
        blob = self._get_blob(file_path)

        if not blob.exists():
            logger.info(f"File not found: {file_path}")
            return False

        blob.delete()
        logger.info(f"Deleted file: {file_path}")
        return True

    def get_file(self, file_path: str) -> Blob:
        logger.info(f"Getting file: {file_path}")
        blob = self._get_blob(file_path)
        return blob

    def _get_blob(self, blob_path: str) -> Blob:
        bucket = self.client.bucket(self.bucket_name)
        return bucket.blob(blob_path)

    def _build_new_file_blob_path(self, file_name: str, folder: str) -> str:
        return f"{folder}/{file_name}"

    def _generate_file_name(self, original_name: str) -> str:
        extension = original_name.split('.')[-1]

        uuid_str = str(uuid.uuid4()).lower().replace('-', '')
        timestamp = int(time.time())
        return f"{uuid_str}_{timestamp}.{extension}"

    def _get_size_in_mb(self, file_data: bytes) -> str:
        file_size_bytes = len(file_data)
        file_size_mb = file_size_bytes / (1024 * 1024)
        return f"{file_size_mb:.2f} MB"
