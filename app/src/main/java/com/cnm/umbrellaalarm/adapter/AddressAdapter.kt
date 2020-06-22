package com.cnm.umbrellaalarm.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cnm.umbrellaalarm.R
import com.cnm.umbrellaalarm.data.model.NaverGeocodeResponse
import kotlinx.android.synthetic.main.item_address.view.*

class AddressAdapter(private val onClickAction: (NaverGeocodeResponse.Addresse) -> Unit) : RecyclerView.Adapter<AddressAdapter.AddressViewHolder>() {
    private val items = mutableListOf<NaverGeocodeResponse.Addresse>()

    fun setItem(list: List<NaverGeocodeResponse.Addresse>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddressViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_address, parent, false)
        return AddressViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: AddressViewHolder, position: Int) {
        holder.bind(items[position])
    }

    inner class AddressViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        init {
            view.setOnClickListener {
                val item = items[adapterPosition]
                onClickAction(item)
            }
        }
            fun bind(item: NaverGeocodeResponse.Addresse) {
                itemView.tv_road_name.text = item.roadAddress
                itemView.tv_number_name.text = item.jibunAddress
            }
    }
}