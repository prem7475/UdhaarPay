package com.example.udhaarpay.ui.dashboard

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.udhaarpay.R

class ServiceAdapter(
    private val services: List<Service>,
    private val onClick: (Service) -> Unit
) : RecyclerView.Adapter<ServiceAdapter.ServiceViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServiceViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_service, parent, false)
        return ServiceViewHolder(view)
    }

    override fun onBindViewHolder(holder: ServiceViewHolder, position: Int) {
        val service = services[position]
        holder.bind(service)
        holder.itemView.setOnClickListener { onClick(service) }
    }

    override fun getItemCount(): Int = services.size

    class ServiceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val serviceIcon: ImageView = itemView.findViewById(R.id.serviceIcon)
        private val serviceName: TextView = itemView.findViewById(R.id.serviceName)

        fun bind(service: Service) {
            serviceIcon.setImageResource(service.iconRes)
            serviceName.text = service.name
        }
    }
}