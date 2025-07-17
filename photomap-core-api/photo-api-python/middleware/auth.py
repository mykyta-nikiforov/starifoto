import jwt
from flask import request, jsonify, g
from functools import wraps
import os
from datetime import datetime, timezone
from exceptions.common_exceptions import UnauthorizedException
from dto.error_dto import GenericErrorResponse

JWT_SECRET = os.getenv('JWT_SECRET', 'photomap-secret')
JWT_ALGORITHM = 'HS512'
BEARER_PREFIX = 'Bearer '
ACCESS_TOKEN_COOKIE = 'accessToken'

ROLE_MODERATOR = 'ROLE_MODERATOR'
ROLE_ADMIN = 'ROLE_ADMIN'

MSG_TOKEN_INVALID_OR_EXPIRED = 'Failed to authenticate. Token is invalid or expired.'

def _create_auth_error_response(status_code: int, message: str) -> tuple:
    error_response = GenericErrorResponse(
        timestamp=datetime.now().isoformat(),
        status=status_code,
        error="Unauthorized",
        message=message,
        path=request.path if request else "/"
    )
    return jsonify(error_response.model_dump()), status_code

def _create_unauthorized_response(message: str) -> tuple:
    return _create_auth_error_response(401, message)

def _create_forbidden_response(message: str) -> tuple:
    return _create_auth_error_response(403, message)

def get_auth_token():
    # First try Authorization header
    auth_header = request.headers.get('Authorization')
    if auth_header and auth_header.startswith(BEARER_PREFIX):
        return auth_header[len(BEARER_PREFIX):]
    
    # Fall back to accessToken cookie
    access_token = request.cookies.get(ACCESS_TOKEN_COOKIE)
    if access_token:
        return access_token
    
    return None

def _validate_token_claims(payload):
    scope = payload.get('scope')
    exp = payload.get('exp')
    email = payload.get('sub')
    
    if not scope:
        raise UnauthorizedException('Token doesn\'t contain scope')
    if not email:
        raise UnauthorizedException('Token doesn\'t contain email')
    if exp and datetime.now(timezone.utc).timestamp() > exp:
        raise UnauthorizedException('Token is expired')

def validate_jwt_token(token):
    try:
        payload = jwt.decode(token, JWT_SECRET, algorithms=[JWT_ALGORITHM])
        _validate_token_claims(payload)
        return payload
        
    except jwt.ExpiredSignatureError:
        raise UnauthorizedException('Token has expired')
    except jwt.InvalidTokenError as e:
        raise UnauthorizedException(f'Token is invalid: {str(e)}')
    except Exception as e:
        raise UnauthorizedException(f'Token validation failed: {str(e)}')

def store_current_user(payload):
    g.current_user = {
        'id': payload.get('user_id'),
        'email': payload.get('sub'),
        'privileges': payload.get('privileges', [])
    }

def get_current_user():
    if not hasattr(g, 'current_user') or g.current_user is None:
        raise Exception('No authentication in context')
    return g.current_user

def _check_user_privilege(payload, required_privilege):
    if required_privilege:
        privileges = payload.get('privileges', [])
        if required_privilege not in privileges:
            return False
    return True

def is_moderator_or_admin(user=None):
    if user is None:
        user = get_current_user()
    
    privileges = user.get('privileges', [])
    return ROLE_MODERATOR in privileges or ROLE_ADMIN in privileges

def _authenticate_user(required_privilege=None):
    token = get_auth_token()
    
    if not token:
        return _create_unauthorized_response(MSG_TOKEN_INVALID_OR_EXPIRED)
    try:
        payload = validate_jwt_token(token)

        if not _check_user_privilege(payload, required_privilege):
            return _create_forbidden_response('Admin access required')
        
        store_current_user(payload)
        return None
        
    except UnauthorizedException as e:
        return _create_forbidden_response(str(e))
    except Exception as e:
        return _create_forbidden_response(MSG_TOKEN_INVALID_OR_EXPIRED)

def jwt_required(f):
    @wraps(f)
    def decorated(*args, **kwargs):
        error_response = _authenticate_user()
        if error_response:
            return error_response
        return f(*args, **kwargs)
    return decorated

def require_admin(f):
    @wraps(f)
    def decorated(*args, **kwargs):
        error_response = _authenticate_user(required_privilege=ROLE_ADMIN)
        if error_response:
            return error_response
        return f(*args, **kwargs)
    return decorated 