package com.afomic.diary;

import android.app.Application;
import android.speech.SpeechRecognizer;

import com.google.firebase.database.FirebaseDatabase;

import net.gotev.speech.Logger;
import net.gotev.speech.Speech;

import java.util.Locale;

/**
 * @author Aleksandar Gotev
 */
public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        Speech.init(this, getPackageName()).setGetPartialResults(false)
                .setStopListeningAfterInactivity(10000);
        Logger.setLogLevel(Logger.LogLevel.DEBUG);
    }
}
