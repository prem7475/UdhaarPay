package com.example.udhaarpay;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

public class CashbackFragment extends Fragment {

    private RecyclerView rvCashbackOffers;
    private TextView tvTotalCashback;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cashback, container, false);

        rvCashbackOffers = view.findViewById(R.id.rvCashbackOffers);
        tvTotalCashback = view.findViewById(R.id.tvTotalCashback);

        // Set up RecyclerView
        rvCashbackOffers.setLayoutManager(new LinearLayoutManager(getContext()));
        List<CashbackOffer> offers = getCashbackOffers();
        CashbackAdapter adapter = new CashbackAdapter(offers);
        rvCashbackOffers.setAdapter(adapter);

        // Calculate and display total cashback
        double totalCashback = offers.stream().mapToDouble(CashbackOffer::getCashbackAmount).sum();
        tvTotalCashback.setText(String.format("Total Cashback: ₹%.2f", totalCashback));

        return view;
    }

    private List<CashbackOffer> getCashbackOffers() {
        List<CashbackOffer> offers = new ArrayList<>();
        offers.add(new CashbackOffer("Amazon Shopping", "₹50 cashback on ₹500+ spend", 50.0, "Valid till Dec 31"));
        offers.add(new CashbackOffer("Movie Tickets", "₹25 cashback on ₹200+ booking", 25.0, "Valid till Dec 31"));
        offers.add(new CashbackOffer("Food Delivery", "₹30 cashback on ₹300+ order", 30.0, "Valid till Dec 31"));
        offers.add(new CashbackOffer("Online Recharge", "₹20 cashback on ₹200+ recharge", 20.0, "Valid till Dec 31"));
        offers.add(new CashbackOffer("Grocery Shopping", "₹40 cashback on ₹400+ purchase", 40.0, "Valid till Dec 31"));
        offers.add(new CashbackOffer("Flight Booking", "₹100 cashback on ₹1000+ booking", 100.0, "Valid till Dec 31"));
        return offers;
    }

    // Cashback Offer model class
    public static class CashbackOffer {
        private String title;
        private String description;
        private double cashbackAmount;
        private String validity;

        public CashbackOffer(String title, String description, double cashbackAmount, String validity) {
            this.title = title;
            this.description = description;
            this.cashbackAmount = cashbackAmount;
            this.validity = validity;
        }

        public String getTitle() { return title; }
        public String getDescription() { return description; }
        public double getCashbackAmount() { return cashbackAmount; }
        public String getValidity() { return validity; }
    }

    // Cashback Adapter
    public static class CashbackAdapter extends RecyclerView.Adapter<CashbackAdapter.ViewHolder> {

        private List<CashbackOffer> offers;

        public CashbackAdapter(List<CashbackOffer> offers) {
            this.offers = offers;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_cashback_offer, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            CashbackOffer offer = offers.get(position);
            holder.tvTitle.setText(offer.getTitle());
            holder.tvDescription.setText(offer.getDescription());
            holder.tvCashbackAmount.setText(String.format("₹%.0f", offer.getCashbackAmount()));
            holder.tvValidity.setText(offer.getValidity());
        }

        @Override
        public int getItemCount() {
            return offers.size();
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvTitle, tvDescription, tvCashbackAmount, tvValidity;
            MaterialCardView cardView;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                cardView = itemView.findViewById(R.id.cardView);
                tvTitle = itemView.findViewById(R.id.tvTitle);
                tvDescription = itemView.findViewById(R.id.tvDescription);
                tvCashbackAmount = itemView.findViewById(R.id.tvCashbackAmount);
                tvValidity = itemView.findViewById(R.id.tvValidity);
            }
        }
    }
}
