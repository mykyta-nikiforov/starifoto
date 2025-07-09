import logging
from flask import request
from flask_jwt_extended import JWTManager
from flask_openapi3 import OpenAPI, Info, Tag
from config import Config
from extensions import db, migrate
from error_handlers import register_error_handlers
from containers import Container
from configuration.logging_config import configure_logging
from configuration.blueprint_config import register_blueprints

logger = logging.getLogger(__name__)


def create_app():
    container = Container()
    
    info = Info(title="Photo API", version="1.0.0", description="API to get and manage photos")
    app = OpenAPI(__name__, info=info)
    app.config.from_object(Config)
    
    configure_logging(app)
    
    # Wire configuration to container
    container.config.from_dict(app.config)
    app.container = container
    
    # Initialize extensions
    db.init_app(app)
    migrate.init_app(app, db)
    
    jwt = JWTManager(app)
    
    # Define OpenAPI tags
    health_tag = Tag(name="health", description="Health check endpoints")
    root_tag = Tag(name="root", description="Root endpoints")
    docs_tag = Tag(name="docs", description="Documentation endpoints")
    
    @app.get('/health', tags=[health_tag])
    def health_check():
        return {'status': 'healthy', 'service': 'photo-api-python'}

    @app.get('/', tags=[root_tag])
    def root():
        """Root endpoint with API information"""
        return {
            'message': 'Photo API - Python Flask Implementation',
            'version': '1.0.0',
            'docs': '/openapi/swagger',
            'openapi': '/openapi/openapi.json',
        }
    
    register_blueprints(app, container)
    
    @app.get('/doc/photo-api/v3/api-docs', tags=[docs_tag])
    def openapi_json():
        return app.api_doc
    
    @app.before_request
    def log_request_info():
        if request.endpoint and request.endpoint != 'static':
            logger.debug(f"Request: {request.method} {request.path}")
            if app.config.get('LOG_LEVEL') == 'DEBUG':
                if request.is_json and request.json:
                    logger.debug(f"JSON payload: {request.json}")
                if request.form:
                    logger.debug(f"Form data: {dict(request.form)}")
    
    register_error_handlers(app)
    
    logger.info("Photo API application created successfully")
    return app


if __name__ == '__main__':
    app = create_app()
    
    debug_mode = Config.FLASK_ENV == 'development'
    port = Config.PORT
    host = Config.HOST
    
    logger.info(f"Starting Flask app in {'debug' if debug_mode else 'production'} mode on {host}:{port}")
    app.run(debug=debug_mode, host=host, port=port)