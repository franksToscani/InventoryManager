package com.example.inventorymanager.ui.barcode;

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
                    interpretScannedCode(scannedCode);
                }
            }
        });
    }

    /*Descrizione del Flusso
Scansione del Codice a Barre poi Il BarcodeView cattura un codice a barre e chiama la callback barcodeResult.
dopo ho fatto una Chiamata a interpretScannedCode:
Una volta ottenuto il codice scansionato (result.getText()), interpretScannedCode  interpreta il contenuto del codice a barre.
Navigazione a AddItemFragment:

Il metodo interpretScannedCode:
Divide il codice a barre nei campi (Nome, Quantità, Categoria).
Passa questi campi a un Bundle.
Naviga a AddItemFragment, passando i dati tramite il Bundle.*/
    private void interpretScannedCode(String scannedCode) {
        if (scannedCode.contains(",")) {
            String[] parts = scannedCode.split(",");
            if (parts.length == 3) {
                String name = parts[0].trim();
                String quantityStr = parts[1].trim();
                String category = parts[2].trim();

                Bundle bundle = new Bundle();
                bundle.putString("name", name);
                bundle.putString("quantity", quantityStr);
                bundle.putString("category", category);

                AddItemFragment fragment = new AddItemFragment();
                fragment.setArguments(bundle);

                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.main, fragment)
                        .addToBackStack(null)
                        .commit();
            } else {
                Toast.makeText(requireContext(), "Formato codice a barre non valido", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(requireContext(), "Codice a barre non supportato", Toast.LENGTH_SHORT).show();
        }
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
