package vs.turbo.hidden;

import android.content.Context;

import vs.turbo.util.AppReloader;

public class HiddenAppsDrawerState {
    private static HiddenAppsDrawerState sInstance;

    public static synchronized HiddenAppsDrawerState getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new HiddenAppsDrawerState(context);
        }
        return sInstance;
    }

    private final Context mContext;
    private boolean mRevealed;

    private HiddenAppsDrawerState(Context context) {
        mContext = context;
    }

    public void toggleRevealed() {
        setRevealed(!isRevealed());
    }

    public void setRevealed(boolean revealed) {
        if (mRevealed != revealed) {
            mRevealed = revealed;
            AppReloader reloader = AppReloader.get(mContext);
            reloader.reload(reloader.hiddenApps());
        }
    }

    public boolean isRevealed() {
        return mRevealed;
    }
}
