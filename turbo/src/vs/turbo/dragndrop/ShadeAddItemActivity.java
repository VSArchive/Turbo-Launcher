package vs.turbo.dragndrop;

import android.os.Bundle;

import com.android.launcher3.dragndrop.AddItemActivity;

import vs.turbo.TurboFont;
import vs.turbo.customization.ShadeStyle;

public class ShadeAddItemActivity extends AddItemActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        TurboFont.override(this);
        ShadeStyle.override(this);
        super.onCreate(savedInstanceState);
    }
}
