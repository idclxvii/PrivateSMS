package ph.edu.sscrmnl.privatesms.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class RefreshAdapterReceiver extends BroadcastReceiver {

    public static final String REFRESH_ADAPTER = "REFRESH_ADAPTER";
    private OnDataUpdateListener mDataUpdateListener = null;


    public RefreshAdapterReceiver(OnDataUpdateListener listener) {
        mDataUpdateListener = listener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        if (null != mDataUpdateListener) {
            mDataUpdateListener.onDataAvailable();
        }

    }
}
