from sqlalchemy.orm import relationship
from extensions import db
from models.associations import photo_image_files
from models.audit_model import AuditModel

class ImageFile(AuditModel):
    __tablename__ = 'image_file'
    __table_args__ = {'schema': 'photo_service'}
    
    id = db.Column(db.Integer, primary_key=True)
    name = db.Column(db.String(255), nullable=False)
    url = db.Column(db.String(255), nullable=False)
    height = db.Column(db.SmallInteger, nullable=False)
    width = db.Column(db.SmallInteger, nullable=False)
    file_type = db.Column(db.String(20), nullable=False)
    image_phash = db.Column(db.String(255))  # For duplicate detection
    
    # Relationships
    photos = relationship("Photo", secondary=photo_image_files, back_populates="files")
    
 