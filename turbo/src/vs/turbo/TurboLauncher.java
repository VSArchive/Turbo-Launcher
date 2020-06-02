package vs.turbo;

import android.os.Bundle;

import com.android.launcher3.Launcher;

import vs.turbo.customization.TurboStyle;

public class TurboLauncher extends Launcher {
    private enum State {
        PAUSED,
        RECREATE_DEFERRED,
        KILL_DEFERRED,
        RESUMED
    }

    private final TurboLauncherCallbacks mCallbacks;
    private State mState = State.PAUSED;

    public TurboLauncher() {
        super();

        mCallbacks = new TurboLauncherCallbacks(this);
        setLauncherCallbacks(mCallbacks);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        TurboRestarter.cancelRestart(this);
        TurboFont.override(this);
        TurboStyle.override(this);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mState == State.KILL_DEFERRED) {
            TurboRestarter.initiateRestart(this);
        } else if (mState == State.RECREATE_DEFERRED) {
            super.recreate();
        }
        mState = State.RESUMED;
    }

    @Override
    public void recreate() {
        if (mState == State.RESUMED) {
            super.recreate();
        } else if (mState != State.KILL_DEFERRED) {
            mState = State.RECREATE_DEFERRED;
        }
    }

    public void kill() {
        if (mState == State.RESUMED) {
            TurboRestarter.initiateRestart(this);
        } else {
            mState = State.KILL_DEFERRED;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mState = State.PAUSED;
    }

    public TurboLauncherCallbacks getCallbacks() {
        return mCallbacks;
    }
}
