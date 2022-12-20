package com.example.recipeapp.listeners.requests;

public interface RecipeResponseListener<R> {

    void didFetch(R response, String message);

    void didError(String message);
}





