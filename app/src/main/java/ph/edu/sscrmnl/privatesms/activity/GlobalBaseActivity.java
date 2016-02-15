package ph.edu.sscrmnl.privatesms.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import ph.edu.sscrmnl.privatesms.ApplicationClass;

/**
 * Created by IDcLxViI on 2/15/2016.
 */
public class GlobalBaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ((ApplicationClass)getApplication()).incrementStart();
    }

    @Override
    protected void onStart(){
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(((ApplicationClass)getApplication()).shouldLogin()) {
            // go to login method
            Intent i = new Intent(this, MainActivity.class);
            boolean reauth = true;
            i.putExtra("reauth", reauth);
            ((ApplicationClass)getApplication()).reauthenticate();
            startActivity(i);

        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        ((ApplicationClass)getApplication()).incrementPause();
    }

    @Override
    public void onStop() {
        super.onStop();

    }

    @Override
    public void finish() {
        ((ApplicationClass)getApplication()).notifyFinish();
        super.finish();
    }





}
