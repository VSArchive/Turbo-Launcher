package vs.turbo.settings;

import android.content.ComponentName;
import android.content.Context;

import androidx.preference.ListPreference;

import com.android.launcher3.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import vs.turbo.customization.IconDatabase;
import vs.turbo.icons.pack.IconPackManager;

public class IconPackPrefSetter implements ReloadingListPreference.OnReloadListener {
    private final Context mContext;
    private final ComponentName mFilter;

    public IconPackPrefSetter(Context context) {
        this(context, null);
    }

    public IconPackPrefSetter(Context context, ComponentName filter) {
        mContext = context;
        mFilter = filter;
    }

    @Override
    public void updateList(ListPreference pref) {
        IconPackManager ipm = IconPackManager.get(mContext);
        Map<String, CharSequence> packList = ipm.getProviderNames();
        String globalPack = IconDatabase.getGlobal(mContext);

        if (mFilter != null) {
            // Filter for packs with icon for this app, or the global pack.
            for (String pkg : new HashSet<>(packList.keySet())) {
                if (!ipm.packContainsActivity(pkg, mFilter)) {
                    packList.remove(pkg);
                }
            }
        }

        CharSequence[] keys = new String[packList.size() + 1];
        CharSequence[] values = new String[keys.length];
        int i = 0;

        // First value, system default, or the current icon pack if that has no icon yet.
        keys[i] = mContext.getResources().getString(R.string.pref_value_default);
        values[i++] = packList.containsKey(globalPack) ? "" : globalPack;

        // List of available icon packs
        List<Map.Entry<String, CharSequence>> packs = new ArrayList<>(packList.entrySet());
        Collections.sort(packs,
                (o1, o2) -> normalizeTitle(o1.getValue()).compareTo(normalizeTitle(o2.getValue())));
        for (Map.Entry<String, CharSequence> entry : packs) {
            keys[i] = entry.getValue();
            values[i++] = entry.getKey();
        }

        pref.setEntries(keys);
        pref.setEntryValues(values);
    }

    private String normalizeTitle(CharSequence title) {
        return title.toString().toLowerCase();
    }
}
