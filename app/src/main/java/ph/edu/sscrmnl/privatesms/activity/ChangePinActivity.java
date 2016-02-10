package ph.edu.sscrmnl.privatesms.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import ph.edu.sscrmnl.privatesms.R;
import ph.edu.sscrmnl.privatesms.util.SQLiteHelper;
import ph.edu.sscrmnl.privatesms.util.Security;
import ph.edu.sscrmnl.privatesms.databasemodel.ModelConfig;
import ph.edu.sscrmnl.privatesms.databasemodel.Tables;

public class ChangePinActivity extends AppCompatActivity implements
        View.OnClickListener {

    private static final boolean LOGCAT = true;
    private final String TAG = this.getClass().getSimpleName();

    private TextView mainMsg;
    private EditText txtPin;
    private Button btnCLear, btnOkay, btn0, btn1, btn2, btn3, btn4, btn5, btn6, btn7, btn8, btn9, btnBack;

    private SQLiteHelper DB;


    private synchronized SQLiteHelper getDb(Context context){
        DB = new SQLiteHelper(context);
        return this.DB;
    }


    private void initializeLayout(){

        mainMsg = (TextView) findViewById(R.id.txtMainMessage);

        txtPin = (EditText) findViewById(R.id.txtPin);
        txtPin.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(confirmation) {
                    if(txtPin.getText().toString().equals(firstPin))
                        btnOkay.setEnabled(true);
                    else
                        btnOkay.setEnabled(false);
                }else{
                    btnOkay.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        btnCLear = (Button) findViewById(R.id.btnClear);
        btnCLear.setOnClickListener(this);
        btnOkay = (Button) findViewById(R.id.btnOk);
        btnOkay.setOnClickListener(this);
        btn0 = (Button) findViewById(R.id.btn0);
        btn0.setOnClickListener(this);
        btn1 = (Button) findViewById(R.id.btn1);
        btn1.setOnClickListener(this);
        btn2 = (Button) findViewById(R.id.btn2);
        btn2.setOnClickListener(this);
        btn3 = (Button) findViewById(R.id.btn3);
        btn3.setOnClickListener(this);
        btn4 = (Button) findViewById(R.id.btn4);
        btn4.setOnClickListener(this);
        btn5 = (Button) findViewById(R.id.btn5);
        btn5.setOnClickListener(this);
        btn6 = (Button) findViewById(R.id.btn6);
        btn6.setOnClickListener(this);
        btn7 = (Button) findViewById(R.id.btn7);
        btn7.setOnClickListener(this);
        btn8 = (Button) findViewById(R.id.btn8);
        btn8.setOnClickListener(this);
        btn9 = (Button) findViewById(R.id.btn9);
        btn9.setOnClickListener(this);

        btnBack = (Button) findViewById(R.id.btnBack);
        btnBack.setOnClickListener(this);
        btnBack.setVisibility(Button.GONE);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DB = getDb(this);


        initializeLayout();
        mainMsg.setText("Input your old PIN to continue");
    }

    private boolean authenticated = false;
    private boolean confirmation = false;
    private String firstPin = "";
    private String oldPin = "";

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn0:
                txtPin.setText(txtPin.getText().toString() + "0");
                break;
            case R.id.btn1:
                txtPin.setText(txtPin.getText().toString() + "1");
                break;
            case R.id.btn2:
                txtPin.setText(txtPin.getText().toString() + "2");
                break;
            case R.id.btn3:
                txtPin.setText(txtPin.getText().toString() + "3");
                break;
            case R.id.btn4:
                txtPin.setText(txtPin.getText().toString() + "4");
                break;
            case R.id.btn5:
                txtPin.setText(txtPin.getText().toString() + "5");
                break;
            case R.id.btn6:
                txtPin.setText(txtPin.getText().toString() + "6");
                break;
            case R.id.btn7:
                txtPin.setText(txtPin.getText().toString() + "7");
                break;
            case R.id.btn8:
                txtPin.setText(txtPin.getText().toString() + "8");
                break;
            case R.id.btn9:
                txtPin.setText(txtPin.getText().toString() + "9");
                break;
            case R.id.btnClear:
                txtPin.setText("");
                break;
            case R.id.btnOk:
                if(authenticated) {
                    if(confirmation) {
                        // insert to database
                        try {
                            if(txtPin.getText().toString().equals(firstPin)){
                                DB.update(Tables.config,
                                        new ModelConfig(Security.md5Hash(oldPin)),
                                        new ModelConfig(Security.md5Hash(firstPin)), null);
                                finish();
                                //startActivity(getIntent());
                            }
                        }catch (Exception e){}

                    }else{
                        firstPin = txtPin.getText().toString();
                        txtPin.setText("");
                        confirmation = true;
                        btnOkay.setEnabled(false);
                        mainMsg.setText("Confirm your NEW PIN ");
                    }

                }else{
                    // pin exists

                    try{
                        if(((ModelConfig)DB.selectAll(Tables.config, ModelConfig.class, null)[0]).getPin()
                                .equals(Security.md5Hash(txtPin.getText().toString()))
                                )
                        {
                            // start activity here
                            oldPin = txtPin.getText().toString();
                            txtPin.setText("");
                            authenticated = true;
                            btnBack.setText("Cancel Changing PIN");
                            mainMsg.setText("Register a your new PIN ");
                            btnBack.setVisibility(Button.VISIBLE);
                            //finish();
                            //startActivity(new Intent(this, ChangePinActivity.class));

                        }else{
                            Toast.makeText(ChangePinActivity.this, "Incorrect PIN!", Toast.LENGTH_LONG).show();
                            txtPin.setText("");
                        }
                    }catch(Exception e){}

                }


                break;
            case R.id.btnBack:
                if(authenticated){
                    finish();
                }



        }



    }


    @Override
    public void onDestroy(){
        super.onDestroy();
        this.DB.close();
        if(this.LOGCAT){
            Log.d(this.TAG, this.TAG + " onDestroy()");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
