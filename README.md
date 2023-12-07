# Developer Info

All copyright reserved @Nimesh Vasani 

[GitHub Profile](https://github.com/NimeshVasani)

[Linked-In](https://www.linkedin.com/in/nimesh-vasani-99b642154/)

[Stack Overflow](https://stackoverflow.com/users/16579306/nimesh-vasani)

# About The App

Compose Pokemon app is for all Pokemon lovers like myself.

Built with  : [Jetpack Compose](https://developer.android.com/jetpack/compose?gclid=CjwKCAiA1MCrBhAoEiwAC2d64eMmRz-en69toTl7z7D5r9XuVPnzQWgnSYqAIOIBzJFE5QfNrHFRFBoCNs8QAvD_BwE&gclsrc=aw.ds),[MVVM](https://developer.android.com/topic/libraries/architecture/viewmodel?gclid=CjwKCAjw5dqgBhBNEiwA7PryaEGGNXBuF_269i5vAml9SedixRgYXYfktdB8NOZm__qJWmdN6hpUahoC2IQQAvD_BwE&gclsrc=aw.ds#kotlin_1), [Retrofit](https://square.github.io/retrofit/), [Room](https://developer.android.com/training/data-storage/room),[Glide](https://github.com/bumptech/glide) and [Dagger Hilt](https://developer.android.com/training/dependency-injection/hilt-android) with Proper [Navigation UI](https://developer.android.com/guide/navigation/navigation-getting-started).

![alt text](https://github.com/NimeshVasani/My_Cooking_Gallary/blob/main/snapshots/final_snap_shot.png)

App use [Poke Api]([https://rapidapi.com/apidojo/api/tasty/](https://pokeapi.co)), you no need to generate your own Api key/Sign In.

To Update the Api key in a project to make it work follow the step: goto `"others"  package > goto "Constant.kt" class > update the value of "const val API_Key"` [Constants.kt](app/src/main/java/diamondcraft/devs/mycookinggallary/other/Constants.kt)


```kotlin
package diamondcraft.devs.mycookinggallary.other

object Constants {
    const val DATABASE_NAME = "Cooking_database"
    const val TABLE_NAME = "Cooking_table"

    const val BASE_URL = "https://tasty.p.rapidapi.com/"
    const val API_KEY = "PASTE_YOUR_API_KEY"
}
```

# Architecture & Work-Flow
This app use MVVM architecture skeleton and room database for saving recipes in local database.

![alt text](https://github.com/NimeshVasani/My_Cooking_Gallary/blob/main/snapshots/architecture.png)

As a Dependency injection, We use Dagger Hilt to set Up Singleton Object Pattern Throughout the whole Project.

in the `di` package we have 2 modules : 1. [ApiModule](app/src/main/java/diamondcraft/devs/mycookinggallary/di/ApiModule.kt)
 and 2. [AppModule](app/src/main/java/diamondcraft/devs/mycookinggallary/di/AppModule.kt)

We do have different types of [Models](app/src/main/java/diamondcraft/devs/mycookinggallary/models) for Handling Api Data set.

## Special Conversion Function
```kotlin
fun fromVulgarFraction(number1: String): Double {
        val number = number1.filterNot { it.isWhitespace() }

        val items = number.split("""\d""".toRegex()).filterNot { it.isEmpty() }
        val mixed: String?
        val fraction = mutableListOf<String>()
        return if (items.isNotEmpty()) {
            mixed = items.first()
            fraction.addAll(Normalizer.normalize(mixed, Normalizer.Form.NFKC).split("\u2044"))
            val decimal = fraction[0].toInt().toDouble() / fraction[1].toDouble()
            val result = """\d+""".toRegex().find(number)
            if (result != null) {
                result.value.toDouble() + decimal
            } else {
                decimal
            }
        } else {
            number.toDouble()
        }
    }
  ```
# Api Response and Room Database 
### Cooking API class
```kotlin
package diamondcraft.devs.mycookinggallary.api

import diamondcraft.devs.mycookinggallary.models.CookingFeedResponse
import diamondcraft.devs.mycookinggallary.models.CookingResponse
import diamondcraft.devs.mycookinggallary.models.FeedResponseInner
import diamondcraft.devs.mycookinggallary.other.Constants.API_KEY
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface CookingApi {

    @Headers(
        "X-RapidAPI-Key: $API_KEY",
        "X-RapidAPI-Host: tasty.p.rapidapi.com"
    )
    @GET("recipes/list")
    suspend fun getAllRecipes(
        @Query("size")
        size: Int = 300

    ): Response<CookingResponse>

    @Headers(
        "X-RapidAPI-Key: $API_KEY",
        "X-RapidAPI-Host: tasty.p.rapidapi.com"
    )
    @GET("feeds/list")
    suspend fun getAllFeeds(
        @Query("X-RapidAPI-Key")
        apiKey: String = API_KEY
    ): Response<CookingFeedResponse>
}
```

To handle the Response We created Some functions in [Repository](app/src/main/java/diamondcraft/devs/mycookinggallary/repositories/CookingRepository.kt).

And Load particular Data As a live Data in [ViewModels](app/src/main/java/diamondcraft/devs/mycookinggallary/viewmodels/CookingViewModel.kt) we create different fucntions. for ex : 
```kotlin
    private fun handleAllRecipesResponse(response: Response<CookingResponse>): Resources<CookingResponse> {
        if (response.isSuccessful) {
            response.body()?.let { cookingResponse ->

                if (allRecipesResponse == null) {
                    allRecipesResponse = cookingResponse
                } else {
                    val oldRecipes = allRecipesResponse?.results
                    val newRecipes = cookingResponse.results

                    oldRecipes?.addAll(newRecipes)

                }
                Log.d("response", response.body().toString())
                return Resources.Success(allRecipesResponse ?: cookingResponse)
            }
        }
        Log.d("response", "fail")

        return Resources.Error(response.message())
    }
```
Handle Success, Error, and Loading State : 
```kotlin

sealed class Resources<T>(
    val data: T? = null,
    val message: String? = null
) {
    class Success<T>(data: T) : Resources<T>(data)
    class Error<T>(message: String, data: T? = null) : Resources<T>(data, message)
    class Loading<T> : Resources<T>()
}
```
# Adapters for RecyclerViews 

All [Adapters](app/src/main/java/diamondcraft/devs/mycookinggallary/adapters) built with diffUtil, so we can submit data as it load by viewModel Observers.

diffUtil Setup in Adapter.kt class.
i.e.

```kotlin
  private var differCallback = object : DiffUtil.ItemCallback<Cooking>() {
        override fun areItemsTheSame(
            oldItem: Cooking, newItem: Cooking
        ): Boolean {
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(
            oldItem: Cooking, newItem: Cooking
        ): Boolean {
            return oldItem == newItem
        }
    }

    var asyncDiffer: AsyncListDiffer<Cooking> = AsyncListDiffer(this, differCallback)
```
and in Activity/Fragment class...
```kotlin
 adapter.differ.submitList(YOUR_LIST)
 ```

# Thank You for Your Interest!! Enjoy. 

Dm for inquiries/Collaboration [Nimesh Vasani](https://www.linkedin.com/in/nimesh-vasani-99b642154)

