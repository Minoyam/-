package com.cnm.umbrellaalarm.adapter

import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.cnm.umbrellaalarm.R
import com.cnm.umbrellaalarm.data.model.NaverGeocodeResponse
import com.cnm.umbrellaalarm.data.model.WeatherResponse

@BindingAdapter("bind:bindOnEditorActionListener")
fun bindOnEditorActionListener(editText: EditText, click: (() -> Unit)) {
    editText.setOnEditorActionListener { _, i, _ ->
        when (i) {
            EditorInfo.IME_ACTION_SEARCH -> {
                click()
            }
        }
        true
    }
}

@BindingAdapter("bind:bindSetImage")
fun ImageView.bindSetImage(item: WeatherResponse?) {
    when (item?.current?.weather?.get(0)?.main) {
        "Clear" -> {
            setImageResource(R.drawable.ic_clear)
        }
        "Clouds" -> {
            setImageResource(R.drawable.ic_cloud)
        }
        "Snow" -> {
            setImageResource(R.drawable.ic_snow)
        }
        "Rain", "Drizzle", "Thunderstorm" -> {
            setImageResource(R.drawable.ic_rain)
        }
        else -> {
            setImageResource(R.drawable.ic_fog)
        }
    }
}

@BindingAdapter("bind:bindSetTemp")
fun TextView.bindSetTemp(item: WeatherResponse?) {
    text = String.format(resources.getString(R.string.tv_temp), item?.current?.temp)
}
@BindingAdapter("bind:bindSetAddressItem")
fun RecyclerView.bindSetAddressItem(items: List<NaverGeocodeResponse.Addresse>?) {
    if (adapter is AddressAdapter)
        items?.let {
            (adapter as AddressAdapter).setItem(it)
        }
}

