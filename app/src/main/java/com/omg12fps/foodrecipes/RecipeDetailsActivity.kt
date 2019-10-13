package com.omg12fps.foodrecipes

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_recipe_details.*
import org.json.JSONObject

class RecipeDetailsActivity : AppCompatActivity() {
    companion object {
        val RECIPE_ID = "RECIPE_ID"
    }

    lateinit var requestQueue: RequestQueue

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe_details)

        setSupportActionBar(toolbar)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val recipe_id = intent.extras?.getString(RECIPE_ID)

        recipe_id?.let{
            //load data
            requestQueue = Volley.newRequestQueue(this)
            loadRecipe(it)
        }
    }

    private fun loadRecipe(id: String) {
        val uriBuilder = Uri.Builder()
        uriBuilder.scheme("https")
            .authority("www.food2fork.com")
            .appendPath("api")
            .appendPath("get")
            .appendQueryParameter("key", BuildConfig.FOOD_API_KEY)
            .appendQueryParameter("rId", id)

        val url = uriBuilder.build().toString()
        val request = JsonObjectRequest(url, null, object : Response.Listener<JSONObject> {
            override fun onResponse(response: JSONObject?) {
                response?.let {
                    val jsonObject = it.getJSONObject("recipe")
                    recipe_title.setText(jsonObject.getString("title"))
                    val imageUrl = jsonObject.getString("image_url")
                    Picasso.get().load(imageUrl).fit().centerCrop().into(recipeImageView)

                    val ingredients = jsonObject.getJSONArray("ingredients")
                    val ingredientsList = StringBuilder()
                    ingredientsList.append("Ingredients:\n")

                    for (i in 0 until ingredients.length()){
                        ingredientsList.append("${i+1}. ${ingredients.getString(i)}\n")
                    }
                    recipe_description.setText(ingredientsList.toString())
                }
            }

        }, object : Response.ErrorListener {
            override fun onErrorResponse(error: VolleyError?) {
                Toast.makeText(this@RecipeDetailsActivity, "Couldn't load data", Toast.LENGTH_SHORT).show()
                Log.e("MainActivity", error.toString())
            }
        })

        requestQueue.add(request)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val itemId = item.itemId

        if (itemId == android.R.id.home) {
            onBackPressed()
            return true
        }

        return super.onOptionsItemSelected(item)
    }
}
