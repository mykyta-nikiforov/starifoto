from extensions import db

# Association tables for many-to-many relationships
photo_tags = db.Table('photo_tag',
    db.Column('photo_id', db.Integer, db.ForeignKey('photo_service.photo.id'), primary_key=True),
    db.Column('tag_id', db.Integer, db.ForeignKey('photo_service.tag.id'), primary_key=True),
    schema='photo_service'
)

photo_image_files = db.Table('photo_image_file',
    db.Column('photo_id', db.Integer, db.ForeignKey('photo_service.photo.id'), primary_key=True),
    db.Column('image_file_id', db.Integer, db.ForeignKey('photo_service.image_file.id'), primary_key=True),
    schema='photo_service'
) 