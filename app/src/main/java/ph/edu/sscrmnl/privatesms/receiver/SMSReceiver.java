package ph.edu.sscrmnl.privatesms.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import ph.edu.sscrmnl.privatesms.databasemodel.ModelContacts;
import ph.edu.sscrmnl.privatesms.databasemodel.ModelConversation;
import ph.edu.sscrmnl.privatesms.databasemodel.ModelSMS;
import ph.edu.sscrmnl.privatesms.databasemodel.Tables;
import ph.edu.sscrmnl.privatesms.util.SQLiteHelper;

/**
 * Created by IDcLxViI on 11/26/2015.
 */
public class SMSReceiver extends BroadcastReceiver {

    private final String TAG = "SMS BroadcastReceiver";
    private static String sender = "";


    private SQLiteHelper DB;

    private synchronized SQLiteHelper getDb(Context context){
        DB = new SQLiteHelper(context);
        return this.DB;
    }


    @Override
    public void onReceive(Context context, Intent intent) {

        DB = getDb(context);
        //if you have registered your class for multiple filters you can check
        //for which filter it is called like this


        if (intent.getAction().compareTo("android.provider.Telephony.SMS_RECEIVED") == 0) {
            Bundle pdusBundle = intent.getExtras();
            Object[] pdus = (Object[]) pdusBundle.get("pdus");
            SmsMessage sms = SmsMessage.createFromPdu((byte[])pdus[0]);
            Long currentDatetime = System.currentTimeMillis();
            // reformat the sender's number if the number has +639xx format  => 09xx
            String sender =  (sms.getOriginatingAddress().contains("+") ?
                    new String( "0" +
                            sms.getOriginatingAddress().substring(3, sms.getOriginatingAddress().length()))
                    //.replace
                    //sms.getOriginatingAddress().substring(2, sms)
                    :
                    sms.getOriginatingAddress()
            );

            try{
                if( ((ModelContacts) DB.select(Tables.contacts, ModelContacts.class,
                        new Object[][]{ {"address", sender} } ,null)).getAddress() != null){

                    // immediately abort broadcasting that a message has been received
                    // if the originating address is on our app's database
                    abortBroadcast();

                    Map<String, String> msg =  RetrieveMessages(intent);
                    Date d = new Date();
                    String  smsBody = "";

                    for(String s : msg.keySet()) {
                        Object[] fullmsg = msg.values().toArray();
                        for (int i=0; i< fullmsg.length; i++){
                            //sms[i] = SmsMessage.createFromPdu((byte[])pdus[i]);
                            smsBody +=  fullmsg[i].toString();//.getMessageBody().toString();
                        }

                    }
                    Log.i(TAG, "Full message received:\n" + smsBody);

                    ModelConversation conversation = (ModelConversation) DB.select(Tables.conversation, ModelConversation.class,
                            new Object[][] { {"address", sender} }, null);

                    // check if conversation exists
                    if(conversation.getId() != null){

                        // a conversation with this sender already exists
                        Log.i(TAG, sender + " Conversation thread index:\n" + conversation.getId());

                        // update the conversation's count and lastmod

                        /*
                        Log.i(TAG, "Conversation's value before update: " +
                                        "id=" + conversation.getId() +
                                        "address=" + conversation.getAddress() +
                                        "count=" + conversation.getCount() +
                                        "lastmod=" + conversation.getLastMod() +
                                        " " + new SimpleDateFormat("EEE MMM dd yyyy hh:mm aaa", Locale.getDefault()).format(conversation.getLastMod().longValue())
                        );
                        */
                        if(DB.update(Tables.conversation,conversation,
                                new ModelConversation(conversation.getId(), conversation.getAddress(),
                                        conversation.getCount() + 1 , (Long) currentDatetime),
                                null)){
                            conversation = (ModelConversation) DB.select(Tables.conversation, ModelConversation.class,
                                    new Object[][] { {"address", sender} }, null);
                            /*
                            Log.i(TAG, "Conversation's value after update: " +
                                            "id=" + conversation.getId() +
                                            "address=" + conversation.getAddress() +
                                            "count=" + conversation.getCount() +
                                            "lastmod=" + conversation.getLastMod() +
                                            " " + new SimpleDateFormat("EEE MMM dd yyyy hh:mm aaa", Locale.getDefault()).format(conversation.getLastMod().longValue())
                            );
                            */

                        }else{
                            // Updating wont fail most probably but will be leaving this Log here
                            // just in case some unexpected event arise
                            Log.i(TAG, "Updating conversation failed!");
                        }

                    }else{
                        // no coversation with this sender exists. create one
                        Log.i(TAG, sender + " HAS NO conversation thread index yet");
                        DB.insert(Tables.conversation,
                                new ModelConversation(sender, 1, (Long) currentDatetime)
                                , null);
                        conversation = (ModelConversation) DB.select(Tables.conversation, ModelConversation.class,
                                new Object[][] { {"address", sender} }, null);
                        Log.i(TAG, sender + " Created conversation thread index: " + conversation.getId());

                    }

                    // save this sms to database
                    saveMessage(conversation, smsBody);

                    // send refresh broadcast on adapters
                    Log.i(TAG, "Broadcasting REFRESH_ADAPTERS");
                    context.sendBroadcast(new Intent(RefreshAdapterReceiver.REFRESH_ALL_ADAPTERS));

                }else {

                    Log.i(TAG, "Message received from " + sms.getOriginatingAddress() + " but skipping it ...");

                }

            }catch (Exception e){}


            //Object[] fullmsg =  msg.values().toArray();
            //String str = "";

        }






    }

    public void saveMessage(ModelConversation conversation, String smsBody) {

        // save whole sms to database
        /**
         * public ModelSMS(Integer conv, String add, String msg, Integer type,
         Integer stat, Long date)
         */
        try{
            if(DB.insert(Tables.sms, new ModelSMS(conversation.getId(),
                    conversation.getAddress(), smsBody, ModelSMS.SMS_RECEIVED,
                    ModelSMS.SMS_STATUS_NOT_APPLICABLE, conversation.getLastMod())
                    ,null)){
                Log.i(TAG, "SUCCESSFULLY SAVED SMS RECEIVED TO DATABASE!");

                /*
                Log.i(TAG, "Enumerating all SMS");
                Object[] test = DB.selectAll(Tables.sms, ModelSMS.class, null);
                for (Object o: test) {
                    Log.i(TAG, "Start");
                    Log.i(TAG, "SMS Index: " +  ((ModelSMS) o).getId() );
                    Log.i(TAG, "Conversation Index: " +  ((ModelSMS) o).getConversation() );
                    Log.i(TAG,(((ModelSMS) o).getType() == ModelSMS.SMS_RECEIVED ? "Sender: " : "Receiver: ")
                            +  ((ModelSMS) o).getAddress() );
                    Log.i(TAG, "Date: " +
                            new SimpleDateFormat("EEE MMM dd yyyy hh:mm aaa",
                                    Locale.getDefault()).format(((ModelSMS) o).getDatetime().longValue()));
                    Log.i(TAG, "Contents: " +  ((ModelSMS) o).getBody() );
                    Log.i(TAG, "Status: " +  ((ModelSMS) o).getStatus() );
                    Log.i(TAG, "End");


                }
                 */
            }else{
                Log.i(TAG, "FAILED TO SAVE SMS RECEIVED TO DATABASE!");
            }
        }catch(Exception e){}

    }

    private static Map<String, String> RetrieveMessages(Intent intent) {
        Map<String, String> msg = null;
        SmsMessage[] msgs;
        Bundle bundle = intent.getExtras();

        if (bundle != null && bundle.containsKey("pdus")) {
            Object[] pdus = (Object[]) bundle.get("pdus");

            if (pdus != null) {
                int nbrOfpdus = pdus.length;
                msg = new HashMap<String, String>(nbrOfpdus);
                msgs = new SmsMessage[nbrOfpdus];

                // There can be multiple SMS from multiple senders, there can be a maximum of nbrOfpdus different senders
                // However, send long SMS of same sender in one message
                for (int i = 0; i < nbrOfpdus; i++) {
                    msgs[i] = SmsMessage.createFromPdu((byte[])pdus[i]);

                    String originatinAddress = msgs[i].getOriginatingAddress();

                    // Check if index with number exists
                    if (!msg.containsKey(originatinAddress)) {
                        // Index with number doesn't exist
                        // Save string into associative array with sender number as index
                        msg.put(msgs[i].getOriginatingAddress(), msgs[i].getMessageBody());

                    } else {
                        // Number has been there, add content but consider that
                        // msg.get(originatinAddress) already contains sms:sndrNbr:previousparts of SMS,
                        // so just add the part of the current PDU
                        String previousparts = msg.get(originatinAddress);
                        String msgString = previousparts + msgs[i].getMessageBody();
                        msg.put(originatinAddress, msgString);
                    }
                }
            }
        }

        return msg;
    }
}
