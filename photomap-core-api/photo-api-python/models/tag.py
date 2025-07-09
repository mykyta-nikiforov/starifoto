from sqlalchemy.orm import relationship
from extensions import db
from models.associations import photo_tags
from models.audit_model import AuditModel

class Tag(AuditModel):
    __tablename__ = 'tag'
    __table_args__ = {'schema': 'photo_service'}
    
    id = db.Column(db.Integer, primary_key=True)
    name = db.Column(db.String(255), nullable=False, unique=True)
    
    # Relationships
    photos = relationship("Photo", secondary=photo_tags, back_populates="tags") 