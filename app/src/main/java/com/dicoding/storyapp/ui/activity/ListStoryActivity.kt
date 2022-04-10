package com.dicoding.storyapp.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.storyapp.MainActivity
import com.dicoding.storyapp.R
import com.dicoding.storyapp.data.model.UserModel
import com.dicoding.storyapp.databinding.ActivityListStoryBinding
import com.dicoding.storyapp.ui.adapter.StoryAdapter
import com.dicoding.storyapp.ui.viewmodel.ListStoryViewModel
import com.google.android.material.snackbar.Snackbar


class ListStoryActivity : AppCompatActivity() {

  private var _binding: ActivityListStoryBinding? = null
  private val binding get() = _binding

  private lateinit var user: UserModel
  private lateinit var adapter: StoryAdapter

  private val viewModel by viewModels<ListStoryViewModel>()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    _binding = ActivityListStoryBinding.inflate(layoutInflater)
    setContentView(binding?.root)

    setupToolbar()
    addStoryAction()

    user = intent.getParcelableExtra(EXTRA_USER)!!
    viewModel.showListStory(user.token)

    setListStory()
    adapter = StoryAdapter()

    showSnackBar()

    binding?.rvStory?.layoutManager = LinearLayoutManager(this)
    binding?.rvStory?.setHasFixedSize(true)
    binding?.rvStory?.adapter = adapter

    showLoading()
  }

  private fun setupToolbar(){
    setSupportActionBar(binding?.toolbar)
    supportActionBar?.setDisplayHomeAsUpEnabled(true)
    supportActionBar?.setDisplayShowHomeEnabled(true)
  }

  override fun onSupportNavigateUp(): Boolean {
    startActivity(Intent(this, MainActivity::class.java))
    finish()
    return true
  }

  private fun showSnackBar() {
    viewModel.snackBarText.observe(this) {
      it.getContentIfNotHandled()?.let { snackBarText ->
        Snackbar.make(
          findViewById(R.id.rv_story),
          snackBarText,
          Snackbar.LENGTH_SHORT
        ).show()
      }
    }
  }

  private fun showLoading() {
    viewModel.isLoading.observe(this) {
      binding?.apply {
        if (it) {
          progressBar.visibility = View.VISIBLE
          rvStory.visibility = View.INVISIBLE
        } else {
          progressBar.visibility = View.GONE
          rvStory.visibility = View.VISIBLE
        }
      }
    }
  }

  override fun onDestroy() {
    super.onDestroy()
    _binding = null
  }

  private fun setListStory() {
    viewModel.itemStory.observe(this) {
      adapter.setListStory(it)
    }
  }

  override fun onResume() {
    super.onResume()
    setListStory()
  }

  private fun addStoryAction(){
    binding?.ivAddStory?.setOnClickListener {
      val moveToAddStoryActivity = Intent(this, AddStoryActivity::class.java)
      moveToAddStoryActivity.putExtra(AddStoryActivity.EXTRA_USER, user)
      startActivity(moveToAddStoryActivity)
    }
  }

  companion object {
    const val EXTRA_USER = "user"
  }
}