package com.falcon.notesapp.login

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.falcon.notesapp.R
import com.falcon.notesapp.databinding.FragmentSignUpBinding
import com.falcon.notesapp.models.UserRequest
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SignUpFragment : Fragment() {

    private var _binding: FragmentSignUpBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignUpBinding.inflate(inflater, container, false)
        // TODO (IF ALREADY USER CREATED (CHECK THROUGH SHARED PREF OR SO) AND SIGNOUT KE TIME SHAREDPREFF SE HTANA BHI HAI
//        findNavController().navigate(R.id.action_SignUpFragment_to_mainFragment)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.loginPanel.setOnClickListener {
            if (isNetworkAvailable(requireContext())) {
                binding.errorTextview.visibility = View.INVISIBLE
            }
            else {
                binding.errorTextview.text = "Please ensure a network connection is available for the registration process."
                binding.errorTextview.visibility = View.VISIBLE
            }

        }
        binding.authGoogle.setOnClickListener {
            Toast.makeText(requireContext(), "Coming Soon", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val networkCapabilities =
            connectivityManager.getNetworkCapabilities(network) ?: return false
        return networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
}