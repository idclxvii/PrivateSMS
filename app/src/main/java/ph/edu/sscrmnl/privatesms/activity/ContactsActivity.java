package ph.edu.sscrmnl.privatesms.activity;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
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
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;

import ph.edu.sscrmnl.privatesms.R;
import ph.edu.sscrmnl.privatesms.adapter.ContactsAdapter;
import ph.edu.sscrmnl.privatesms.databasemodel.ModelContacts;
import ph.edu.sscrmnl.privatesms.databasemodel.Tables;
import ph.edu.sscrmnl.privatesms.util.SQLiteHelper;

public class ContactsActivity extends GlobalBaseActivity {

    private SQLiteHelper DB;

    private ListView listview;
    private TextView addNew, hidden;
    private EditText search;
    private ContactsAdapter adapter;

    private synchronized SQLiteHelper getDb(Context context){
        DB = new SQLiteHelper(context);
        return this.DB;
    }



    private void refreshListView(){

        try{
            Object[] res = DB.selectAll(Tables.contacts, ModelContacts.class, null);

            adapter = new ContactsAdapter(this, android.R.layout.simple_list_item_1,
                    // database query here
                    new ArrayList<ModelContacts>(
                                    Arrays.asList(
                                            Arrays.copyOf(res, res.length,
                                                    ModelContacts[].class))
                                    )
                    );
            // sort adapter contents
            adapter.sortAscending();
            listview.setAdapter(adapter);

            listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                final Dialog d = new Dialog(ContactsActivity.this);
                DisplayMetrics metrics = getResources().getDisplayMetrics();
                int width = metrics.widthPixels;
                int height = metrics.heightPixels;

                // callbacks
                View.OnClickListener positiveDefault;
                View.OnClickListener negativeDefault;

                public void onItemClick(final AdapterView<?> parent, View view, final int position, long id) {

                    d.setContentView(R.layout.contacts_dialog);
                    final EditText name = (EditText) d.findViewById(R.id.txtName);
                    final EditText number = (EditText) d.findViewById(R.id.txtNumber);
                    final Button positive = (Button) d.findViewById(R.id.btnPositive);
                    final Button negative = (Button) d.findViewById(R.id.btnNegative);

                    negativeDefault = new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            final Dialog dd = new Dialog(ContactsActivity.this);
                            dd.setContentView(R.layout.confirmation_dialog);
                            final Button yes = (Button) dd.findViewById(R.id.confirm);
                            final Button no = (Button) dd.findViewById(R.id.cancel);
                            dd.setTitle("Confirm Deletion");

                            no.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dd.cancel();
                                    positive.setText("Edit");
                                    positive.setOnClickListener(positiveDefault);

                                    negative.setText("Delete");
                                    negative.setOnClickListener(negativeDefault);
                                }
                            });

                            yes.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    // delete record in database

                                    Log.d("ContactsActivity", "Deleting record " +
                                                    (((ModelContacts) parent.getAdapter().getItem(position)).getName())
                                                    + " "
                                                    + (((ModelContacts) parent.getAdapter().getItem(position)).getAddress())
                                    );
                                    try {


                                        if (DB.delete(Tables.contacts,
                                                new ModelContacts(
                                                        ((ModelContacts) parent.getAdapter().getItem(position)).getAddress(),
                                                        ((ModelContacts) parent.getAdapter().getItem(position)).getName()

                                                )
                                                , null)) {
                                            Log.d("ContactsActivity", "Successfully deleted record");
                                        } else {
                                            Log.d("ContactsActivity", "Failed to delete record");
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
                    };

                    positiveDefault = new View.OnClickListener() {
                        final View.OnClickListener positiveEdit = new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                // update database

                                Log.d("ContactsActivity", "Updating record FROM " +
                                                (((ModelContacts) parent.getAdapter().getItem(position)).getName())
                                                + " "
                                                + (((ModelContacts) parent.getAdapter().getItem(position)).getAddress())
                                                + " TO "
                                                + name.getText().toString() + " " + number.getText().toString()
                                );
                                try {

                                    if (DB.update(Tables.contacts,
                                            // old params
                                            new ModelContacts(
                                                    ((ModelContacts) parent.getAdapter().getItem(position)).getAddress(),
                                                    ((ModelContacts) parent.getAdapter().getItem(position)).getName()

                                            ),
                                            // new params
                                            new ModelContacts(
                                                    number.getText().toString(),
                                                    name.getText().toString()
                                            ),

                                            null
                                    )) {
                                        Log.d("ContactsActivity", "Successfully updated record");
                                    } else
                                        Log.d("ContactsActivity", "Failed to update record");


                                } catch (Exception e) {
                                }


                                d.cancel();

                                refreshListView();
                            }
                        };

                        final View.OnClickListener negativeEdit = new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                name.setEnabled(false);
                                number.setEnabled(false);

                                positive.setText("Edit");
                                positive.setOnClickListener(positiveDefault);

                                negative.setText("Delete");
                                negative.setOnClickListener(negativeDefault);
                            }
                        };

                        @Override
                        public void onClick(View v) {
                            name.setEnabled(true);
                            number.setEnabled(true);
                            positive.setText("Save");
                            positive.setOnClickListener(positiveEdit);

                            negative.setText("Cancel");
                            negative.setOnClickListener(negativeEdit);

                        }
                    }

                    ;


                    name.setEnabled(false);
                    number.setEnabled(false);

                    name.setText(((ModelContacts) parent.getAdapter().

                                    getItem(position)

                            ).

                                    getName()

                    );
                    number.setText(((ModelContacts) parent.getAdapter().

                                    getItem(position)

                            ).

                                    getAddress()

                    );


                    positive.setText("Edit");
                    positive.setOnClickListener(positiveDefault);

                    negative.setText("Delete");
                    negative.setOnClickListener(negativeDefault);


                    d.show();
                    d.getWindow().

                            setLayout((6 * width)

                                    / 7, LinearLayout.LayoutParams.WRAP_CONTENT);
                }
            });


            if(adapter.getCount() > 0){
                listview.setVisibility(ListView.VISIBLE);
                hidden.setVisibility(TextView.GONE);

            }else{
                listview.setVisibility(ListView.GONE);
                hidden.setVisibility(TextView.VISIBLE);
            }



        }catch (Exception e){

        }

    }

    private void initializeLayout() {

        search = (EditText) findViewById(R.id.txtSearch);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search.setText("");
            }
        });

        addNew = (TextView) findViewById(R.id.txtAddContact);
        addNew.setOnClickListener(new View.OnClickListener() {
            final Dialog d = new Dialog(ContactsActivity.this);
            DisplayMetrics metrics = getResources().getDisplayMetrics();
            int width = metrics.widthPixels;
            int height = metrics.heightPixels;

            @Override
            public void onClick(View v) {
                d.setContentView(R.layout.contacts_dialog);
                final EditText name = (EditText) d.findViewById(R.id.txtName);
                final EditText number = (EditText) d.findViewById(R.id.txtNumber);
                Button btnAdd = (Button) d.findViewById(R.id.btnPositive);
                Button btnCancel = (Button) d.findViewById(R.id.btnNegative);

                btnAdd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // save on database
                        try{
                            if(DB.insert(Tables.contacts,
                                    new ModelContacts(number.getText().toString(), name.getText().toString()),
                                    null)) {
                                refreshListView();
                                d.cancel();
                            }else{
                                Toast.makeText(ContactsActivity.this, "That contact already exists!", Toast.LENGTH_LONG).show();

                            }

                        }catch (Exception e){

                        }
                    }
                });

                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        d.cancel();
                    }

                });


                d.show();
                d.getWindow().setLayout((6 * width) / 7, LinearLayout.LayoutParams.WRAP_CONTENT);
            }
        });

        hidden = (TextView) findViewById(R.id.hidden);


        listview = (ListView) findViewById(R.id.listViewContacts);
        refreshListView();


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
        setContentView(R.layout.activity_contacts);

        DB = getDb(this);
        initializeLayout();


    }

    @Override
    public void onStart() {
        super.onStart();

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
        super.onStop();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_contacts, menu);
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
