from typing import List, Optional
from models.license import License
from extensions import db
import logging

logger = logging.getLogger(__name__)

class LicenseRepository:    
    def find_all_by_active_true(self) -> List[License]:
        with db.session.no_autoflush:
            return db.session.query(License).filter(License.active == True).all()
    
    def find_by_id(self, license_id: int) -> Optional[License]:
        with db.session.no_autoflush:
            return db.session.query(License).filter(License.id == license_id).first() 