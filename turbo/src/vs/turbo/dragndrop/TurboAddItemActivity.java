package vs.turbo.dragndrop;

import android.os.Bundle;

import com.android.launcher3.dragndrop.AddItemActivity;

import vs.turbo.TurboFont;
import vs.turbo.customization.TurboStyle;

public class TurboAddItemActivity extends AddItemActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        TurboFont.override(this);
        TurboStyle.override(this);
        super.onCreate(savedInstanceState);
    }
}
