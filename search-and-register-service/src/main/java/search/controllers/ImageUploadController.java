package search.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import search.exceptions.ImageUploadException;
import search.model.Facility;
import search.model.FacilityOwner;
import search.model.Picture;
import search.service.persistence.FacilityOwnerService;
import search.service.persistence.FacilityService;
import search.service.persistence.PictureService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/imageUpload")
public class ImageUploadController {
    private static final int MAX_IMAGE_SIZE = 5 * 1048576;
    private static final int SIZE_IN_MB = 5;
    //    public static final String IMAGES_FOLDER = "C:" + File.separator + "images" + File.separator; //for windows
    public static final String IMAGES_FOLDER = "/opt/reservations/backend/images/";

    @Autowired
    private PictureService pictureService;
    @Autowired
    private FacilityService facilityService;
    @Autowired
    private FacilityOwnerService facilityOwnerService;

    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(method = RequestMethod.POST)
    public List<Picture> handleFileUpload(@RequestParam("id") int facilityId, @RequestParam("images") List<MultipartFile> images) throws ImageUploadException, IOException {
        Facility facility = facilityService.findById(facilityId);
        FacilityOwner owner = facilityOwnerService.findById(facility.getFacilityOwner().getId());
        String email = owner.getEmail();

        String filePath = IMAGES_FOLDER + email + File.separator + facility.getId();
        Iterator<MultipartFile> files = images.iterator();
        List<Picture> picturesForReturn = new ArrayList<>();

        while (files.hasNext()) {
            MultipartFile file = files.next();
            String fileName = saveFile(file, filePath);
            Picture picture = new Picture(filePath + File.separator + fileName);
            picturesForReturn.add(picture);
            picture.setFacility(facility);
            pictureService.save(picture);
            facility.addPicture(picture);
        }

        return picturesForReturn;
    }

    // Returns the name of file if it's successfully saved.
    public static String saveFile(MultipartFile file, String filePath) throws ImageUploadException, IOException {
        if (file.getContentType() != null && file.getContentType().startsWith("image")) {
            String fileName = file.getOriginalFilename();
            String extension = fileName.substring(fileName.lastIndexOf("."));

            if (file.getSize() >= MAX_IMAGE_SIZE) {
                throw new ImageUploadException("Image size must be " + SIZE_IN_MB + "MB or less.");
            }

            String uniqueName = fileName.substring(0, fileName.lastIndexOf(".")) + UUID.randomUUID().toString() + extension;
            Files.copy(file.getInputStream(), Paths.get(filePath, uniqueName));
            return uniqueName;

        } else {
            throw new ImageUploadException("Invalid file format. File must be an image.");
        }
    }
}
