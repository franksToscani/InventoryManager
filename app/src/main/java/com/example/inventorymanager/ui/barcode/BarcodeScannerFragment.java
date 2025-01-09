package com.example.inventorymanager.ui.barcode;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.inventorymanager.R;
import com.example.inventorymanager.ui.additem.AddItemFragment;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.BarcodeView;

public class BarcodeScannerFragment extends Fragment {
    private BarcodeView barcodeView;
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 100;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_barcode_scanner, container, false);
        barcodeView = view.findViewById(R.id.barcode_scanner);

        startCamera();

        return view;
    }

    private void startCamera() {
        barcodeView.decodeContinuous(new BarcodeCallback() {
            @Override
            public void barcodeResult(BarcodeResult result) {
                if (result != null) {
                    String scannedCode = result.getText();
                    Log.d("BarcodeScanner", "Codice scansionato: " + scannedCode);

                    // Passa il codice scansionato al metodo interpretScannedCode
                    boolean isValid = interpretScannedCode(scannedCode);
                    if (isValid) {
                        playSuccessSound();
                    } else {
                        playFailureSound();
                    }
                }
            }
        });
    }

    private boolean interpretScannedCode(String scannedCode) {
        Log.d("BarcodeScanner", "Codice scansionato: " + scannedCode);

        if (scannedCode.contains(",")) {
            String[] parts = scannedCode.split(",");
            if (parts.length == 3) {
                String name = parts[0].trim(); // Nome del prodotto
                String category = parts[1].trim(); // Categoria del prodotto
                String eanCodeStr = parts[2].trim(); // EAN come stringa

                try {
                    // Converti EAN in float
                    float eanCode = Float.parseFloat(eanCodeStr);

                    Log.d("BarcodeScanner", "Nome: " + name + ", Categoria: " + category + ", EAN: " + eanCode);

                    // Passa i dati al fragment AddItemFragment tramite un Bundle
                    Bundle bundle = new Bundle();
                    bundle.putString("name", name);
                    bundle.putString("category", category);
                    bundle.putFloat("eanCode", eanCode);

                    AddItemFragment fragment = new AddItemFragment();
                    fragment.setArguments(bundle);

                    requireActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.main, fragment)
                            .addToBackStack(null)
                            .commit();
                    return true; // Scansione valida
                } catch (NumberFormatException e) {
                    Log.e("BarcodeScanner", "EAN non valido: " + eanCodeStr, e);
                    Toast.makeText(requireContext(), "EAN non valido", Toast.LENGTH_SHORT).show();
                }
            } else {
                Log.e("BarcodeScanner", "Formato codice a barre non valido: " + scannedCode);
                Toast.makeText(requireContext(), "Formato codice a barre non valido", Toast.LENGTH_SHORT).show();
            }
        } else {
            Log.e("BarcodeScanner", "Codice a barre non supportato: " + scannedCode);
            Toast.makeText(requireContext(), "Codice a barre non supportato", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    private void playSuccessSound() {
        MediaPlayer mediaPlayer = MediaPlayer.create(requireContext(), R.raw.scan_success);
        mediaPlayer.setOnCompletionListener(MediaPlayer::release);
        mediaPlayer.start();
    }

    private void playFailureSound() {
        MediaPlayer mediaPlayer = MediaPlayer.create(requireContext(), R.raw.scan_failure);
        mediaPlayer.setOnCompletionListener(MediaPlayer::release);
        mediaPlayer.start();
    }

    @Override
    public void onResume() {
        super.onResume();
        barcodeView.resume();
    }

    @Override
    public void onPause() {
        super.onPause();
        barcodeView.pause();
    }
}
