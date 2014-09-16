package app.mysunshine.android.example.com.mysunshine.services;

import android.app.IntentService;
import android.content.Intent;

/**
 * Created by k557782 on 9/15/14.
 */
public class GcmIntentService extends IntentService {
    public GcmIntentService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
    }
}
