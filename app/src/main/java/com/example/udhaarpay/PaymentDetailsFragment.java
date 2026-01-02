package com.example.udhaarpay;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.core.ExperimentalGetImage;
import androidx.fragment.app.Fragment;

@androidx.camera.core.ExperimentalGetImage

public class PaymentDetailsFragment extends Fragment {

    private TextView tvPayeeName, tvPayeeUpiId;
    private EditText etAmount;
    private RadioGroup radioGroupAccounts;
    private Button btnProceedToPay, btnAmount100, btnAmount500, btnAmount1000;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_payment_details, container, false);

        // Initialize views
        tvPayeeName = view.findViewById(R.id.tvPayeeName);
        tvPayeeUpiId = view.findViewById(R.id.tvPayeeUpiId);
        etAmount = view.findViewById(R.id.etAmount);
        radioGroupAccounts = view.findViewById(R.id.radioGroupAccounts);
        btnProceedToPay = view.findViewById(R.id.btnProceedToPay);
        btnAmount100 = view.findViewById(R.id.btnAmount100);
        btnAmount500 = view.findViewById(R.id.btnAmount500);
        btnAmount1000 = view.findViewById(R.id.btnAmount1000);

        // Get payee details from arguments (passed from QR scan)
        Bundle args = getArguments();
        if (args != null) {
            String payeeName = args.getString("payeeName", "Unknown Merchant");
            String payeeUpiId = args.getString("payeeUpiId", "unknown@upi");

            tvPayeeName.setText(payeeName);
            tvPayeeUpiId.setText(payeeUpiId);
        }

        // Set up quick amount buttons
        setupQuickAmountButtons();

        // Set up proceed to pay button
        btnProceedToPay.setOnClickListener(v -> {
            String amount = etAmount.getText().toString().trim();
            if (amount.isEmpty()) {
                Toast.makeText(getContext(), "Please enter an amount", Toast.LENGTH_SHORT).show();
                return;
            }

            // Get selected account
            int selectedId = radioGroupAccounts.getCheckedRadioButtonId();
            RadioButton selectedRadio = view.findViewById(selectedId);
            String selectedAccount = selectedRadio.getText().toString();

            // Navigate to UPI PIN entry
            navigateToUpiPin(amount, selectedAccount);
        });

        return view;
    }

    private void setupQuickAmountButtons() {
        btnAmount100.setOnClickListener(v -> etAmount.setText("100"));
        btnAmount500.setOnClickListener(v -> etAmount.setText("500"));
        btnAmount1000.setOnClickListener(v -> etAmount.setText("1000"));
    }

    private void navigateToUpiPin(String amount, String selectedAccount) {
        // Create UPI PIN bottom sheet
        UpiPinBottomSheetFragment upiPinFragment = new UpiPinBottomSheetFragment();

        // Pass payment details to the bottom sheet
        Bundle args = new Bundle();
        args.putString("amount", amount);
        args.putString("account", selectedAccount);
        args.putString("payeeName", tvPayeeName.getText().toString());
        args.putString("payeeUpiId", tvPayeeUpiId.getText().toString());
        upiPinFragment.setArguments(args);

        upiPinFragment.show(getParentFragmentManager(), upiPinFragment.getTag());
    }
}
