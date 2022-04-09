package com.dicoding.storyapp.ui.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dicoding.storyapp.data.remote.response.AllStoriesResponse
import com.dicoding.storyapp.data.remote.response.ListStoryItem
import com.dicoding.storyapp.data.remote.retrofit.ApiConfig
import com.dicoding.storyapp.helper.Event
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ListStoryViewModel : ViewModel() {
  private val _itemStory = MutableLiveData<List<ListStoryItem>>()
  val itemStory: LiveData<List<ListStoryItem>> = _itemStory

  private val _isLoading = MutableLiveData<Boolean>()
  val isLoading: LiveData<Boolean> = _isLoading

  private val _snackBarText = MutableLiveData<Event<String>>()
  val snackBarText: LiveData<Event<String>> = _snackBarText

  fun showListStory(token: String) {
    _isLoading.value = true

    val client = ApiConfig.getApiService().getAllStories(token)
    client.enqueue(object : Callback<AllStoriesResponse> {
      override fun onResponse(
        call: Call<AllStoriesResponse>,
        response: Response<AllStoriesResponse>
      ) {
        _isLoading.value = false
        if (response.isSuccessful) {
          val responseBody = response.body()
          if (responseBody != null) {
            if (!responseBody.error) {
              _snackBarText.value = Event(NO_STORY)
            }
            _itemStory.value = response.body()?.listStory
          }
        } else {
          Log.e(TAG, "onFailure: ${response.message()}")
          _snackBarText.value = Event(FAILED)
        }
      }

      override fun onFailure(call: Call<AllStoriesResponse>, t: Throwable) {
        _isLoading.value = false
        Log.e(TAG, "onFailure: ${t.message}")
        _snackBarText.value = Event(FAILED)
      }
    })
  }

  companion object {
    private const val TAG = "ListStoryViewModel"
    private const val FAILED = "Connection Failed"
    private const val NO_STORY = "Story Not Found"
  }
}