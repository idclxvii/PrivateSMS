package ph.edu.sscrmnl.privatesms.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;

import ph.edu.sscrmnl.privatesms.R;
import ph.edu.sscrmnl.privatesms.adapter.SMSListAdapter;
import ph.edu.sscrmnl.privatesms.databasemodel.ModelContacts;
import ph.edu.sscrmnl.privatesms.databasemodel.ModelConversation;
import ph.edu.sscrmnl.privatesms.databasemodel.ModelSMS;
import ph.edu.sscrmnl.privatesms.databasemodel.Tables;
import ph.edu.sscrmnl.privatesms.receiver.OnDataUpdateListener;
import ph.edu.sscrmnl.privatesms.receiver.RefreshAdapterReceiver;
import ph.edu.sscrmnl.privatesms.util.SQLiteHelper;

public class SMSListActivity extends AppCompatActivity implements OnDataUpdateListener {

    private SQLiteHelper DB;
    private RefreshAdapterReceiver refreshReceiver;

    private ListView listview;
    private TextView hidden;
    private EditText search;
    private SMSListAdapter adapter;

    private synchronized SQLiteHelper getDb(Context context){
        DB = new SQLiteHelper(context);
        return this.DB;
    }



    private void refreshListView(){
        try{
            Object[] res = DB.selectAll(Tables.conversation, ModelConversation.class, null);
            adapter = new SMSListAdapter(this, R.layout.smslist_row,
                    // database query here

                    new ArrayList<ModelConversation>(
                            Arrays.asList(
                                    Arrays.copyOf(res, res.length,
                                            ModelConversation[].class))
                    )
            );
            listview.setAdapter(adapter);

            // Hold listview elements to invoke conversation deletion
            listview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

                final Dialog d = new Dialog(SMSListActivity.this);
                DisplayMetrics metrics = getResources().getDisplayMetrics();
                int width = metrics.widthPixels;
                int height = metrics.heightPixels;

                @Override
                public boolean onItemLongClick(final AdapterView<?> parent, View view, final int position, long id) {
                    d.setContentView(R.layout.confirmation_dialog);
                    final Button yes = (Button) d.findViewById(R.id.confirm);
                    yes.setText("Delete whole conversation");
                    final Button no = (Button) d.findViewById(R.id.cancel);
                    no.setText("Back");
                    d.setTitle(((ModelConversation) parent.getAdapter().getItem(position)).getAddress());

                    no.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            d.cancel();
                        }

                    });

                    yes.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            final Dialog dd = new Dialog(SMSListActivity.this);
                            dd.setContentView(R.layout.confirmation_dialog);
                            final Button yes = (Button) dd.findViewById(R.id.confirm);
                            final Button no = (Button) dd.findViewById(R.id.cancel);
                            dd.setTitle("Are you sure?");

                            no.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dd.cancel();

                                }
                            });

                            yes.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    // delete record in database


                                    try {
                                        Log.d("SMSListActivity", "Deleting whole conversation " +
                                                        "Address: " +(((ModelConversation) parent.getAdapter().getItem(position)).getAddress())
                                                        + " Contact: "
                                                        + ((ModelContacts) DB.select(Tables.contacts, ModelContacts.class,
                                                        new Object[][] {{"address", ((ModelConversation) parent.getAdapter().getItem(position)).getAddress()}}
                                                        , null)).getName()
                                        );

                                        if (DB.delete(Tables.sms,
                                                new ModelSMS(
                                                        ((ModelConversation) parent.getAdapter().getItem(position)).getId()
                                                )
                                                , null)) {
                                            Log.d("SMSListActivity", "Successfully deleted all SMS with conversation index "+
                                                    ((ModelConversation) parent.getAdapter().getItem(position)).getId());

                                            if (DB.delete(Tables.conversation,((ModelConversation) parent.getAdapter().getItem(position))
                                                    , null)) {
                                                Log.d("SMSListActivity", "Successfully deleted conversation with conversation index "+
                                                        ((ModelConversation) parent.getAdapter().getItem(position)).getId());


                                            }else{
                                                Log.d("SMSListActivity", "Failed to delete conversation with conversation index "+
                                                ((ModelConversation) parent.getAdapter().getItem(position)).getId());
                                            }
                                        } else {
                                            Log.d("SMSListActivity", "Failed to delete all SMS with conversation index "+
                                            ((ModelConversation) parent.getAdapter().getItem(position)).getId());
                                        }


                                    } catch (Exception e) {

                                    }

                                    dd.cancel();
                                    d.cancel();
                                    refreshListView();
                                }
                            });

                            dd.show();
                            dd.getWindow().setLayout((6 * width) / 7, LinearLayout.LayoutParams.WRAP_CONTENT);
                        }
                    });

                    d.show();
                    d.getWindow().setLayout((6 * width) / 7, LinearLayout.LayoutParams.WRAP_CONTENT);
                    return true;
                }
            });

            listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("args", (ModelConversation) parent.getAdapter().getItem(position));
                    Intent intent = new Intent(SMSListActivity.this, SMSActivity.class);
                    intent.putExtras(bundle);
                    startActivity(intent);

                }
            });

            if(adapter.getCount() > 0){
                listview.setVisibility(ListView.VISIBLE);
                hidden.setVisibility(TextView.GONE);

            }else{
                listview.setVisibility(ListView.GONE);
                hidden.setVisibility(TextView.VISIBLE);
            }
        }catch(Exception e){

        }

    }


    private void initializeLayout() {

        hidden = (TextView) findViewById(R.id.hiddenSMS);

        listview = (ListView) findViewById(R.id.listViewSMSList);
        refreshListView();

        search = (EditText) findViewById(R.id.txtSearchSMS);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search.setText("");
            }
        });
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smslist);

        DB = getDb(this);
        refreshReceiver = new RefreshAdapterReceiver(this);

        initializeLayout();

    }

    @Override
    public void onDataAvailable() {
        refreshListView();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (null != refreshReceiver) {
            registerReceiver(refreshReceiver, new IntentFilter(RefreshAdapterReceiver.REFRESH_ADAPTER));
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        DB = getDb(getApplicationContext());
        listview.requestFocus();
    }

    @Override
    public void onPause(){
        super.onPause();
        DB.closeConnection();
    }

    @Override
    public void onStop() {
        if (null != refreshReceiver) {
            unregisterReceiver(refreshReceiver);
        }
        super.onStop();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_smslist, menu);
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
