package search.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import search.model.User;
import search.service.persistence.UserService;

import java.io.File;
import java.io.FileInputStream;

/**
 * Created by Trayan_Muchev on 8/30/2016.
 */
@Controller
@RequestMapping(value = "/profilePic")
public class ImageController {
    private UserService userService;

    @Autowired
    public ImageController(UserService userService) {
        this.userService = userService;
    }

    @ResponseBody
    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.IMAGE_JPEG_VALUE)
    public byte[] returnRequestedPicture(@PathVariable("id") int id) {
        User user = userService.findById(id);
        String path = user.getProfilePic();

        FileInputStream fileInputStream = null;
        File picture = new File(path);
        byte[] bFile = new byte[(int) picture.length()];
        if (picture != null) {
            try {
                fileInputStream = new FileInputStream(picture);
                fileInputStream.read(bFile);
                fileInputStream.close();

                return bFile;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
