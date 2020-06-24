package com.cnm.umbrellaalarm.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.cnm.umbrellaalarm.R
import com.cnm.umbrellaalarm.data.model.NaverGeocodeResponse
import com.cnm.umbrellaalarm.databinding.ItemAddressBinding

class AddressAdapter(private val onClickAction: (NaverGeocodeResponse.Addresse) -> Unit) :
    RecyclerView.Adapter<AddressAdapter.AddressViewHolder>() {
    private val items = mutableListOf<NaverGeocodeResponse.Addresse>()

    fun setItem(list: List<NaverGeocodeResponse.Addresse>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddressViewHolder {
        val binding = DataBindingUtil.inflate<ItemAddressBinding>(
            LayoutInflater.from(parent.context),
            R.layout.item_address,
            parent,
            false
        )
        return AddressViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: AddressViewHolder, position: Int) {
        holder.bind(items[position])
    }

    inner class AddressViewHolder(private val binding: ItemAddressBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                val item = items[adapterPosition]
                onClickAction(item)
            }
        }
        fun bind(item: NaverGeocodeResponse.Addresse) {
            binding.items = item
        }
    }
}