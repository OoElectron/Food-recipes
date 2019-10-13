package com.omg12fps.foodrecipes

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.recipe_item.view.*

class RecipeRecyclerAdapter(
    val recipes: ArrayList<Recipe>,
    val listener: OnRecipeSelected
) : RecyclerView.Adapter<RecipeRecyclerAdapter.RecyclerViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recipe_item, parent, false)
        return RecyclerViewHolder(view)
    }

    override fun getItemCount() = recipes.size

    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
        val recipe = recipes[position]
        holder.title.setText(recipe.title)
        holder.itemView.setOnClickListener(View.OnClickListener {
            it?.let{
                listener.onSelected(recipe)
            }
        })
        Picasso.get().load(recipe.imageURL).fit().centerCrop().into(holder.image)
    }

    class RecyclerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val image: ImageView = itemView.foodImageView;
        val title: TextView = itemView.title;
    }
}