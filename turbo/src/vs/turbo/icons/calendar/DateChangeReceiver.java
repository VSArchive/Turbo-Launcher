package vs.turbo.icons.calendar;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;

import com.android.launcher3.util.ComponentKey;
import com.android.launcher3.util.Executors;

import java.util.HashSet;
import java.util.Set;

import vs.turbo.util.AppReloader;

/**
 * Listens for date change events and uses the IconReloader to reload all loaded calendar icons
 * when the date has changed.
 */
public class DateChangeReceiver extends BroadcastReceiver {
    private final Set<ComponentKey> mDynamicCalendars = new HashSet<>();

    public DateChangeReceiver(Context context) {
        super();

        IntentFilter filter = new IntentFilter(Intent.ACTION_DATE_CHANGED);
        filter.addAction(Intent.ACTION_TIME_CHANGED);
        filter.addAction(Intent.ACTION_TIMEZONE_CHANGED);

        Handler handler = new Handler(Executors.MODEL_EXECUTOR.getLooper());
        context.registerReceiver(this, filter, null, handler);
    }

    public void setIsDynamic(ComponentKey key, boolean calendar) {
        if (calendar) {
            mDynamicCalendars.add(key);
        } else {
            mDynamicCalendars.remove(key);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        AppReloader.get(context).reload(mDynamicCalendars);
    }
}
