package vs.turbo;

import android.content.Context;
import android.text.TextUtils;

import com.android.launcher3.MainProcessInitializer;
import com.android.launcher3.Utilities;
import com.android.launcher3.config.FeatureFlags;

import vs.turbo.customization.DockSearch;
import vs.turbo.customization.IconShapeOverride;

import static com.android.searchlauncher.SmartspaceQsbWidget.KEY_SMARTSPACE;

@SuppressWarnings("unused")
public class TurboProcessInitializer extends MainProcessInitializer {
    public TurboProcessInitializer(Context context) {
        FeatureFlags.QSB_ON_FIRST_SCREEN =
                Utilities.getPrefs(context).getBoolean(KEY_SMARTSPACE, true);
        FeatureFlags.HOTSEAT_WIDGET =
                !TextUtils.isEmpty(Utilities.getPrefs(context)
                        .getString(DockSearch.KEY_DOCK_SEARCH, ""));
        IconShapeOverride.apply(context);
    }
}
