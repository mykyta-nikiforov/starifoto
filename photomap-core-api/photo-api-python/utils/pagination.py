from typing import TypeVar, List
from dto.page import Page

T = TypeVar('T')

def paginate_query_spring_format(query, page: int, size: int) -> Page[T]:
    """
    Paginate a SQLAlchemy query and return Page DTO
    
    Args:
        query: SQLAlchemy query object
        page: Page number (0-based)
        size: Number of items per page
    
    Returns:
        Page DTO with paginated results
    """
    offset = page * size
    total = query.count()
    items = query.offset(offset).limit(size).all()

    return Page.of(
        content=items,
        page=page,
        size=size,
        total_elements=total
    )