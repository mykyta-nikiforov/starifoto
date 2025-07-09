import logging
from typing import List, Optional

from sqlalchemy import desc
from sqlalchemy.orm import joinedload

from dto.page import Page
from dto.photo_sitemap_data_dto import PhotoSitemapDataDTO
from extensions import db
from models.associations import photo_image_files
from models.image_file import ImageFile
from models.photo import Photo
from utils.pagination import paginate_query_spring_format
from constants.image_file_type import ImageFileType

logger = logging.getLogger(__name__)


class PhotoRepository:

    def find_by_id_with_relations(self, photo_id: int) -> Optional[Photo]:
        return db.session.query(Photo).options(
            joinedload(Photo.tags),
            joinedload(Photo.files),
            joinedload(Photo.license)
        ).filter(Photo.id == photo_id).first()

    def save(self, photo: Photo) -> Photo:
        try:
            db.session.add(photo)
            db.session.commit()
            db.session.refresh(photo)
            return photo
        except Exception as e:
            db.session.rollback()
            logger.error(f"Error saving photo: {str(e)}")
            raise

    def save_all(self, photos: List[Photo]):
        try:
            db.session.add_all(photos)
            db.session.commit()
        except Exception as e:
            db.session.rollback()
            logger.error(f"Error saving photos: {str(e)}")
            raise

    def delete_by_id(self, photo_id: int):
        try:
            photo = db.session.query(Photo).filter(Photo.id == photo_id).first()
            if photo:
                db.session.delete(photo)
                db.session.commit()
        except Exception as e:
            db.session.rollback()
            logger.error(f"Error deleting photo: {str(e)}")
            raise

    def find_all_sitemap_data_page(self, page: int, size: int) -> Page[PhotoSitemapDataDTO]:
        query = db.session.query(
            Photo.id,
            Photo.updated_at,
            ImageFile.url
        ).outerjoin(
            photo_image_files,
            Photo.id == photo_image_files.c.photo_id
        ).outerjoin(
            ImageFile,
            (photo_image_files.c.image_file_id == ImageFile.id) & (ImageFile.file_type == ImageFileType.ORIGINAL.value)
        ).order_by(desc(Photo.created_at))

        raw_page = paginate_query_spring_format(query, page, size)

        sitemap_items = []
        for row in raw_page.content:
            sitemap_dto = PhotoSitemapDataDTO(
                id=row.id,
                updatedAt=row.updated_at.isoformat() if row.updated_at else '',
                url=row.url or ''
            )
            sitemap_items.append(sitemap_dto)

        return Page.of(
            content=sitemap_items,
            page=page,
            size=size,
            total_elements=raw_page.totalElements
        )

    def find_similar_photos_by_hash(self, p_hash: str) -> List[int]:
        from models.associations import photo_image_files

        similar_photo_ids = db.session.query(photo_image_files.c.photo_id).join(
            ImageFile, ImageFile.id == photo_image_files.c.image_file_id
        ).filter(
            ImageFile.image_phash == p_hash,
            ImageFile.file_type == ImageFileType.ORIGINAL.value  # Only check original files
        ).distinct().all()

        return [row.photo_id for row in similar_photo_ids]

    def exists_by_id_and_user_id(self, photo_id: int, user_id: int) -> bool:
        photo = db.session.query(Photo).filter(
            Photo.id == photo_id,
            Photo.user_id == user_id
        ).first()
        return photo is not None

    def find_by_ids_with_files(self, photo_ids: List[int]) -> List[Photo]:
        return db.session.query(Photo).options(
            joinedload(Photo.files)
        ).filter(Photo.id.in_(photo_ids)).all()

    def find_all_with_relations_paginated(self, page: int, size: int) -> Page[Photo]:
        photos_query = db.session.query(Photo).options(
            joinedload(Photo.tags),
            joinedload(Photo.files),
            joinedload(Photo.license)
        ).order_by(desc(Photo.created_at))

        return paginate_query_spring_format(photos_query, page, size)

    def find_all_by_ids_with_relations(self, photo_ids: List[int]) -> List[Photo]:
        photos = db.session.query(Photo).options(
            joinedload(Photo.tags),
            joinedload(Photo.files),
            joinedload(Photo.license)
        ).filter(Photo.id.in_(photo_ids)).all()

        photo_dict = {photo.id: photo for photo in photos}
        ordered_photos = [photo_dict[pid] for pid in photo_ids if pid in photo_dict]

        return ordered_photos

    def find_all_by_user_id_with_relations_paginated(self, user_id: int, page: int, size: int) -> Page[Photo]:
        photos_query = db.session.query(Photo).options(
            joinedload(Photo.tags),
            joinedload(Photo.files),
            joinedload(Photo.license)
        ).filter(Photo.user_id == user_id).order_by(desc(Photo.created_at))

        return paginate_query_spring_format(photos_query, page, size)

    def find_all_for_geojson_paginated(self, page: int, size: int) -> Page[Photo]:
        photos_query = db.session.query(Photo).options(
            joinedload(Photo.tags),
            joinedload(Photo.files)
        ).order_by(desc(Photo.created_at))

        return paginate_query_spring_format(photos_query, page, size)
