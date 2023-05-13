package com.example.composepokemonapp.respositories

import com.example.composepokemonapp.data.remote.PokeApi
import com.example.composepokemonapp.data.remote.response.Pokemon
import com.example.composepokemonapp.data.remote.response.PokemonList
import com.example.composepokemonapp.utils.Resource
import dagger.hilt.android.scopes.ActivityScoped
import javax.inject.Inject

@ActivityScoped
class PokemonRepository @Inject constructor(
    val api: PokeApi
) {
    suspend fun getPokemonList(limits: Int, offset: Int): Resource<PokemonList> {
        val response = try {
            api.getPokemonList(limits, offset)
        } catch (e:Exception){
            return Resource.Error("Unspecified Error Occurred")
        }
        return Resource.Success(response)
    }

    suspend fun getPokemonInfo(pokemonName : String): Resource<Pokemon> {
        val response = try {
            api.getPokemonInfo(pokemonName)
        } catch (e:Exception){
            return Resource.Error("Unspecified Error Occurred")
        }
        return Resource.Success(response)
    }
}