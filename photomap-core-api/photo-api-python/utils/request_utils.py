import json
from typing import Dict, Any, List, Optional, Union, Type
from flask import request
from werkzeug.datastructures import FileStorage
from exceptions.common_exceptions import ValidationException

class RequestParser:
    """Utility class for parsing common Flask request patterns"""
    
    @staticmethod
    def get_json_from_multipart(field_name: str) -> Dict[str, Any]:
        json_file = request.files.get(field_name)
        if not json_file:
            raise ValidationException(f"Required field '{field_name}' is missing")
        
        try:
            content = json_file.read().decode('utf-8')
            return json.loads(content)
        except (UnicodeDecodeError, json.JSONDecodeError) as e:
            raise ValidationException(f"Invalid JSON in field '{field_name}': {str(e)}")
    
    @staticmethod
    def get_files_list(field_name: str) -> List[FileStorage]:
        files = request.files.getlist(field_name)
        if not files or len(files) == 0:
            raise ValidationException(f"Required field '{field_name}' is missing or empty")
        return files
    
    @staticmethod
    def get_files_list_optional(field_name: str) -> List[FileStorage]:
        return request.files.getlist(field_name) or []
    
    @staticmethod
    def get_query_param(param_name: str, param_type: Type = str, default: Any = None, required: bool = False) -> Any:
        value = request.args.get(param_name)
        
        if value is None:
            if required:
                raise ValidationException(f"Required parameter '{param_name}' is missing")
            return default
        
        try:
            if param_type == bool:
                return RequestParser._parse_bool(value)
            elif param_type == int:
                return int(value)
            elif param_type == float:
                return float(value)
            elif param_type == str:
                return str(value)
            else:
                raise ValidationException(f"Unsupported parameter type: {param_type}")
        except (ValueError, TypeError) as e:
            raise ValidationException(f"Invalid {param_type.__name__} value for parameter '{param_name}': {value}")
    
    @staticmethod
    def _parse_bool(value: str) -> bool:
        if isinstance(value, bool):
            return value
        
        lower_value = str(value).lower()
        if lower_value in ('true', '1', 'yes', 'on'):
            return True
        elif lower_value in ('false', '0', 'no', 'off'):
            return False
        else:
            raise ValueError(f"Invalid boolean value: {value}")