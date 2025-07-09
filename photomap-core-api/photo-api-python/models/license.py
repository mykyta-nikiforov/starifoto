from sqlalchemy.orm import relationship
from extensions import db
from models.audit_model import AuditModel

class License(AuditModel):
    __tablename__ = 'license'
    __table_args__ = {'schema': 'photo_service'}
    
    id = db.Column(db.Integer, primary_key=True)
    name = db.Column(db.String(255), nullable=False)
    description = db.Column(db.Text)
    details_url = db.Column(db.String(255))
    sequence_number = db.Column(db.Integer)
    active = db.Column(db.Boolean, default=True, nullable=False)
    
    # Relationships
    photos = relationship("Photo", back_populates="license")
    
 