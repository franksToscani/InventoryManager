package com.example.inventorymanager;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.example.inventorymanager.WelcomeActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Mostra lo splash screen di default di Android 12+
        try {
            Thread.sleep(1500);  // Durata dello splash (1.5 secondi)
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Dopo lo splash, avvia la WelcomeActivity
        Intent intent = new Intent(this, WelcomeActivity.class);
        startActivity(intent);
        finish();  // Chiude lo Splash Screen
    }
}
