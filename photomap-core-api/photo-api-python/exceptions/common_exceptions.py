class ValidationException(Exception):
    def __init__(self, message: str):
        self.message = message
        super().__init__(self.message)


class ResourceNotFoundException(Exception):
    def __init__(self, message: str):
        self.message = message
        super().__init__(self.message)


class UnauthorizedException(Exception):
    def __init__(self, message: str):
        super().__init__(message)


class ForbiddenException(Exception):
    def __init__(self, message: str):
        super().__init__(message)


class InternalException(Exception):
    def __init__(self, message: str):
        super().__init__(message)


class FilterChainException(Exception):
    def __init__(self, message: str, http_status: int):
        super().__init__(message)
        self.http_status = http_status 