from sqlalchemy.orm import relationship

from extensions import db
from models.associations import photo_tags, photo_image_files
from models.audit_model import AuditModel


class Photo(AuditModel):
    __tablename__ = 'photo'
    __table_args__ = {'schema': 'photo_service'}

    id = db.Column(db.Integer, primary_key=True)
    title = db.Column(db.String(255), nullable=False)
    description = db.Column(db.Text, nullable=False)
    author = db.Column(db.String(255))
    source = db.Column(db.Text, nullable=False)
    year_start = db.Column(db.SmallInteger, nullable=False)
    year_end = db.Column(db.SmallInteger, nullable=False)
    license_id = db.Column(db.Integer, db.ForeignKey('photo_service.license.id'), nullable=False)
    user_id = db.Column(db.Integer, nullable=False)
    latitude = db.Column(db.Float, nullable=False)
    longitude = db.Column(db.Float, nullable=False)
    is_approximate_location = db.Column(db.Boolean, default=False, nullable=False)

    # Relationships
    license = relationship("License", back_populates="photos")
    tags = relationship("Tag", secondary=photo_tags, back_populates="photos")
    files = relationship("ImageFile", secondary=photo_image_files, back_populates="photos")
    comments = relationship("Comment", back_populates="photo", cascade="all, delete-orphan")


