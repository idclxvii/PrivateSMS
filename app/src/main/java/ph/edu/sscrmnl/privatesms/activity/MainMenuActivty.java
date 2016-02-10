package ph.edu.sscrmnl.privatesms.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import ph.edu.sscrmnl.privatesms.adapter.MainMenuAdapter;
import ph.edu.sscrmnl.privatesms.R;

public class MainMenuActivty extends AppCompatActivity {

    private ListView listview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        listview = (ListView) findViewById(R.id.listViewMainMenu);
        listview.setAdapter(new MainMenuAdapter(this, new String[] {"Contacts", "SMS", "Change PIN"} ));

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:
                        // contacts activity
                        startActivity(new Intent( MainMenuActivty.this, ContactsActivity.class ));
                        break;
                    case 1:
                        // sms activity
                        startActivity(new Intent (MainMenuActivty.this, SMSListActivity.class));
                        break;
                    case 2:
                        // change PIN activity
                        startActivity(new Intent(MainMenuActivty.this, ChangePinActivity.class));
                        break;

                }
            }
        });
    }

    private long backPressed = 0;
    @Override
    public void onBackPressed(){
        if (this.backPressed + 2000 > System.currentTimeMillis()){
            finish();
        }else{
            Toast.makeText(getBaseContext(), "Press once again to exit!", Toast.LENGTH_SHORT).show();
        }
        this.backPressed = System.currentTimeMillis();

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_menu_activty, menu);
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
