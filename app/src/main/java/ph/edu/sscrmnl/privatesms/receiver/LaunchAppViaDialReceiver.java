package ph.edu.sscrmnl.privatesms.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import ph.edu.sscrmnl.privatesms.util.SQLiteHelper;

public class LaunchAppViaDialReceiver extends BroadcastReceiver {


    private SQLiteHelper DB;

    private synchronized SQLiteHelper getDb(Context context){
        DB = new SQLiteHelper(context);
        return this.DB;
    }


    @Override
    public void onReceive(Context context, Intent intent) {

        // Disable this at the moment, all relying components not ready

        /*
        DB = getDb(context);

        // TODO Auto-generated method stub
        Bundle bundle = intent.getExtras();
        if (null == bundle)
            return;
        String phoneNubmer = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
        //here change the number to your desired number

        try{
            if(
                    ("*" +((ModelConfig)DB.selectAll(Tables.config, ModelConfig.class, null)[0]).getPin()
                    ).equals("*" + Security.md5Hash(phoneNubmer.substring(1)))
              )
            {
                setResultData(null);
                Intent appIntent = new Intent(context, MainActivity.class);
                appIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(appIntent);
            }
        }catch (Exception e) {
            Intent appIntent = new Intent(context, MainActivity.class);
            appIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(appIntent);

        }
        */
    }
}
