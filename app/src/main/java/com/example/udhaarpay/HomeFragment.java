package com.example.udhaarpay;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.core.ExperimentalGetImage;
import androidx.fragment.app.Fragment;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

@androidx.camera.core.ExperimentalGetImage
public class HomeFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Setup click listeners for service cards
        setupServiceClickListeners(view);

        return view;
    }

    private void setupServiceClickListeners(View view) {
        // Scan and Pay
        view.findViewById(R.id.scanQrLayout).setOnClickListener(v -> startActivityWithAnimation(new Intent(getActivity(), ScanPayActivity.class)));
        view.findViewById(R.id.payBillsLayout).setOnClickListener(v -> startActivityWithAnimation(new Intent(getActivity(), PayBillsActivity.class)));
        view.findViewById(R.id.historyLayout).setOnClickListener(v -> startActivityWithAnimation(new Intent(getActivity(), HistoryActivity.class)));
        view.findViewById(R.id.balanceLayout).setOnClickListener(v -> startActivityWithAnimation(new Intent(getActivity(), WalletActivity.class)));
        view.findViewById(R.id.walletLayout).setOnClickListener(v -> startActivityWithAnimation(new Intent(getActivity(), WalletActivity.class)));
        view.findViewById(R.id.addMoneyLayout).setOnClickListener(v -> startActivityWithAnimation(new Intent(getActivity(), AddMoneyActivity.class)));
        view.findViewById(R.id.sendMoneyLayout).setOnClickListener(v -> startActivityWithAnimation(new Intent(getActivity(), SendMoneyActivity.class)));
        // `receiveMoneyLayout` may be absent in some layout variants; resolve id at runtime
        int receiveId = getResources().getIdentifier("receiveMoneyLayout", "id", getActivity().getPackageName());
        if (receiveId != 0) {
            View receiveView = view.findViewById(receiveId);
            if (receiveView != null) {
                receiveView.setOnClickListener(v -> startActivityWithAnimation(new Intent(getActivity(), ReceiveMoneyActivity.class)));
            }
        }

        // Recharges
        view.findViewById(R.id.mobileRechargeLayout).setOnClickListener(v -> startActivityWithAnimation(new Intent(getActivity(), MobileRechargeActivity.class)));
        view.findViewById(R.id.dthRechargeLayout).setOnClickListener(v -> startActivityWithAnimation(new Intent(getActivity(), DTHRechargeActivity.class)));
        view.findViewById(R.id.electricityBillLayout).setOnClickListener(v -> startActivityWithAnimation(new Intent(getActivity(), ElectricityBillActivity.class)));
        view.findViewById(R.id.gasBillLayout).setOnClickListener(v -> startActivityWithAnimation(new Intent(getActivity(), GasBillActivity.class)));
        view.findViewById(R.id.waterBillLayout).setOnClickListener(v -> startActivityWithAnimation(new Intent(getActivity(), WaterBillActivity.class)));
        view.findViewById(R.id.internetLayout).setOnClickListener(v -> startActivityWithAnimation(new Intent(getActivity(), InternetRechargeActivity.class)));
        view.findViewById(R.id.broadbandLayout).setOnClickListener(v -> startActivityWithAnimation(new Intent(getActivity(), BroadbandRechargeActivity.class)));
        view.findViewById(R.id.moreRechargesLayout).setOnClickListener(v -> Toast.makeText(getActivity(), "Coming soon!", Toast.LENGTH_SHORT).show());

        // Tickets Booking
        view.findViewById(R.id.busTicketsLayout).setOnClickListener(v -> startActivityWithAnimation(new Intent(getActivity(), BusTicketBookingActivity.class)));
        view.findViewById(R.id.trainTicketsLayout).setOnClickListener(v -> startActivityWithAnimation(new Intent(getActivity(), TicketBookingActivity.class)));
        view.findViewById(R.id.flightTicketsLayout).setOnClickListener(v -> Toast.makeText(getActivity(), "Coming soon!", Toast.LENGTH_SHORT).show());
        view.findViewById(R.id.movieTicketsLayout).setOnClickListener(v -> Toast.makeText(getActivity(), "Coming soon!", Toast.LENGTH_SHORT).show());
        view.findViewById(R.id.eventTicketsLayout).setOnClickListener(v -> Toast.makeText(getActivity(), "Coming soon!", Toast.LENGTH_SHORT).show());
        view.findViewById(R.id.hotelBookingLayout).setOnClickListener(v -> Toast.makeText(getActivity(), "Coming soon!", Toast.LENGTH_SHORT).show());
        view.findViewById(R.id.cabBookingLayout).setOnClickListener(v -> Toast.makeText(getActivity(), "Coming soon!", Toast.LENGTH_SHORT).show());
        view.findViewById(R.id.moreTicketsLayout).setOnClickListener(v -> Toast.makeText(getActivity(), "Coming soon!", Toast.LENGTH_SHORT).show());

        // Investing
        view.findViewById(R.id.stocksLayout).setOnClickListener(v -> startActivityWithAnimation(new Intent(getActivity(), InvestingActivity.class)));
        view.findViewById(R.id.mutualFundsLayout).setOnClickListener(v -> Toast.makeText(getActivity(), "Coming soon!", Toast.LENGTH_SHORT).show());
        view.findViewById(R.id.sipLayout).setOnClickListener(v -> Toast.makeText(getActivity(), "Coming soon!", Toast.LENGTH_SHORT).show());
        view.findViewById(R.id.goldLayout).setOnClickListener(v -> Toast.makeText(getActivity(), "Coming soon!", Toast.LENGTH_SHORT).show());
        view.findViewById(R.id.insuranceLayout).setOnClickListener(v -> Toast.makeText(getActivity(), "Coming soon!", Toast.LENGTH_SHORT).show());
        view.findViewById(R.id.fdLayout).setOnClickListener(v -> Toast.makeText(getActivity(), "Coming soon!", Toast.LENGTH_SHORT).show());
        view.findViewById(R.id.rdLayout).setOnClickListener(v -> Toast.makeText(getActivity(), "Coming soon!", Toast.LENGTH_SHORT).show());
        view.findViewById(R.id.dematAccountLayout).setOnClickListener(v -> startActivityWithAnimation(new Intent(getActivity(), DematAccountActivity.class)));

        // Financial Services
        view.findViewById(R.id.loanApplicationLayout).setOnClickListener(v -> startActivityWithAnimation(new Intent(getActivity(), LoanApplicationActivity.class)));
        view.findViewById(R.id.creditCardApplicationLayout).setOnClickListener(v -> startActivityWithAnimation(new Intent(getActivity(), CreditCardApplicationActivity.class)));
        view.findViewById(R.id.emiCalculatorLayout).setOnClickListener(v -> startActivityWithAnimation(new Intent(getActivity(), EMICalculatorActivity.class)));
        view.findViewById(R.id.creditScoreLayout).setOnClickListener(v -> startActivityWithAnimation(new Intent(getActivity(), CreditScoreActivity.class)));
        view.findViewById(R.id.budgetTrackerLayout).setOnClickListener(v -> startActivityWithAnimation(new Intent(getActivity(), BudgetTrackerActivity.class)));
        view.findViewById(R.id.expenseManagerLayout).setOnClickListener(v -> startActivityWithAnimation(new Intent(getActivity(), ExpenseManagerActivity.class)));
        view.findViewById(R.id.supportLayout).setOnClickListener(v -> startActivityWithAnimation(new Intent(getActivity(), ChatSupportActivity.class)));
        view.findViewById(R.id.moreServicesLayout).setOnClickListener(v -> Toast.makeText(getActivity(), "Coming soon!", Toast.LENGTH_SHORT).show());
    }

    private void startActivityWithAnimation(Intent intent) {
        ActivityOptions options = ActivityOptions.makeCustomAnimation(getActivity(), android.R.anim.fade_in, android.R.anim.fade_out);
        startActivity(intent, options.toBundle());
    }
}
