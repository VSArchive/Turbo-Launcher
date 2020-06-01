package vs.turbo;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;

import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceScreen;

import com.android.launcher3.BuildConfig;
import com.android.launcher3.R;
import com.android.launcher3.Utilities;
import com.android.launcher3.settings.SettingsActivity;

import vs.turbo.customization.IconDatabase;
import vs.turbo.customization.ShadeStyle;
import vs.turbo.customization.IconShapeOverride;
import vs.turbo.icons.pack.IconPackManager;
import vs.turbo.settings.DockSearchPrefSetter;
import vs.turbo.settings.FeedProviderPrefSetter;
import vs.turbo.settings.IconPackPrefSetter;
import vs.turbo.settings.ReloadingListPreference;
import vs.turbo.util.AppReloader;

import static vs.turbo.TurboFont.KEY_FONT;
import static vs.turbo.TurboLauncherCallbacks.KEY_ENABLE_MINUS_ONE;
import static vs.turbo.TurboLauncherCallbacks.KEY_FEED_PROVIDER;
import static vs.turbo.customization.ShadeStyle.KEY_THEME;
import static vs.turbo.customization.DockSearch.KEY_DOCK_SEARCH;
import static vs.turbo.customization.IconShapeOverride.KEY_ICON_SHAPE;
import static com.android.launcher3.util.Themes.KEY_DEVICE_THEME;

public class TurboSettings extends SettingsActivity {
    public interface OnResumePreferenceCallback {
        void onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        TurboFont.override(this);
        ShadeStyle.override(this);
        super.onCreate(savedInstanceState);
        getActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("unused")
    public static class TurboSettingsFragment extends SettingsActivity.LauncherSettingsFragment {
        private static final String CATEGORY_STYLE = "category_style";

        private static final String KEY_ICON_PACK = "pref_icon_pack";
        private static final String KEY_APP_VERSION = "pref_app_version";

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            super.onCreatePreferences(savedInstanceState, rootKey);

            final Context context = getActivity();

            // Load the icon pack once to set the correct default icon pack.
            IconPackManager.get(context);

            // Customization
            ReloadingListPreference icons = (ReloadingListPreference) findPreference(KEY_ICON_PACK);
            icons.setValue(IconDatabase.getGlobal(context));
            icons.setOnReloadListener(new IconPackPrefSetter(context));
            icons.setOnPreferenceChangeListener((pref, val) -> {
                IconDatabase.clearAll(context);
                IconDatabase.setGlobal(context, (String) val);
                AppReloader.get(context).reload();
                return true;
            });

            PreferenceCategory style = (PreferenceCategory) findPreference(CATEGORY_STYLE);
            Preference iconShapeOverride = findPreference(KEY_ICON_SHAPE);
            if (iconShapeOverride != null) {
                if (Utilities.ATLEAST_OREO) {
                    IconShapeOverride.handlePreferenceUi((ListPreference) iconShapeOverride);
                } else {
                    style.removePreference(iconShapeOverride);
                }
            }

            if (Utilities.ATLEAST_Q) {
                style.removePreference(style.findPreference(KEY_DEVICE_THEME));
            }

            Preference.OnPreferenceChangeListener restart = (pref, val) -> {
                startActivity(getActivity().getIntent()
                                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP),
                        ActivityOptions.makeCustomAnimation(
                                context, R.anim.fade_in, R.anim.fade_out).toBundle());
                return true;
            };

            findPreference(KEY_THEME).setOnPreferenceChangeListener(restart);
            findPreference(KEY_FONT).setOnPreferenceChangeListener(restart);

            ReloadingListPreference search =
                    (ReloadingListPreference) findPreference(KEY_DOCK_SEARCH);
            search.setOnReloadListener(new DockSearchPrefSetter(context));

            ReloadingListPreference feed =
                    (ReloadingListPreference) findPreference(KEY_FEED_PROVIDER);
            feed.setOnReloadListener(new FeedProviderPrefSetter(context));
            feed.setOnPreferenceChangeListener((pref, val) -> {
                Utilities.getPrefs(context).edit()
                        .putBoolean(KEY_ENABLE_MINUS_ONE, !TextUtils.isEmpty((String) val))
                        .apply();
                return true;
            });

            // About
            String versionName = BuildConfig.VERSION_NAME;
            try {
                PackageManager pm = context.getPackageManager();
                PackageInfo pi = pm.getPackageInfo(BuildConfig.APPLICATION_ID, 0);
                versionName = pi.versionName;
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }

            Preference version = findPreference(KEY_APP_VERSION);
            version.setSummary(context.getString(R.string.about_app_version_value,
                    versionName, BuildConfig.BUILD_TYPE));
            Uri intentData = Uri.parse("package:" + BuildConfig.APPLICATION_ID);
            version.setIntent(version.getIntent().setData(intentData));
        }

        @Override
        public void onResume() {
            super.onResume();

            PreferenceScreen screen = getPreferenceScreen();
            for (int i = 0; i < screen.getPreferenceCount(); i++) {
                Preference preference = screen.getPreference(i);
                if (preference instanceof PreferenceCategory) {
                    PreferenceCategory cat = (PreferenceCategory) preference;
                    for (int j = 0; j < cat.getPreferenceCount(); j++) {
                        Preference preference2 = cat.getPreference(j);
                        if (preference2 instanceof OnResumePreferenceCallback) {
                            ((OnResumePreferenceCallback) preference2).onResume();
                        }
                    }
                }
            }
        }
    }
}
