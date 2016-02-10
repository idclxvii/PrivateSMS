package ph.edu.sscrmnl.privatesms.activity;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;

import ph.edu.sscrmnl.privatesms.R;
import ph.edu.sscrmnl.privatesms.adapter.SMSAdapter;
import ph.edu.sscrmnl.privatesms.databasemodel.ModelConversation;
import ph.edu.sscrmnl.privatesms.databasemodel.ModelSMS;
import ph.edu.sscrmnl.privatesms.databasemodel.Tables;
import ph.edu.sscrmnl.privatesms.receiver.OnDataUpdateListener;
import ph.edu.sscrmnl.privatesms.receiver.RefreshAdapterReceiver;
import ph.edu.sscrmnl.privatesms.util.SQLiteHelper;

public class SMSActivity extends AppCompatActivity implements OnDataUpdateListener {
    private final String TAG = "SMSActivity";

    private SQLiteHelper DB;
    private RefreshAdapterReceiver refreshReceiver;

    private ListView listview;
    private TextView hidden;
    private EditText inputSMS;
    private Button sendSMS;

    private SMSAdapter adapter;

    private  ModelConversation c;


    private synchronized SQLiteHelper getDb(Context context){
        DB = new SQLiteHelper(context);
        return this.DB;
    }

    private void refreshListView() {
        try {
            Object[] res = DB.selectConditional(Tables.sms, ModelSMS.class,
                    new Object[][] {{"conversation", c.getId()}}, null);
            adapter = new SMSAdapter(this, R.layout.sms_received_row,
                    // database query here

                    new ArrayList<ModelSMS>(
                            Arrays.asList(
                                    Arrays.copyOf(res, res.length,
                                            ModelSMS[].class))
                    )
            );
            listview.setAdapter(adapter);

            // Hold listview elements to invoke conversation deletion


        } catch (Exception e) {}


        try {
            //  public ModelConversation(Integer id, String add, Integer num, Long datetime)

            // requery
            ModelConversation cc = (ModelConversation) DB.select(Tables.conversation, ModelConversation.class,
                    new Object[][] { {"id" , c.getId()} }
                    ,null);
            if(DB.update(Tables.conversation, cc,
                    new ModelConversation(cc.getId(), cc.getAddress(), 0,
                            cc.getLastMod()), null)){
                Log.i(TAG, "Conversation successfully updated");

               // might invoke infinite looping / broadcast; it's a TRAAAAAP !
                sendBroadcast(new Intent(RefreshAdapterReceiver.REFRESH_CONVERSATIONS_ADAPTER));
            }else{
                Log.i(TAG, "Conversation FAILED to update");
            }

        }catch(Exception e){}

        listview.setSelection(listview.getCount() - 1);
    }

    private void initializeLayout(){

        hidden = (TextView) findViewById(R.id.hiddenSMS);

        inputSMS = (EditText) findViewById( R.id.txtSMS);

        sendSMS = (Button) findViewById(R.id.btnSendSMS);
        // put send sms action listener here later

        sendSMS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneNo = c.getAddress();
                String sms = inputSMS.getText().toString();

                try {
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(phoneNo, null, sms, null, null);

                    // save to database: successfully sent

                    try{
                        DB.insert(Tables.sms,
                                new ModelSMS(c.getId(), c.getAddress(), sms, ModelSMS.SMS_SENT,
                                        ModelSMS.SMS_STATUS_SEND_SUCCESS, (Long) System.currentTimeMillis()), null);
                    }catch(Exception e){ e.printStackTrace();}

                } catch (Exception e) {
                    // save to database: failed to send
                    try{
                        DB.insert(Tables.sms,
                                new ModelSMS(c.getId(), c.getAddress(), sms, ModelSMS.SMS_SENT,
                                        ModelSMS.SMS_STATUS_SEND_FAILED, (Long) System.currentTimeMillis()), null);
                    }catch(Exception ee){ ee.printStackTrace();}

                    e.printStackTrace();
                }

                // save the last mod
                try {
                    //  public ModelConversation(Integer id, String add, Integer num, Long datetime)
                    if(DB.update(Tables.conversation, c,
                            new ModelConversation(c.getId(), c.getAddress(), 0,
                                    (Long) System.currentTimeMillis()), null)){
                        Log.i(TAG, "Conversation successfully updated");
                        sendBroadcast(new Intent(RefreshAdapterReceiver.REFRESH_ALL_ADAPTERS));
                    }else{
                        Log.i(TAG, "Conversation FAILED to update");
                    }

                }catch(Exception e){}

                inputSMS.setText("");
                refreshListView();
            }
        });

        listview = (ListView) findViewById(R.id.listViewSMS);
        refreshListView();


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms);

        DB = getDb(this);
        refreshReceiver = new RefreshAdapterReceiver(this);

        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();
        c = (ModelConversation)bundle.getSerializable("args");

        // initialize layouts
        initializeLayout();

        // finally, update the counts in conversation
        try {
            //  public ModelConversation(Integer id, String add, Integer num, Long datetime)
            if(DB.update(Tables.conversation, c,
                    new ModelConversation(c.getId(), c.getAddress(), 0,
                            c.getLastMod()), null)){
                Log.i(TAG, "Conversation successfully updated");
                sendBroadcast(new Intent(RefreshAdapterReceiver.REFRESH_ALL_ADAPTERS));
            }else{
                Log.i(TAG, "Conversation FAILED to update");
            }

        }catch(Exception e){}
    }

    @Override
    public void onDataAvailable() {
        refreshListView();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (null != refreshReceiver) {
            registerReceiver(refreshReceiver, new IntentFilter(RefreshAdapterReceiver.REFRESH_ALL_ADAPTERS));
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        DB = getDb(getApplicationContext());
    }

    @Override
    public void onPause(){
        super.onPause();
        DB.closeConnection();
    }

    @Override
    public void onStop() {
        /*
        if (null != refreshReceiver) {
            unregisterReceiver(refreshReceiver);
        }
        */
        super.onStop();

    }

    @Override
    public void onDestroy(){
        if (null != refreshReceiver) {
            unregisterReceiver(refreshReceiver);
        }
        super.onDestroy();

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_sm, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
