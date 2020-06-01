package amirz.turbo.dragndrop;

import android.os.Bundle;

import com.android.launcher3.dragndrop.AddItemActivity;

import amirz.turbo.TurboFont;
import amirz.turbo.customization.ShadeStyle;

public class ShadeAddItemActivity extends AddItemActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        TurboFont.override(this);
        ShadeStyle.override(this);
        super.onCreate(savedInstanceState);
    }
}
