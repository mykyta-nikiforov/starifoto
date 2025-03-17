package ua.in.photomap.photoapi.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ua.in.photomap.photoapi.model.*;
import ua.in.photomap.photoapi.repository.ImageFileRepository;
import ua.in.photomap.photoapi.service.DataGenerationService;
import ua.in.photomap.photoapi.service.PhotoService;
import ua.in.photomap.photoapi.service.TagService;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DataGenerationServiceImpl implements DataGenerationService {

    private final PhotoService photoService;
    private final TagService tagService;
    private final ImageFileRepository imageFileRepository;

    private final Random random = new Random();

    List<String> ukrainianWords = List.of(
            "Київ", "Львів", "Дніпро", "Озеро", "Гори", "Річка", "Сонце", "Місяць", "Зорі", "Небо",
            "Ліс", "Море", "Пляж", "Квіти", "Сад", "Вулиця", "Міст", "Школа", "Вечір", "Ранок",
            "Обеліск", "Парк", "Скульптура", "Пам'ятник", "Фонтан", "Бібліотека", "Університет", "Кафе", "Ресторан", "Готель"
    );

    private static final String BASE_URL = "https://picsum.photos";

    @Override
    public void generateBulkPhotos(int numberOfPhotos) {
        List<Photo> photos = new ArrayList<>();
        for (int i = 0; i < numberOfPhotos; i++) {
            log.info("Generating a new photo. Index: {}", i);
            Photo photo = generatePhoto(); // This should only create and return the photo object, not save it
            photos.add(photo);
            if (photos.size() == 500) { // Check if the batch size has reached 500
                photoService.saveAll(photos); // Save all photos at once
                photos.clear(); // Clear the list for the next batch
            }
        }
        if (!photos.isEmpty()) {
            photoService.saveAll(photos); // Save remaining photos which didn't fill up the last batch
        }
    }

    public Photo generatePhoto() {
        Photo photo = new Photo();

        // Set title, description, and source with random or static data
        photo.setTitle(generateRandomTitle());
        photo.setDescription("Текст опису. " + generateRandomTitle());
        photo.setAuthor("Ім'я Прізвище + " + generateRandomTitle());
        photo.setSource("Текст джерела. " + generateRandomTitle());

        // Set random year range between 1852 and 2004
        short startYear = (short) (random.nextInt((2004 - 1852) + 1) + 1852);
        short endYear = (short) (random.nextInt((2004 - startYear) + 1) + startYear); // Ensure endYear is not before startYear
        photo.setYearRange(new YearRange(startYear, endYear));

        // Set a random license (assuming licenses with IDs 1-4 exist)
        Integer licenseId = 1 + random.nextInt(4);
        License license = new License();
        license.setId(licenseId);
        photo.setLicense(license);

        // Set fixed userId
        photo.setUserId(100000L);

        // Set random coordinates within Ukraine
        photo.setCoordinates(generateCoordinatesWithinUkraine());

        // Set tags (using existing ones or creating new ones)
        String[] tagNames = generateRandomTitle(random.nextInt(7) + 1).split(" ");
        Set<Tag> tags = tagService.getExistingTagsOrCreateNew(Arrays.asList(tagNames));
        photo.setTags(tags);

        ImageFile imageFile = createRandomImageFile();
        String[] parts = imageFile.getUrl().split("/");
        int imageId = Integer.parseInt(parts[parts.length - 3].replace("id", "").trim()); // Extracting the ID

        ImageFile thumbnailImageFile = createThumbnailFile(imageId);
        photo.setFiles(Set.of(imageFile, thumbnailImageFile));
        return photo;
    }

    public ImageFile createRandomImageFile() {
        // Choose a random image ID from the provided list
        int imageId = random.nextInt(60); // Assuming you have images 0-59 as per your list
        int width = 200 + random.nextInt(301); // Adjust if you want other dimensions
        int height = 200 + random.nextInt(301);

        // Construct the URL for a specific image
        String url = String.format("%s/id/%d/%d/%d", BASE_URL, imageId, width, height);

        ImageFile imageFile = new ImageFile();
        imageFile.setName("Photo_" + imageId + ".jpg");
        imageFile.setUrl(url);
        imageFile.setHeight(Integer.valueOf(height).shortValue());
        imageFile.setWidth(Integer.valueOf(width).shortValue());
        imageFile.setFileType(ImageFileType.ORIGINAL);
        return imageFileRepository.save(imageFile);
    }

    public ImageFile createThumbnailFile(int imageId) {
        // Using the same image ID for the thumbnail to keep the image consistent
        int thumbnailSize = 100; // Fixed size for thumbnail

        // Construct the URL for the thumbnail
        String url = String.format("%s/id/%d/%d", BASE_URL, imageId, thumbnailSize);

        // Create and return the Thumbnail PhotoFile object
        ImageFile thumbnailFile = new ImageFile();
        thumbnailFile.setName("Thumbnail_" + imageId + ".jpg");
        thumbnailFile.setUrl(url);
        thumbnailFile.setHeight((short) thumbnailSize);
        thumbnailFile.setWidth((short) thumbnailSize);
        thumbnailFile.setFileType(ImageFileType.THUMBNAIL);
        return imageFileRepository.save(thumbnailFile);
    }

    public String generateRandomTitle() {
        return generateRandomTitle(random.nextInt(3) + 1);
    }

    public String generateRandomTitle(Integer wordCount) {
        List<String> words = new ArrayList<>(ukrainianWords);
        Collections.shuffle(words, random);
        return words.stream()
                .limit(wordCount)
                .collect(Collectors.joining(" "));
    }

    public Coordinates generateCoordinatesWithinUkraine() {
        // Define the approximate latitude and longitude boundaries of Ukraine
        double minLatitude = 44.3833; // Slightly above the actual minimum to be safely within borders
        double maxLatitude = 51.9833; // Slightly below the actual maximum for the same reason
        double minLongitude = 22.1333; // Similarly, adjusted for the westernmost point
        double maxLongitude = 40.1833; // Adjusted for the easternmost point

        // Generate random coordinates within these bounds
        double latitude = minLatitude + (maxLatitude - minLatitude) * random.nextDouble();
        double longitude = minLongitude + (maxLongitude - minLongitude) * random.nextDouble();

        return new Coordinates(latitude, longitude, false);
    }
}
