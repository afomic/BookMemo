package com.afomic.diary.fragment;


import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afomic.diary.R;
import com.afomic.diary.model.Entry;
import com.afomic.diary.util.Util;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import net.gotev.speech.GoogleVoiceTypingDisabledException;
import net.gotev.speech.Speech;
import net.gotev.speech.SpeechDelegate;
import net.gotev.speech.SpeechRecognitionNotAvailable;
import net.gotev.speech.SpeechUtil;
import net.gotev.speech.ui.SpeechProgressView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by afomic on 11/27/17.
 */

public class RecordVoiceDialog extends DialogFragment implements SpeechDelegate {

    @BindView(R.id.tv_translation)
    EditText entryEditText;

    @BindView(R.id.edt_page_number)
    EditText pageNumberEditText;

    @BindView(R.id.btn_listen)
    Button startListenButton;

    @BindView(R.id.recording_view)
    LinearLayout recordingLayout;

    @BindView(R.id.save_layout)
    LinearLayout saveLayout;

    @BindView(R.id.listen_layout)
    LinearLayout listenLayout;

    @BindView(R.id.imv_record_icon)
    ImageView recordIcon;

    @BindView(R.id.progress)
    SpeechProgressView progress;

    Unbinder mUnbinder;

    private boolean listening=false;

    private static final String BUNDLE_BOOK_ID="book_id";

    private DatabaseReference entryRef;

    public static RecordVoiceDialog getInstance(String bookId){
        Bundle args=new Bundle();
        args.putString(BUNDLE_BOOK_ID,bookId);
        RecordVoiceDialog dialog=new RecordVoiceDialog();
        dialog.setArguments(args);
        return dialog;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String bookId=getArguments().getString(BUNDLE_BOOK_ID);
        entryRef= FirebaseDatabase.getInstance().getReference("entries")
                .child(bookId);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
        View v= LayoutInflater.from(getActivity()).inflate(R.layout.dailog_record_voice,
                null,
                false);
        builder.setView(v);
        builder.setTitle("Record Entry");
        mUnbinder= ButterKnife.bind(this,v);
        requestPermission();
        int[] colors = {
                ContextCompat.getColor(getActivity(), android.R.color.black),
                ContextCompat.getColor(getActivity(), android.R.color.darker_gray),
                ContextCompat.getColor(getActivity(), android.R.color.black),
                ContextCompat.getColor(getActivity(), android.R.color.holo_orange_dark),
                ContextCompat.getColor(getActivity(), android.R.color.holo_red_dark)
        };
        progress.setColors(colors);
        return builder.create();
    }
    @OnClick(R.id.btn_listen)
    public void startListen(){
        if(!listening){
            startListenButton.setText("STOP");
            recordingLayout.setVisibility(View.VISIBLE);
            recordIcon.setVisibility(View.GONE);
            listening=true;
            initializeVoiceListening();
        }else {
            Speech.getInstance().stopListening();

        }

    }
    @OnClick(R.id.cancel_recoding)
    public void cancelRecording(){
        dismiss();
    }
    @OnClick(R.id.btn_cancel_entry)
    public void dismissDialog(){
        dismiss();
    }
    @OnClick(R.id.btn_save_entry)
    public void saveEntry(){

        if(isValidEntry()){
            Entry newEntry=new Entry();
            newEntry.setContent(Util.getString(entryEditText));
            newEntry.setPageNumber(Util.getString(pageNumberEditText));
            String id=entryRef.push().getKey();
            newEntry.setId(id);
            entryRef.child(id).setValue(newEntry);
            dismiss();

        }

    }


    @Override
    public void onStartOfSpeech() {
        Log.e("rr", "onStartOfSpeech: ");

    }

    @Override
    public void onSpeechRmsChanged(float value) {

    }

    @Override
    public void onSpeechPartialResults(List<String> results) {
        String text="";
        for (String partial : results) {
            text=text.concat(partial+" ");
        }


    }

    @Override
    public void onSpeechResult(String result) {
        recordingLayout.setVisibility(View.GONE);
        listenLayout.setVisibility(View.GONE);
        recordIcon.setVisibility(View.GONE);
        saveLayout.setVisibility(View.VISIBLE);
        entryEditText.append(result);
        entryEditText.requestFocus();
        entryEditText.setCursorVisible(true);
    }
    public void initializeVoiceListening(){
        try {
            Speech.getInstance().setGetPartialResults(true)
                    .startListening(progress, this);

        } catch (SpeechRecognitionNotAvailable exc) {
            showSpeechNotSupportedDialog();

        } catch (GoogleVoiceTypingDisabledException exc) {
            showEnableGoogleVoiceTyping();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }
    private void showSpeechNotSupportedDialog() {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        SpeechUtil.redirectUserToGoogleAppOnPlayStore(getActivity());
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.speech_not_available)
                .setCancelable(false)
                .setPositiveButton(R.string.yes, dialogClickListener)
                .setNegativeButton(R.string.no, dialogClickListener)
                .show();
    }

    private void showEnableGoogleVoiceTyping() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.enable_google_voice_typing)
                .setCancelable(false)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // do nothing
                    }
                })
                .show();
    }
    public void requestPermission(){
        if(Build.VERSION.SDK_INT>Build.VERSION_CODES.LOLLIPOP){
            if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.READ_EXTERNAL_STORAGE) !=
                    PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[] {
                                android.Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO
                        },100);
            }
        }


    }
    public boolean isValidEntry(){
        if(Util.isEmpty(pageNumberEditText)){
            Util.makeToast(getActivity(),"Please provide Page number");
            return false;
        }
        if(Util.isEmpty(entryEditText)){
            Util.makeToast(getActivity(),"You cannot save empty value");
            return false;
        }
        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}

