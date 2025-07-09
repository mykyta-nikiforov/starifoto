from typing import Generic, TypeVar, List, Dict, Any, Callable, Optional
from pydantic import BaseModel, Field

T = TypeVar('T')
U = TypeVar('U')


class Page(BaseModel, Generic[T]):
    """
    Python implementation of Spring Data Page interface
    A page is a sublist of a list of objects. It allows gain information about the position of it in the containing entire list.
    """
    
    content: List[T] = Field(description="The content of this page")
    page: int = Field(description="Current page number (0-based)")
    size: int = Field(description="Page size")
    totalElements: int = Field(description="Total number of elements")
    totalPages: int = Field(description="Total number of pages")
    first: bool = Field(description="Whether this is the first page")
    last: bool = Field(description="Whether this is the last page")
    numberOfElements: int = Field(description="Number of elements in current page")
    
    class Config:
        arbitrary_types_allowed = True
    
    @classmethod
    def empty(cls, page: int = 0, size: int = 20) -> 'Page[T]':
        return cls(
            content=[],
            page=page,
            size=size,
            totalElements=0,
            totalPages=0,
            first=True,
            last=True,
            numberOfElements=0
        )
    
    @classmethod
    def of(cls, content: List[T], page: int, size: int, total_elements: int) -> 'Page[T]':
        total_pages = (total_elements + size - 1) // size if size > 0 else 0
        
        return cls(
            content=content,
            page=page,
            size=size,
            totalElements=total_elements,
            totalPages=total_pages,
            first=page == 0,
            last=page >= total_pages - 1,
            numberOfElements=len(content)
        )
    
    def map(self, converter: Callable[[T], U]) -> 'Page[U]':
        """
        Returns a new Page with the content of the current one mapped by the given function
        """
        converted_content = [converter(item) for item in self.content]
        
        return Page[U](
            content=converted_content,
            page=self.page,
            size=self.size,
            totalElements=self.totalElements,
            totalPages=self.totalPages,
            first=self.first,
            last=self.last,
            numberOfElements=self.numberOfElements
        )