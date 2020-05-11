package com.supesuba.smoothing.presentation.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.supesuba.smoothing.R
import com.supesuba.smoothing.model.repository.ModelInfo
import kotlinx.android.synthetic.main.item_model.view.*

class ModelAdapter : RecyclerView.Adapter<ModelAdapter.ActivitiesViewHolder>() {

    private var items: List<ModelInfo> = listOf()

    fun setItems(items: List<ModelInfo>) {
        this.items = items
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = items.count()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActivitiesViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_model, parent, false)

        return ActivitiesViewHolder(view)
    }

    override fun onBindViewHolder(holder: ActivitiesViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item)
    }

    inner class ActivitiesViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

        fun bind(data: ModelInfo) {
            view.modelItemTV.text = data.name
        }

    }
}