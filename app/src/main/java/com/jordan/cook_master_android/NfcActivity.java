package com.jordan.cook_master_android;

import androidx.appcompat.app.AppCompatActivity;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Parcelable;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class NfcActivity extends AppCompatActivity {
    NfcAdapter nfcAdapter;
    PendingIntent pendingIntent;
    IntentFilter writingTagFilters[];
    Tag tag;
    Context context;

    private static final String SHARED_PREFS_NAME = "MySharedPrefs";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nfc);
        context = this;

        // Obtention de l'adaptateur NFC pour le périphérique
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (nfcAdapter == null) {
            // Le périphérique ne prend pas en charge le NFC
            Toast.makeText(this, "This device doesn't support NFC.", Toast.LENGTH_LONG).show();
            finish();
        }

        // Lecture du contenu du NFC à partir de l'intent actuel
        readfromIntent(getIntent());

        // Configuration de l'intent pour la détection des tags NFC
        pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        tagDetected.addCategory(Intent.CATEGORY_DEFAULT);
        writingTagFilters = new IntentFilter[]{tagDetected};
    }

    // Méthode appelée lorsqu'un nouvel intent NFC est reçu
    private void readfromIntent(Intent intent) {
        String action = intent.getAction();
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)){
            // Extraction des messages NDEF du tag NFC
            Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            NdefMessage[] msgs = new NdefMessage[0];
            if(rawMsgs != null){
                msgs = new NdefMessage[rawMsgs.length];
                for(int i = 0; i < rawMsgs.length; i++){
                    msgs[i] = (NdefMessage) rawMsgs[i];
                }
            }
            // Construction de la vue des tags NFC
            buidTagsViews(msgs);
        }
    }

    // Construction de la vue des tags NFC
    private void buidTagsViews(NdefMessage[] msgs) {
        if(msgs == null || msgs.length == 0) return;
        String text = "";
        byte[] payload = msgs[0].getRecords()[0].getPayload();
        String textEncoding = ((payload[0] & 128) == 0) ? "UTF-8" : "UTF-16";
        int languageSize = payload[0] & 0063;
        try{
            text = new String(payload, languageSize + 1, payload.length - languageSize - 1, textEncoding);
        }catch (Exception e){
            Toast.makeText(this, "Unsupported Encoding", Toast.LENGTH_LONG).show();
        }
        getBonus(text);
    }

    @Override
    protected void onNewIntent(Intent intent){
        super.onNewIntent(intent);
        setIntent(intent);
        // Lecture du contenu du NFC à partir du nouvel intent
        readfromIntent(intent);
        if(NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())){
            // Récupération du tag NFC
            tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        }
    }

    private void getBonus(String content) {
        // Créer l'URL de la requête GET
        String url = BuildConfig.API_URL + "user/" + content;
        SharedPreferences preferences = getSharedPreferences(SHARED_PREFS_NAME, MODE_PRIVATE);
        String api_key = preferences.getString("api_key", "");
        Map<String, String> headers = new HashMap<>();
        headers.put("API-KEY", api_key);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, null,
                response -> {
                    Toast.makeText(this, "Bonus récupéré avec succès", Toast.LENGTH_SHORT).show();
                },
                error -> {
                    // Traitement de l'erreur de la requête
                    Toast.makeText(this, "Erreur lors de la récupération du bonus: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return headers;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }

}
