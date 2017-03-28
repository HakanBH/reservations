package search.service.persistence;

import search.model.Picture;

import java.util.List;

/**
 * Created by Trayan_Muchev on 8/30/2016.
 */
public interface PictureService {

    void delete(Integer id);

    void delete(List<Picture> pictures);

    List<Picture> findByFacilityId(Integer id);

    Picture save(Picture picture);

    Picture findOne(Integer id);

    List<Picture> findAll();
}
