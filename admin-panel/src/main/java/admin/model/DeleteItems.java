package admin.model;

import java.util.List;

/**
 * Created by Trayan_Muchev on 10/25/2016.
 */
public class DeleteItems {

    private List<Integer> itemsId;

    public DeleteItems(List<Integer> itemsId) {
        this.itemsId = itemsId;
    }

    public DeleteItems() {
    }

    public List<Integer> getItemsId() {
        return itemsId;
    }

    public void setItemsId(List<Integer> itemsId) {
        this.itemsId = itemsId;
    }
}
