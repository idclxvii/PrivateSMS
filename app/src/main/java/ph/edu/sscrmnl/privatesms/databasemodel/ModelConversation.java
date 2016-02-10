package ph.edu.sscrmnl.privatesms.databasemodel;

import java.io.Serializable;

/**
 * Created by IDcLxViI on 2/7/2016.
 */
public class ModelConversation implements Serializable{

    // data structure
    private Integer index = null;
    private String address = null;
    private Integer count = null; // count of unread messages
    private Long lastMod = null; // last modified, either sent or received

    public enum fields{
        Id, Address, Count, LastMod
    }


    public String[] getFields(){
        String[] f = new String[fields.values().length];
        for(int x=0; x < f.length; x++ ){
            f[x] = fields.values()[x].toString();

        }
        return f;
    }

    public ModelConversation() {}

    public ModelConversation(Integer id) {
        this.index = id;
    }

    public ModelConversation(Integer id, String add) { this.index = id; this.address = add;}

    public ModelConversation(Integer id, String add, Integer num) { this.index = id; this.address = add; this.count = num; }

    public ModelConversation(String add, Integer num, Long datetime){
        this.address = add;
        this.count = num;
        this.lastMod = datetime;
    }

    public ModelConversation(Integer id, String add, Integer num, Long datetime) {
        this.index = id;
        this.address = add;
        this.count = num;
        this.lastMod = datetime ;
    }

    // setters
    public void setId(Integer id) { this.index = id; }

    public void setAddress(String add) { this.address = add; }

    public void setCount(Integer num) { this.count = num ; }

    public void setLastMod(Long datetime) { this.lastMod = datetime; }

    // getters
    public Integer getId() { return this.index; }

    public String getAddress() { return this.address; }

    public Integer getCount() { return this.count; }

    public Long getLastMod(){ return this.lastMod; }

}
