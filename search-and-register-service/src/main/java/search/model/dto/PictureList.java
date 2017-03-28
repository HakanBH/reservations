package search.model.dto;

import search.model.Picture;

import java.util.List;

/**
 * Created by Trayan_Muchev on 10/18/2016.
 */

public class PictureList {
    private List<Picture> pictures;

    public PictureList() {
    }

    public List<Picture> getPictures() {
        return pictures;
    }

    public void setPictures(List<Picture> pictures) {
        this.pictures = pictures;
    }
}
