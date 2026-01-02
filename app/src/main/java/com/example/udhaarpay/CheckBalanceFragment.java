package com.example.udhaarpay;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.core.ExperimentalGetImage;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

@androidx.camera.core.ExperimentalGetImage

public class CheckBalanceFragment extends Fragment {

    private RecyclerView rvBankAccounts;
    private LinkedBankAccountsAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_check_balance, container, false);

        rvBankAccounts = view.findViewById(R.id.rvBankAccounts);
        rvBankAccounts.setLayoutManager(new LinearLayoutManager(getContext()));

        List<PaymentSettingsActivity.BankAccount> accounts = getLinkedAccounts();
        adapter = new LinkedBankAccountsAdapter(accounts, this::onAccountClick);
        rvBankAccounts.setAdapter(adapter);

        return view;
    }

    private void onAccountClick(PaymentSettingsActivity.BankAccount account) {
        // Show UPI PIN bottom sheet for balance check
        UpiPinBottomSheetFragment pinFragment = new UpiPinBottomSheetFragment();
        pinFragment.setOnPinEnteredListener(pin -> {
            // In a real app, this would verify the PIN and fetch balance
            Toast.makeText(getContext(), "Balance for " + account.getBankName() + ": ₹ 25,000.00", Toast.LENGTH_LONG).show();
        });
        pinFragment.show(getParentFragmentManager(), "UpiPinBottomSheet");
    }

    private List<PaymentSettingsActivity.BankAccount> getLinkedAccounts() {
        List<PaymentSettingsActivity.BankAccount> accounts = new ArrayList<>();
        accounts.add(new PaymentSettingsActivity.BankAccount("1", "State Bank of India", "****1234", true));
        accounts.add(new PaymentSettingsActivity.BankAccount("2", "HDFC Bank", "****5678", false));
        accounts.add(new PaymentSettingsActivity.BankAccount("3", "ICICI Bank", "****9012", false));
        return accounts;
    }
}
