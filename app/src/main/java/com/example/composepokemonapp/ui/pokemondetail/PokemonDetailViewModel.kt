package com.example.composepokemonapp.ui.pokemondetail

import androidx.lifecycle.ViewModel
import com.example.composepokemonapp.data.remote.response.Pokemon
import com.example.composepokemonapp.respositories.PokemonRepository
import com.example.composepokemonapp.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import java.lang.reflect.Constructor
import javax.inject.Inject

@HiltViewModel
class PokemonDetailViewModel @Inject constructor(
    private val repository: PokemonRepository
) : ViewModel() {
    suspend fun getPokemonInfo(pokemonName: String): Resource<Pokemon> {
        return repository.getPokemonInfo(pokemonName)

    }
}