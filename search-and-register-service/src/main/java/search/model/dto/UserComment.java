package search.model.dto;

/**
 * Created by Toncho_Petrov on 8/30/2016.
 */
public class UserComment {
    private String text;
    private Integer user_id;

    public UserComment() {
    }

    public UserComment(String text, Integer user_id) {
        this.text = text;
        this.user_id = user_id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Integer getUser_id() {
        return user_id;
    }

    public void setUser_id(Integer user_id) {
        this.user_id = user_id;
    }
}
