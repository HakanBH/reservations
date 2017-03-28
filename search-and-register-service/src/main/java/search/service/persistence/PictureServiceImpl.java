package search.service.persistence;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import search.model.Picture;
import search.repository.PictureRepository;

import java.io.File;
import java.util.List;

/**
 * Created by Trayan_Muchev on 8/30/2016.
 */
@Service("pictureService")
@Transactional
public class PictureServiceImpl implements PictureService {

    @Autowired
    private PictureRepository pictureRepository;

    @Override
    public void delete(Integer id) {
        Picture picture = pictureRepository.findOne(id);
        File file = new File(picture.getPath());
        file.delete();
        pictureRepository.delete(id);
    }

    @Override
    public void delete(List<Picture> pictures) {
        pictureRepository.delete(pictures);
    }

    @Override
    public List<Picture> findByFacilityId(Integer id) {
        return pictureRepository.findByFacilityId(id);
    }

    @Override
    public Picture save(Picture picture) {
        return pictureRepository.save(picture);
    }

    @Override
    public Picture findOne(Integer id) {
        return pictureRepository.findOne(id);
    }

    @Override
    public List<Picture> findAll() {
        return (List<Picture>) pictureRepository.findAll();
    }
}
