package search.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import search.model.Picture;
import search.model.dto.PictureList;
import search.service.persistence.FacilityService;
import search.service.persistence.PictureService;

import java.util.List;

/**
 * Created by Trayan_Muchev on 10/18/2016.
 */
@RestController
@RequestMapping("/picturesDelete")
public class DeletePicturesController {
    @Autowired
    PictureService pictureService;
    @Autowired
    FacilityService facilityService;

    @RequestMapping(value = "/facility/{id}", method = RequestMethod.POST)
    public void refreshPictures(@PathVariable("id") Integer id, @RequestBody PictureList images) {
        List<Picture> toDelete = pictureService.findByFacilityId(id);
        toDelete.removeAll(images.getPictures());
        pictureService.delete(toDelete);
    }
}
