package app.michaelwuensch.bitbanana.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;

import app.michaelwuensch.bitbanana.pin.PinEntryActivity;
import app.michaelwuensch.bitbanana.R;
import app.michaelwuensch.bitbanana.connection.manageNodeConfigs.NodeConfigsManager;

public class PinScreenUtil {

    static public void askForAccess(Activity activity, OnSecurityCheckPerformedListener onSecurityCheckPerformedListener) {
        if (NodeConfigsManager.getInstance().hasAnyConfigs() && TimeOutUtil.getInstance().isTimedOut()) {
            if (PrefsUtil.isPinEnabled()) {
                // Go to PIN entry screen
                Intent pinIntent = new Intent(activity, PinEntryActivity.class);
                pinIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                activity.startActivity(pinIntent);
            } else {

                // Check if pin is active according to key store
                boolean isPinActive = false;
                try {
                    isPinActive = new KeystoreUtil().isPinActive();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // Only allow access if pin is not active in key store!
                if (isPinActive) {
                    // According to the key store, the pin is still active. This happens if the pin got deleted from the prefs file without also removing the keystore entry.
                    // Basically this would be the case if the PIN hash was removed in a different way than from the apps settings menu. (For example with a file explorer on a rooted device)
                    new AlertDialog.Builder(activity)
                            .setMessage(R.string.error_pin_deactivation_attempt)
                            .setCancelable(false)
                            .setPositiveButton(R.string.continue_string, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    activity.finish();
                                }
                            }).show();
                } else {
                    // Access granted
                    onSecurityCheckPerformedListener.onAccessGranted();
                }
            }
        } else {
            // Access granted
            onSecurityCheckPerformedListener.onAccessGranted();
        }
    }

    public interface OnSecurityCheckPerformedListener {
        void onAccessGranted();
    }

}


