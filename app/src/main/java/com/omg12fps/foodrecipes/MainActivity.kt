package com.omg12fps.foodrecipes

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.omg12fps.foodrecipes.RecipeDetailsActivity.Companion.RECIPE_ID
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

    lateinit var requestQueue: RequestQueue
    val recipes = ArrayList<Recipe>()

    lateinit var adapter : RecipeRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        requestQueue = Volley.newRequestQueue(this)

        adapter = RecipeRecyclerAdapter(recipes, object: OnRecipeSelected {
            override fun onSelected(recipe: Recipe) {
                val intent = Intent(this@MainActivity, RecipeDetailsActivity::class.java)
                intent.putExtra(RECIPE_ID, recipe.id)
                startActivity(intent)
            }
        })

        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        getRecipes()
    }

    private fun getRecipes() {
        val uriBuilder = Uri.Builder()
        uriBuilder.scheme("https")
            .authority("www.food2fork.com")
            .appendPath("api")
            .appendPath("search")
            .appendQueryParameter("key", BuildConfig.FOOD_API_KEY)

        val url = uriBuilder.build().toString()

        val request = JsonObjectRequest(url, null, object : Response.Listener<JSONObject> {
            override fun onResponse(response: JSONObject?) {
                response?.let {
                    val jsonArray = it.getJSONArray("recipes")
                    for (i in 0 until jsonArray.length()){
                        val jsonObject = jsonArray.getJSONObject(i)
                        val id = jsonObject.getString("recipe_id")
                        val title = jsonObject.getString("title")
                        val imageUrl = jsonObject.getString("image_url")
                        val recipe = Recipe(id, title, imageUrl)
                        recipes.add(recipe)
                    }
                    adapter.notifyDataSetChanged()
                }
            }

        }, object : Response.ErrorListener {
            override fun onErrorResponse(error: VolleyError?) {
                Toast.makeText(this@MainActivity, "Couldn't load data", Toast.LENGTH_SHORT).show()
                Log.e("MainActivity", error.toString())
            }
        })

        requestQueue.add(request)
    }
}
