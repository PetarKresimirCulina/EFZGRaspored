package hr.efzg.pculina.efzgraspored;

/**
 * Created by Petar-Kresimir Culina on 2/28/2016.
 */
public class ListModelGroups {

    private String groupName;
    private int group_id;
    private int parent_id;

    /***********
     * Set Methods
     ******************/

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public void setID(int group_id) {
        this.group_id = group_id;
    }

    public void setParentID(int parent_id) {
        this.parent_id = parent_id;
    }

    /***********
     * Get Methods
     ****************/

    public String getGroupName() {
        return this.groupName;
    }

    public int getID() {
        return this.group_id;
    }

    public int getParentID() {
        return this.parent_id;
    }
}
