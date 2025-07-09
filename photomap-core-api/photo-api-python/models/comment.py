from sqlalchemy.orm import relationship
from extensions import db
from models.audit_model import AuditModel

class Comment(AuditModel):
    __tablename__ = 'comment'
    __table_args__ = {'schema': 'photo_service'}
    
    id = db.Column(db.Integer, primary_key=True)
    text = db.Column(db.Text, nullable=False)
    user_id = db.Column(db.Integer, nullable=False)
    photo_id = db.Column(db.Integer, db.ForeignKey('photo_service.photo.id'), nullable=False)
    
    # Relationships
    photo = relationship("Photo", back_populates="comments")
    
 