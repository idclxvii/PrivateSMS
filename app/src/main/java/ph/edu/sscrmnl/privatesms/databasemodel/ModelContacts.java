package ph.edu.sscrmnl.privatesms.databasemodel;

/**
 * Created by IDcLxViI on 2/7/2016.
 */
public class ModelContacts {

    // data structure
    private String address = null;
    private String name = null;

    public enum fields{
        Address, Name
    }


    public String[] getFields(){
        String[] f = new String[fields.values().length];
        for(int x=0; x < f.length; x++ ){
            f[x] = fields.values()[x].toString();

        }
        return f;
    }

    public ModelContacts() {

    }

    public ModelContacts(String add) {
        this.address = add;

    }

    public ModelContacts(String add, String contact) {
        this.address = add;
        this.name = contact;

    }

    // setters
    public void setAddress(String add) {
        this.address = add;
    }

    public  void setName(String contact) {
        this.name = contact;
    }

    // getters
    public String getAddress() {
        return this.address;
    }

    public  String getName(){
        return  this.name;
    }

}


