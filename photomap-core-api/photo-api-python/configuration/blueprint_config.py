import logging
from routes.photo_routes import create_photo_routes
from routes.map_routes import create_map_routes
from routes.comment_routes import create_comment_routes
from routes.user_photo_routes import create_user_photo_routes
from routes.license_routes import create_license_routes
from routes.geojson_routes import create_geojson_routes
from routes.tag_routes import create_tag_routes

logger = logging.getLogger(__name__)


def register_blueprints(app, container):
    blueprints = [
        (create_photo_routes, [container.photo_manager(), container.photo_validator()]),
        (create_map_routes, [container.map_data_service()]),
        (create_comment_routes, [container.comment_service(), container.comment_validator(), container.comment_mapper()]),
        (create_user_photo_routes, [container.user_photo_service()]),
        (create_license_routes, [container.license_service()]),
        (create_geojson_routes, [container.photo_service(), container.map_data_service()]),
        (create_tag_routes, [container.tag_service()])
    ]
    
    for blueprint_factory, dependencies in blueprints:
        try:
            blueprint = blueprint_factory(*dependencies)
            app.register_api(blueprint)
            logger.info(f"Registered blueprint: {blueprint.name}")
        except Exception as e:
            logger.error(f"Failed to register blueprint {blueprint_factory.__name__}: {str(e)}")
            raise 