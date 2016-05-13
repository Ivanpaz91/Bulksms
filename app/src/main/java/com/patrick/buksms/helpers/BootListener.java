package com.patrick.buksms.helpers;

import android.content.Context;
import android.content.Intent;

/**
 * Created by ivan on 10/30/15.
 */
public interface BootListener {
    void onBootCompleted(Context context, Intent intent);
}
