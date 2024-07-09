package com.falcon.notesapp.login

import android.annotation.SuppressLint
import android.app.Activity
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
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SignUpFragment : Fragment() {

    private var _binding: FragmentSignUpBinding? = null
    private val binding get() = _binding!!
    private lateinit var oneTapClient: SignInClient
    @Inject
    lateinit var tokenManager: TokenManager
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignUpBinding.inflate(inflater, container, false)
        if (tokenManager.doesUserExist()) { // IF ALREADY USER CREATED DIRECTLY REDIRECT HER TO MAIN FRAGMENT
            findNavController().navigate(R.id.action_SignUpFragment_to_mainFragment)
        }
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        oneTapClient = Identity.getSignInClient(requireActivity())
        binding.authGoogle.setOnClickListener {
            binding.continueWithGoogleLL.visibility = View.GONE
            binding.animationView.visibility = View.VISIBLE
            binding.animationView.setAnimation("loading-dots.json")
            binding.animationView.playAnimation()
            if (isNetworkAvailable(requireContext())) {
                initiateLogin()
            } else {
                showSnackBar("Login Failed. Check Your Internet Connection", activity)
                binding.continueWithGoogleLL.visibility = View.VISIBLE
                binding.animationView.visibility = View.GONE
            }
        }
    }

    private fun initiateLogin() {
        val signInRequest = BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setServerClientId(getString(R.string.your_web_client_id)) // Replace with your client ID
                    .setFilterByAuthorizedAccounts(false)
                    .build()
            )
            .setAutoSelectEnabled(true)
            .build()

        oneTapClient.beginSignIn(signInRequest)
            .addOnSuccessListener { result ->
                try {
                    startIntentSenderForResult(
                        result.pendingIntent.intentSender, REQ_ONE_TAP, null, 0, 0, 0, null
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                    showSnackBar("Login Failed: ${e.localizedMessage}", activity)
                }
            }
            .addOnFailureListener { e ->
                e.printStackTrace()
                showSnackBar("Login Failed: ${e.localizedMessage}", activity)
            }
        workAfterLogin()
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

    private fun showSnackBar(message: String?, activity: Activity?) {
        if (null != activity && null != message) {
            Snackbar.make(
                activity.findViewById(android.R.id.content),
                message, Snackbar.LENGTH_SHORT
            ).show()
        }
    }

    companion object {
        private const val REQ_ONE_TAP = 2
    }
}