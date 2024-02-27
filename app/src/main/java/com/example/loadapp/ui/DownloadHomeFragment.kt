package com.example.loadapp.ui

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.loadapp.DOWNLOAD_FILE_NAME
import com.example.loadapp.DOWNLOAD_STATUS
import com.example.loadapp.MainViewModel
import com.example.loadapp.NOTIFICATION_ACTION_CLICKED
import com.example.loadapp.R
import com.example.loadapp.databinding.FragmentDownloadHomeBinding

class DownloadHomeFragment : Fragment() {

    private lateinit var binding: FragmentDownloadHomeBinding
    private val viewModel: MainViewModel by lazy {
        ViewModelProvider(
            requireActivity(),
            ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)
        )[MainViewModel::class.java]
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(p0: Context?, p1: Intent?) {
            viewModel.onReceive(p1)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDownloadHomeBinding.inflate(inflater, container, false)
        viewModel.createChannel(getString(R.string.download_channel), getString(R.string.download))
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initListener()
        initObservers()
        val intent = requireActivity().intent
        if (intent?.action == NOTIFICATION_ACTION_CLICKED) {
            val status = intent.getIntExtra(DOWNLOAD_STATUS, 0)
            val fileName = intent.getStringExtra(DOWNLOAD_FILE_NAME)
            requireActivity().intent = null
            findNavController().navigate(
                DownloadHomeFragmentDirections.actionDownloadHomeFragmentToDetailFragment(
                    fileName,
                    status
                )
            )
        }
    }

    private fun initObservers() {
        viewModel.downloadProgressLiveData.observe(viewLifecycleOwner) {
            it?.let { downloadProgress ->
                binding.downloadCustomButton.changeButtonProgress(downloadProgress)
                if (it == 100) showToastMessage(getString(R.string.download_success))
            }
        }

        viewModel.downloadingFileLiveData.observe(viewLifecycleOwner) {
            it?.let { fileName ->
                showToastMessage(getString(R.string.downloading_please_wait, fileName))
            }
        }


    }

    private fun initListener() {
        customButtonOnClick()
    }

    private fun customButtonOnClick() {
        binding.downloadCustomButton.setOnClickListener {
            when (binding.radioGroup.checkedRadioButtonId) {
                R.id.glideRadioButton -> {
                    viewModel.download(
                        glideURL,
                        getString(R.string.app_name),
                        getString(R.string.glide)
                    )
                }

                R.id.loadAppRadioButton -> {
                    viewModel.download(
                        loadAppURL,
                        getString(R.string.app_name),
                        getString(R.string.loadApp)
                    )
                }

                R.id.retrofitRadioButton -> {
                    viewModel.download(
                        retrofitURL,
                        getString(R.string.app_name),
                        getString(R.string.retrofit)
                    )
                }

                else -> {
                    showToastMessage(getString(R.string.select_file))
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.registerReceiver(receiver)
    }

    override fun onPause() {
        super.onPause()
        viewModel.unregisterReceiver(receiver)
    }

    private fun showToastMessage(msg: String) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val bigFileTestURL = "https://link.testfile.org/PDF200MB"
        private const val retrofitURL = "https://github.com/square/retrofit"
        private const val loadAppURL =
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter"
        private const val glideURL = "https://github.com/bumptech/glide"

        fun newInstance() = DownloadHomeFragment()

    }
}