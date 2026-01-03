package com.example.udhaarpay;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.core.ExperimentalGetImage;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

@androidx.camera.core.ExperimentalGetImage

public class UpiPinBottomSheetFragment extends BottomSheetDialogFragment {

    public interface OnPinEnteredListener {
        void onPinEntered(String pin);
    }

    private OnPinEnteredListener onPinEnteredListener;
    private Runnable onPinVerifiedListener;

    public void setOnPinVerifiedListener(Runnable listener) {
        this.onPinVerifiedListener = listener;
    }

    public void setOnPinEnteredListener(OnPinEnteredListener listener) {
        this.onPinEnteredListener = listener;
    }

    private TextView tvBankName;
    private EditText etUpiPin;
    private Button btnSubmit;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_upi_pin_bottom_sheet, container, false);

        // Initialize views
        tvBankName = view.findViewById(R.id.bankName);
        etUpiPin = view.findViewById(R.id.upiPin);
        btnSubmit = view.findViewById(R.id.submit);

        // Get payment details from arguments
        Bundle args = getArguments();
        if (args != null) {
            String account = args.getString("account", "Unknown Account");
            tvBankName.setText("Enter UPI PIN for " + account);
        }

        // Set up submit button
        btnSubmit.setOnClickListener(v -> {
            String pin = etUpiPin.getText().toString().trim();
            if (pin.isEmpty()) {
                Toast.makeText(getContext(), "Please enter UPI PIN", Toast.LENGTH_SHORT).show();
                return;
            }

            if (pin.length() < 4) {
                Toast.makeText(getContext(), "UPI PIN must be at least 4 digits", Toast.LENGTH_SHORT).show();
                return;
            }

            // Simulate payment processing
            processPayment();
        });

        return view;
    }

    private void processPayment() {
        String pin = etUpiPin.getText().toString().trim();

        // Dismiss the bottom sheet
        dismiss();

        // Call the listener if set
        if (onPinVerifiedListener != null) {
            onPinVerifiedListener.run();
        } else if (onPinEnteredListener != null) {
            onPinEnteredListener.onPinEntered(pin);
        } else {
            // Navigate to payment confirmation if no listener is set
            Bundle args = getArguments();
            if (args != null && getActivity() instanceof MainActivity) {
                PaymentConfirmationFragment confirmationFragment = new PaymentConfirmationFragment();
                confirmationFragment.setArguments(args);
                ((MainActivity) getActivity()).loadFragment(confirmationFragment);
            }
        }
    }
}
