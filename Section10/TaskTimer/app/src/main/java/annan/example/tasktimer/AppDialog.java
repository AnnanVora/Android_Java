package annan.example.tasktimer;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.NoSuchElementException;

public class AppDialog extends DialogFragment {

    private static final String TAG = "AppDialog";
    public static final String DIALOG_ID = "id";
    public static final String DIALOG_MESSAGE = "message";
    public static final String DIALOG_POSITIVE_RID = "positive_rid";
    public static final String DIALOG_NEGATIVE_RID = "negative_rid";
    private DialogEvents dialogEvents;

    interface DialogEvents {
        void onPositiveDialogEvent(int dialogID, Bundle args);
        void onNegativeDialogEvent(int dialogID, Bundle args);
        void onDialogCancel(int dialogID);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        Log.d(TAG, "onAttach: Entering, activity is " + context.toString());
        super.onAttach(context);

        if (!(context instanceof DialogEvents)) {
            throw new ClassCastException(context.toString() + " must implement " + DialogEvents.class.getSimpleName());
        }
        dialogEvents = (DialogEvents) context;
    }

    @Override
    public void onDetach() {
        Log.d(TAG, "onDetach: Entering...");
        super.onDetach();
        dialogEvents = null;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateDialog: starts");

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        final Bundle args = getArguments();
        final int dialogID;
        String messageString;
        int positiveStringID;
        int negativeStringID;

        if (args != null) {
            dialogID = args.getInt(DIALOG_ID);
            messageString = args.getString(DIALOG_MESSAGE);

            if (dialogID == 0 || messageString == null) {
                throw new NoSuchElementException("DIALOG_ID and/or DIALOG_MESSAGE not present in the bundle");
            }
            positiveStringID = args.getInt(DIALOG_POSITIVE_RID);
            negativeStringID = args.getInt(DIALOG_NEGATIVE_RID);
            if (positiveStringID == 0) {
                positiveStringID = R.id.ok;
            }
            if (negativeStringID == 0) {
                negativeStringID = R.id.cancel;
            }
        } else {
            throw new NoSuchElementException("Must pass DIALOG_ID and DIALOG_MESSAGE in the Bundle");
        }
        builder.setMessage(messageString)
            .setPositiveButton(positiveStringID, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (dialogEvents != null) {
                        dialogEvents.onPositiveDialogEvent(dialogID, args);
                    }
                }
            })
            .setNegativeButton(negativeStringID, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (dialogEvents != null) {
                        dialogEvents.onNegativeDialogEvent(dialogID, args);
                    }
                }
            });
        return super.onCreateDialog(savedInstanceState);
    }

    @Override
    public void onCancel(@NonNull DialogInterface dialog) {
        Log.d(TAG, "onCancel: starts");
        if (dialogEvents != null) {
            int dialogID = getArguments().getInt(DIALOG_ID);
            dialogEvents.onDialogCancel(dialogID);
        }
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        Log.d(TAG, "onDismiss: starts");
        super.onDismiss(dialog);
    }
}
