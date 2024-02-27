package com.example.loadapp.ui

import android.app.DownloadManager
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.loadapp.R
import com.example.loadapp.databinding.FragmentDetailBinding

class DetailFragment : Fragment() {

    private lateinit var binding: FragmentDetailBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDetailBinding.inflate(inflater, container, false)
        val args = DetailFragmentArgs.fromBundle(requireArguments())
        initUI(args)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initListeners()
    }

    private fun initListeners() {
        binding.button.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun initUI(args: DetailFragmentArgs) {
        binding.downloadedFileName.text = args.fileName
        binding.downloadStatus.text = when (args.downloadStatus) {
            DownloadManager.STATUS_SUCCESSFUL -> {
                binding.downloadStatus.setTextColor(resources.getColor(R.color.tealBlue))
                getString(R.string.success)
            }

            DownloadManager.STATUS_PENDING -> {
                binding.downloadStatus.setTextColor(Color.BLACK)
                getString(R.string.pending)
            }

            DownloadManager.STATUS_RUNNING -> {
                binding.downloadStatus.setTextColor(Color.BLACK)
                getString(R.string.running)
            }

            DownloadManager.STATUS_PAUSED -> {
                binding.downloadStatus.setTextColor(Color.BLACK)
                getString(R.string.paused)
            }

            DownloadManager.STATUS_FAILED -> {
                binding.downloadStatus.setTextColor(Color.RED)
                getString(R.string.success)
            }

            else -> {
                binding.downloadStatus.setTextColor(Color.RED)
                getString(R.string.fail)
            }
        }
    }

}