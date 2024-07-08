package com.falcon.notesapp.login

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.falcon.notesapp.R
import com.falcon.notesapp.databinding.FragmentSignUpBinding
import com.falcon.notesapp.utils.TokenManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SignUpFragment : Fragment() {

    private var _binding: FragmentSignUpBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var tokenManager: TokenManager
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignUpBinding.inflate(inflater, container, false)
        if (tokenManager.doesUserExist()) { // IF ALREADY USER CREATED DIRECTLY REDIRECT HER TO MAINFRAGMENT
            findNavController().navigate(R.id.action_SignUpFragment_to_mainFragment)
        }
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.authGoogle.setOnClickListener {
            if (isNetworkAvailable(requireContext())) {
                initiateLogin(workAfterLogin())
            }
        }
    }

    private fun initiateLogin(workAfterLogin: Unit) {
        TODO("Not yet implemented")
    }

    private fun workAfterLogin() {
        tokenManager.saveUserExistance()
        findNavController().navigate(R.id.action_SignUpFragment_to_mainFragment)
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