package admin.service.persistance;

import admin.model.Picture;

import java.util.List;

/**
 * Created by Trayan_Muchev on 8/30/2016.
 */
public interface PictureService {

    Picture findOne(Integer id);

    List<Picture> findAll();

    void delete(Integer id);

    List<Picture> deleteList(List<Integer> picturesId);

    List<Picture> findByFacilityId(Integer id);


}
