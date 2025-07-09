from extensions import db
from utils.datetime_utils import utc_now

class AuditModel(db.Model):
    __abstract__ = True
    
    created_at = db.Column(db.DateTime, default=utc_now, nullable=False)
    updated_at = db.Column(db.DateTime, default=utc_now, onupdate=utc_now, nullable=False) 