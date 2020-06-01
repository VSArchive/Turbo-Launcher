package vs.turbo;

import android.content.ComponentName;
import android.content.Context;
import android.os.UserHandle;

import com.android.launcher3.AppFilter;
import com.android.launcher3.BuildConfig;

import java.util.HashSet;
import java.util.Set;

import vs.turbo.hidden.HiddenAppsDrawerState;
import vs.turbo.hidden.HiddenAppsDatabase;

@SuppressWarnings("unused")
public class TurboAppFilter extends AppFilter {
    private final Context mContext;
    private final Set<ComponentName> mFilter = new HashSet<>();

    public TurboAppFilter(Context context) {
        mContext = context;
        mFilter.add(new ComponentName(BuildConfig.APPLICATION_ID, TurboLauncher.class.getName()));
    }

    @Override
    public boolean shouldShowApp(ComponentName app, UserHandle user) {
        boolean revealed = HiddenAppsDrawerState.getInstance(mContext).isRevealed();
        return !mFilter.contains(app)
                && (revealed || !HiddenAppsDatabase.isHidden(mContext, app, user));
    }
}
