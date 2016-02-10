package ph.edu.sscrmnl.privatesms.databasemodel;

/**
 * Created by IDcLxViI on 2/7/2016.
 */
public class ModelSMS {

    public static final Integer SMS_RECEIVED = 0;
    public static final Integer SMS_SENT = 1;
    public static final Integer SMS_STATUS_NOT_APPLICABLE = -1;
    public static final Integer SMS_STATUS_SEND_SUCCESS = 1;
    public static final Integer SMS_STATUS_SEND_FAILED = 2;


    // data structure
    private Integer id = null;
    private Integer conversation = null; // conversation id
    private String address = null; // sms originatingAddress
    private String body = null;  // sms contents
    private Integer type = null; // sms type, 0 = received; 1 = sent
    private Integer status = null; // sms sent type, -1 = n/a (means received) ; 1 = success; 2 = failed
    private Long datetime = null; // sms datetime


    public enum fields{
        Id, Conversation, Address, Body, Type, Status, Datetime
    }

    public String[] getFields(){
        String[] f = new String[fields.values().length];
        for(int x=0; x < f.length; x++ ){
            f[x] = fields.values()[x].toString();

        }
        return f;
    }

    public ModelSMS() {

    }

    public ModelSMS(Integer conv){ this.conversation = conv; }

    public ModelSMS(Integer conv, String add, String msg, Integer type,
                    Integer stat, Long date) {
        this.conversation = conv;
        this.address = add;
        this.body = msg;
        this.type = type;
        this.status = stat;
        this.datetime = date;
    }

    public ModelSMS(Integer index, Integer conv, String add, String msg, Integer type,
                    Integer stat, Long date) {
        this.id = index;
        this.conversation = conv;
        this.address = add;
        this.body = msg;
        this.type = type;
        this.status = stat;
        this.datetime = date;
    }

    // setters
    public  void setId (Integer index){ this.id = index; }

    public void setConversation(Integer conv) { this.conversation = conv;}

    public void setAddress(String add) { this.address = add; }

    public void setBody(String msg){ this.body = msg; }

    public void setType(Integer type){ this.type = type;  }

    public void setStatus(Integer stat){ this.status = stat; }

    public void setDatetime(Long date){ this.datetime = date; }


    // getters
    public Integer getId(){ return this.id; }

    public Integer getConversation() { return  this.conversation; }

    public String getAddress() { return this.address; }

    public String getBody(){ return  this.body; }

    public Integer getType(){ return  this.type; }

    public Integer getStatus(){ return  this.status; }

    public  Long getDatetime(){ return  this.datetime; }
}
