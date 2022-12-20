package com.example.recipeapp;

import android.content.Context;

import androidx.annotation.NonNull;

import com.example.recipeapp.listeners.requests.RandomRecipeResponseListener;
import com.example.recipeapp.listeners.requests.RecipeDetailsResponseListener;
import com.example.recipeapp.listeners.requests.RecipeResponseListener;
import com.example.recipeapp.models.RandomRecipeApiResponse;
import com.example.recipeapp.models.RecipeDetailsResponse;
import com.example.recipeapp.models.SimilarRecipeResponse;
import com.example.recipeapp.listeners.requests.SimilarRecipeResponseListener;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public class RequestManager {

    private final Context context;

    private final RecipesApi recipesApi;

    public RequestManager(Context context) {
        this.context = context;
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.spoonacular.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        this.recipesApi = retrofit.create(RecipesApi.class);
    }

    public void getRandomRecipes(RandomRecipeResponseListener listener, List<String> tags) {
        Call<RandomRecipeApiResponse> call = recipesApi.callRandomRecipe(context.getString(R.string.api_key), "10", tags);
        call.enqueue(new BaseRetrofitCallback<>(listener));
    }

    public void getRecipeDetails(RecipeDetailsResponseListener listener, int id) {
        Call<RecipeDetailsResponse> call = recipesApi.callRecipeDetails(id, context.getString(R.string.api_key));
        call.enqueue(new BaseRetrofitCallback<>(listener));
    }

    public void getSimilarRecipes(SimilarRecipeResponseListener listener, int id) {
        Call<List<SimilarRecipeResponse>> call = recipesApi.callSimilarRecipe(id, "3", context.getString(R.string.api_key));
        call.enqueue(new BaseRetrofitCallback<>(listener));
    }

    private interface RecipesApi {

        @GET("recipes/random")
        Call<RandomRecipeApiResponse> callRandomRecipe(
                @Query("apiKey") String apiKey,
                @Query("number") String number,
                @Query("tags") List<String> tags
        );

        @GET("recipes/{id}/information")
        Call<RecipeDetailsResponse> callRecipeDetails(
                @Path("id") int id,
                @Query("apiKey") String apiKey
        );

        @GET("recipes/{id}/similar")
        Call<List<SimilarRecipeResponse>> callSimilarRecipe(
                @Path("id") int id,
                @Query("number") String number,
                @Query("apiKey") String apiKey
        );
    }

    private static class BaseRetrofitCallback<R> implements Callback<R> {

        private final RecipeResponseListener<R> delegate;

        private BaseRetrofitCallback(RecipeResponseListener<R> delegate) {
            this.delegate = delegate;
        }

        @Override
        public void onResponse(@NonNull Call<R> call, Response<R> response) {
            if (!response.isSuccessful()) {
                delegate.didError(response.message());
                return;
            }
            delegate.didFetch(response.body(), response.message());
        }

        @Override
        public void onFailure(@NonNull Call<R> call, Throwable t) {
            delegate.didError(t.getMessage());
        }
    }
}
