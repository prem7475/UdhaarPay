package com.example.udhaarpay;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BankFragment extends Fragment {

    private RecyclerView rvLinkedAccounts;
    private Button btnAddBank;
    private LinkedBankAccountsAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bank, container, false);

        rvLinkedAccounts = view.findViewById(R.id.linkedAccountsRecyclerView);
        btnAddBank = view.findViewById(R.id.btnAddBank);

        // Setup RecyclerView
        rvLinkedAccounts.setLayoutManager(new LinearLayoutManager(getContext()));
        List<PaymentSettingsActivity.BankAccount> accounts = getLinkedAccounts();
        adapter = new LinkedBankAccountsAdapter(accounts, this::onAccountClick);
        rvLinkedAccounts.setAdapter(adapter);

        // Setup Add Bank button
        btnAddBank.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), BankSelectionActivity.class);
            startActivity(intent);
        });

        return view;
    }

    private void onAccountClick(PaymentSettingsActivity.BankAccount account) {
        Intent intent = new Intent(getContext(), AccountDetailsActivity.class);
        intent.putExtra("account_id", account.getId());
        intent.putExtra("bank_name", account.getBankName());
        intent.putExtra("account_number", account.getAccountNumber());
        intent.putExtra("is_default", account.isDefault());
        startActivity(intent);
    }

    private List<PaymentSettingsActivity.BankAccount> getLinkedAccounts() {
        List<PaymentSettingsActivity.BankAccount> accounts = new ArrayList<>();
        SharedPreferences prefs = getActivity().getSharedPreferences("BankAccounts", Context.MODE_PRIVATE);
        Map<String, ?> allEntries = prefs.getAll();

        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            String key = entry.getKey();
            if (key.startsWith("account_") && key.endsWith("_bankName")) {
                String accountId = key.substring(0, key.lastIndexOf("_"));
                String bankName = prefs.getString(accountId + "_bankName", "");
                String accountNumber = prefs.getString(accountId + "_accountNumber", "");
                String maskedAccount = accountNumber.length() > 4 ?
                    "****" + accountNumber.substring(accountNumber.length() - 4) : accountNumber;
                boolean isDefault = prefs.getBoolean(accountId + "_isDefault", false);

                accounts.add(new PaymentSettingsActivity.BankAccount(accountId, bankName, maskedAccount, isDefault));
            }
        }

        // If no accounts found, return empty list
        return accounts;
    }
}
