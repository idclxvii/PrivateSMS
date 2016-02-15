package ph.edu.sscrmnl.privatesms;

import android.app.Application;

/**
 * Created by IDcLxViI on 2/15/2016.
 */
public class ApplicationClass extends Application {

    private static int nRequiredStarted = 0; //this is our startCounter

    public void incrementStart() {
        ++nRequiredStarted;
    }

    private static int nRequiredPaused = 0; //this is our pauseCounter

    public void incrementPause() {
        ++nRequiredPaused;
    }

    public void notifyFinish() { //this is called when the activity finishes
        --nRequiredPaused;
        --nRequiredPaused;
        --nRequiredStarted;
    }

    public void reauthenticate(){
        --nRequiredPaused;
        --nRequiredPaused;
    }

    public boolean shouldLogin() { //this decides whether to go to the login screen(true)
        return (nRequiredPaused==nRequiredStarted);

    }

    public void reset() { //reset the counters in the login screen
        nRequiredPaused = 0;
        nRequiredStarted = 0;
    }
}

