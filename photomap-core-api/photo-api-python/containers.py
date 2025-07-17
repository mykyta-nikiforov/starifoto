from dependency_injector import containers, providers
from concurrent.futures import ThreadPoolExecutor

from mappers.comment_mapper import CommentMapper
from mappers.license_mapper import LicenseMapper
from mappers.photo_mapper import PhotoMapper
from mappers.photo_metadata_mapper import PhotoMetadataMapper
from mappers.tag_mapper import TagMapper
from repositories.comment_repository import CommentRepository
from repositories.image_file_repository import ImageFileRepository
from repositories.license_repository import LicenseRepository
from repositories.photo_repository import PhotoRepository
from repositories.tag_repository import TagRepository
from services.cloud_storage_service import CloudStorageService
from services.comment_service import CommentService
from services.image_file_service import ImageFileService
from services.kafka_service import KafkaService
from services.license_service import LicenseService
from services.map_data_service import MapDataService
from services.photo_file_upload_service import PhotoFileUploadService
from services.photo_manager import PhotoManager
from services.photo_service import PhotoService
from services.supercluster_api_service import SuperClusterApiService
from services.tag_service import TagService
from services.thumbnail_generation_service import ThumbnailGenerationService
from services.user_api_service import UserApiService
from services.user_photo_service import UserPhotoService
from validators.comment_validator import CommentValidator
from validators.common_photo_validator import CommonPhotoValidator
from validators.photo_validator import PhotoValidator
from configuration.kafka_producer_factory import create_kafka_producer
from websocket.notification_sender import NotificationSender


class Container(containers.DeclarativeContainer):
    
    config = providers.Configuration()
    
    thread_pool = providers.Singleton(
        ThreadPoolExecutor,
        max_workers=config.THREAD_POOL_MAX_WORKERS
    )
    
    kafka_producer = providers.Singleton(
        create_kafka_producer,
        bootstrap_servers=config.KAFKA_BOOTSTRAP_SERVERS
    )

    comment_repository = providers.Singleton(CommentRepository)
    image_file_repository = providers.Singleton(ImageFileRepository)
    license_repository = providers.Singleton(LicenseRepository)
    photo_repository = providers.Singleton(PhotoRepository)
    tag_repository = providers.Singleton(TagRepository)

    user_api_service = providers.Singleton(
        UserApiService,
        user_api_base_url=config.USER_API_BASE_URL,
        timeout=config.INTERNAL_API_HTTP_TIMEOUT
    )

    cloud_storage_service = providers.Singleton(
        CloudStorageService,
        bucket_name=config.GOOGLE_CLOUD_STORAGE_BUCKET,
        project_id=config.GOOGLE_CLOUD_PROJECT,
        credentials_path=config.GOOGLE_APPLICATION_CREDENTIALS
    )

    supercluster_api_service = providers.Singleton(
        SuperClusterApiService,
        supercluster_api_base_url=config.SUPERCLUSTER_API_BASE_URL,
        timeout=config.INTERNAL_API_HTTP_TIMEOUT
    )

    notification_sender = providers.Singleton(
        NotificationSender,
        notification_api_base_url=config.NOTIFICATION_API_BASE_URL,
        timeout=config.INTERNAL_API_HTTP_TIMEOUT
    )

    license_mapper = providers.Singleton(LicenseMapper)

    tag_mapper = providers.Singleton(TagMapper)

    license_service = providers.Singleton(
        LicenseService,
        license_repository=license_repository,
        license_mapper=license_mapper
    )

    tag_service = providers.Singleton(
        TagService,
        tag_repository=tag_repository,
        tag_mapper=tag_mapper
    )

    comment_mapper = providers.Singleton(
        CommentMapper,
        user_api_service=user_api_service
    )

    photo_mapper = providers.Singleton(
        PhotoMapper,
        user_api_service=user_api_service
    )

    photo_metadata_mapper = providers.Singleton(
        PhotoMetadataMapper,
        license_service=license_service,
        tag_service=tag_service
    )

    comment_service = providers.Singleton(
        CommentService,
        comment_repository=comment_repository,
        photo_repository=photo_repository
    )

    photo_service = providers.Singleton(
        PhotoService,
        photo_repository=photo_repository,
        photo_mapper=photo_mapper
    )

    user_photo_service = providers.Singleton(
        UserPhotoService,
        photo_service=photo_service,
        photo_mapper=photo_mapper
    )

    kafka_service = providers.Singleton(
        KafkaService,
        kafka_producer=kafka_producer,
        thread_pool=thread_pool
    )

    thumbnail_generation_service = providers.Singleton(ThumbnailGenerationService)

    image_file_service = providers.Singleton(
        ImageFileService,
        cloud_storage_service=cloud_storage_service,
        kafka_service=kafka_service,
        image_file_repository=image_file_repository,
        thumbnail_generation_service=thumbnail_generation_service,
        thread_pool=thread_pool
    )

    photo_file_upload_service = providers.Singleton(
        PhotoFileUploadService,
        image_file_service=image_file_service,
        kafka_service=kafka_service,
        photo_mapper=photo_mapper,
        photo_service=photo_service,
        notification_sender=notification_sender
    )

    map_data_service = providers.Singleton(
        MapDataService,
        photo_service=photo_service,
        supercluster_api_service=supercluster_api_service,
        geojson_generator_base_url=config.GEOJSON_GENERATOR_BASE_URL,
        geojson_generator_timeout=config.INTERNAL_API_HTTP_TIMEOUT
    )

    comment_validator = providers.Singleton(
        CommentValidator,
        comment_repository=comment_repository
    )

    common_photo_validator = providers.Singleton(
        CommonPhotoValidator,
        license_service=license_service
    )

    photo_validator = providers.Singleton(
        PhotoValidator,
        photo_mapper=photo_mapper,
        photo_repository=photo_repository,
        common_validator=common_photo_validator
    )

    photo_manager = providers.Singleton(
        PhotoManager,
        photo_mapper=photo_mapper,
        photo_validator=photo_validator,
        photo_metadata_mapper=photo_metadata_mapper,
        photo_service=photo_service,
        photo_file_upload_service=photo_file_upload_service,
        image_file_service=image_file_service,
        user_api_service=user_api_service,
        kafka_service=kafka_service,
        thread_pool=thread_pool
    )
