package com.assignment.okpassignment.fragments

import android.os.Bundle
import android.util.Log
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.assignment.okpassignment.R
import com.assignment.okpassignment.databinding.FragmentVerificationBinding
import com.assignment.okpassignment.repository.AuthRepository

class VerificationFragment : Fragment() {

    private var _binding: FragmentVerificationBinding? = null
    private val binding get() = _binding!!

    private var email: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        email = arguments?.getString("email")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVerificationBinding.inflate(inflater, container, false)
        return binding.root
    }

    private val authRepository = AuthRepository()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupOtpNavigation()

        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.tvResend.setOnClickListener {
             email?.let {
                 authRepository.requestOtp(it)
                     .addOnSuccessListener {
                         Toast.makeText(requireContext(), "OTP resent successfully", Toast.LENGTH_SHORT).show()
                     }
                     .addOnFailureListener { e ->
                         Toast.makeText(requireContext(), "Failed to resend OTP: ${e.message}", Toast.LENGTH_SHORT).show()
                     }
             }
        }

        binding.btnVerify.setOnClickListener {
             val otp = binding.etOtp1.text.toString() +
                      binding.etOtp2.text.toString() +
                      binding.etOtp3.text.toString() +
                      binding.etOtp4.text.toString()

            Log.d("VerificationFragment", "Verify clicked. Email: $email, OTP: $otp")

            if (otp.length == 4) {
                 binding.btnVerify.isEnabled = false
                 email?.let { e ->
                     Log.d("VerificationFragment", "Calling verifyOtp...")
                     authRepository.verifyOtp(e, otp)
                         .addOnSuccessListener { token ->
                             Log.d("VerificationFragment", "verifyOtp success. Token received, calling signInWithCustomToken...")
                             authRepository.signInWithCustomToken(token)
                                 .addOnSuccessListener {
                                     Log.d("VerificationFragment", "signInWithCustomToken success. Navigating to success screen.")
                                     val bundle = bundleOf("email" to email)
                                     findNavController().navigate(R.id.action_verificationFragment_to_successFragment, bundle)
                                 }
                                 .addOnFailureListener { err ->
                                     Log.e("VerificationFragment", "signInWithCustomToken failure", err)
                                     binding.btnVerify.isEnabled = true
                                     Toast.makeText(requireContext(), "Login failed: ${err.message}", Toast.LENGTH_SHORT).show()
                                 }
                         }
                         .addOnFailureListener { err ->
                             Log.e("VerificationFragment", "verifyOtp failure", err)
                             binding.btnVerify.isEnabled = true
                             Toast.makeText(requireContext(), "Verification failed: ${err.message}", Toast.LENGTH_SHORT).show()
                         }
                 }
            } else {
                Log.w("VerificationFragment", "OTP length is not 4: ${otp.length}")
                Toast.makeText(requireContext(), "Please enter complete code", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupOtpNavigation() {
        val inputs = listOf(binding.etOtp1, binding.etOtp2, binding.etOtp3, binding.etOtp4)
        
        inputs.forEachIndexed { index, editText ->
            editText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    // Forward navigation
                    if ((s?.length ?: 0) == 1 && index < inputs.lastIndex) {
                        inputs[index + 1].requestFocus()
                    }
                    // Backward navigation (if creating emptiness from non-empty)
                     else if ((s?.length ?: 0) == 0 && index > 0) {
                         inputs[index - 1].requestFocus()
                    }
                }

                override fun afterTextChanged(s: Editable?) {}
            })

            // Handle backspace when field is already empty
            editText.setOnKeyListener { _, keyCode, event ->
                if (keyCode == KeyEvent.KEYCODE_DEL && event.action == KeyEvent.ACTION_DOWN) {
                    if (editText.text.isEmpty() && index > 0) {
                        inputs[index - 1].requestFocus()
                         // Optionally consume the event, though returning false usually fine for standard behavior 
                         // returning true prevents default backspace which is fine since it's empty
                        return@setOnKeyListener true 
                    }
                }
                false
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
