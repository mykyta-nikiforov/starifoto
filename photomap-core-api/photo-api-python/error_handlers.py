from flask import jsonify, request, current_app
from pydantic import ValidationError
from dto.error_dto import GenericErrorResponse
from datetime import datetime
import logging

from exceptions.photo_exceptions import (
    SimilarPhotosExistException,
    FileUploadInternalException
)

from exceptions.common_exceptions import (
    FilterChainException,
    ForbiddenException,
    InternalException,
    ResourceNotFoundException,
    UnauthorizedException,
    ValidationException
)

logger = logging.getLogger(__name__)

def _create_error_response(status_code: int, error_name: str, message: str) -> GenericErrorResponse:
    return GenericErrorResponse(
        timestamp=datetime.now().isoformat(),
        status=status_code,
        error=error_name,
        message=message,
        path=request.path if request else "/"
    )

def register_error_handlers(app):

    @app.errorhandler(ValidationError)
    def handle_validation_error(e):
        errors = {}
        for error in e.errors():
            field = '.'.join(str(loc) for loc in error['loc'])
            errors[field] = error['msg']
        
        error_messages = [f"{field}: {msg}" for field, msg in errors.items()]
        message = "Validation failed: " + "; ".join(error_messages)
        error_response = _create_error_response(400, "Bad Request", message)
        
        logger.warning(f"ValidationError: {error_response.model_dump()}")
        return jsonify(error_response.model_dump()), error_response.status
    
    @app.errorhandler(ValidationException)
    def handle_validation_exception(e):
        error_response = _create_error_response(400, "Bad Request", str(e))
        
        logger.warning(f"ValidationException: {error_response.model_dump()}")
        return jsonify(error_response.model_dump()), error_response.status
    
    @app.errorhandler(ResourceNotFoundException)
    def handle_resource_not_found(e):
        error_response = _create_error_response(404, "Not Found", str(e))
        
        logger.warning(f"ResourceNotFoundException: {error_response.model_dump()}")
        return jsonify(error_response.model_dump()), error_response.status
    
    @app.errorhandler(UnauthorizedException)
    def handle_unauthorized_exception(e):
        error_response = _create_error_response(401, "Unauthorized", str(e))
        
        logger.warning(f"UnauthorizedException: {error_response.model_dump()}")
        return jsonify(error_response.model_dump()), error_response.status
    
    @app.errorhandler(ForbiddenException)
    def handle_forbidden_exception(e):
        error_response = _create_error_response(403, "Forbidden", str(e))
        
        logger.warning(f"ForbiddenException: {error_response.model_dump()}")
        return jsonify(error_response.model_dump()), error_response.status
    
    @app.errorhandler(SimilarPhotosExistException)
    def handle_similar_photos_exist(e):
        similar_photo_ids = [photo.id for photo in e.similar_photos] if e.similar_photos else []
        logger.info(f"SimilarPhotosExistException has been thrown. Similar photos ids: {similar_photo_ids}")

        # Return similar photos list directly in response body
        similar_photos_data = []
        for photo in e.similar_photos:
            similar_photos_data.append(photo.model_dump())
        
        return jsonify(similar_photos_data), 409
    
    @app.errorhandler(FileUploadInternalException)
    def handle_file_upload_internal(e):
        error_response = _create_error_response(500, "Internal Server Error", str(e))
        
        logger.error(f"FileUploadInternalException: {error_response.model_dump()}")
        return jsonify(error_response.model_dump()), error_response.status
    
    @app.errorhandler(InternalException)
    def handle_internal_exception(e):
        error_response = _create_error_response(500, "Internal Server Error", str(e))
        
        logger.error(f"InternalException: {error_response.model_dump()}")
        return jsonify(error_response.model_dump()), error_response.status
    
    @app.errorhandler(FilterChainException)
    def handle_filter_chain_exception(e):
        # Use the http_status from the exception
        status_code = getattr(e, 'http_status', 500)
        status_name = "Internal Server Error"
        if status_code == 400:
            status_name = "Bad Request"
        elif status_code == 401:
            status_name = "Unauthorized"
        elif status_code == 403:
            status_name = "Forbidden"
        elif status_code == 404:
            status_name = "Not Found"
        
        error_response = _create_error_response(status_code, status_name, str(e))
        
        logger.warning(f"FilterChainException: {error_response.model_dump()}")
        return jsonify(error_response.model_dump()), error_response.status
    
    @app.errorhandler(ValueError)
    def handle_value_error(e):
        error_response = _create_error_response(400, "Bad Request", str(e))
        
        logger.warning(f"ValueError: {error_response.model_dump()}")
        return jsonify(error_response.model_dump()), error_response.status
    
    @app.errorhandler(404)
    def handle_not_found(e):
        error_response = _create_error_response(404, "Not Found", "Resource not found")
        
        logger.warning(f"NotFound: {error_response.model_dump()}")
        return jsonify(error_response.model_dump()), error_response.status
    
    @app.errorhandler(403)
    def handle_forbidden(e):
        error_response = _create_error_response(403, "Forbidden", "Access denied")
        
        logger.warning(f"Forbidden: {error_response.model_dump()}")
        return jsonify(error_response.model_dump()), error_response.status
    
    @app.errorhandler(401)
    def handle_unauthorized(e):
        error_response = _create_error_response(401, "Unauthorized", "Authentication required")
        
        logger.warning(f"Unauthorized: {error_response.model_dump()}")
        return jsonify(error_response.model_dump()), error_response.status
    
    @app.errorhandler(500)
    def handle_internal_error(e):
        error_response = _create_error_response(500, "Internal Server Error", "An unexpected error occurred")
        
        logger.error(f"InternalServerError: {error_response.model_dump()}", exc_info=True)
        return jsonify(error_response.model_dump()), error_response.status

    @app.errorhandler(Exception)
    def handle_generic_exception(e):
        error_response = _create_error_response(500, "Internal Server Error", str(e))
        
        logger.error(f"Exception: {e.__class__.__name__}, {error_response.model_dump()}", exc_info=True)
        return jsonify(error_response.model_dump()), error_response.status 