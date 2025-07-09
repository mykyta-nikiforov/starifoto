import logging
from PIL import Image
from io import BytesIO
from constants.thumb_dimensions import ThumbDimensions

logger = logging.getLogger(__name__)

class ThumbnailGenerationService:
    
    THUMBNAIL_WIDTH = ThumbDimensions.WIDTH_IN_PIXELS.value
    THUMBNAIL_HEIGHT = ThumbDimensions.WIDTH_IN_PIXELS.value
    
    def __init__(self):
        pass
    
    def generate(self, image_data: bytes) -> bytes:
        logger.info("Generating thumbnail from image data")
        
        # Open image from bytes
        with Image.open(BytesIO(image_data)) as source_img:
            if source_img.mode != 'RGB':
                source_img = source_img.convert('RGB')
            
            original_width = source_img.width
            original_height = source_img.height
            target_width = self.THUMBNAIL_WIDTH
            target_height = self.THUMBNAIL_HEIGHT
            
            # Calculate scaling factor to maintain aspect ratio
            scale_factor = max(target_width / original_width, target_height / original_height)
            scaled_width = max(int(original_width * scale_factor), target_width)
            scaled_height = max(int(original_height * scale_factor), target_height)
            
            # Create scaled image
            scaled_img = source_img.resize((scaled_width, scaled_height), Image.Resampling.LANCZOS)
            
            # Crop to exact target size
            x = (scaled_width - target_width) // 2
            y = (scaled_height - target_height) // 2
            cropped_img = scaled_img.crop((x, y, x + target_width, y + target_height))
            
            thumbnail_bytes = BytesIO()
            cropped_img.save(thumbnail_bytes, format='JPEG', quality=85, optimize=True)
            
            logger.info(f"Thumbnail generated successfully. Size: {len(thumbnail_bytes.getvalue())} bytes")
            return thumbnail_bytes.getvalue()
