package com.assignment.okpassignment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.assignment.okpassignment.databinding.FragmentVerificationBinding

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupOtpNavigation()

        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.tvResend.setOnClickListener {
            Toast.makeText(requireContext(), "Code resent to $email", Toast.LENGTH_SHORT).show()
        }

        binding.btnVerify.setOnClickListener {
            // Dummy verification: any code length 4 works
             val otp = binding.etOtp1.text.toString() +
                      binding.etOtp2.text.toString() +
                      binding.etOtp3.text.toString() +
                      binding.etOtp4.text.toString()

            if (otp.length == 4) {
                 val bundle = bundleOf("email" to email)
                findNavController().navigate(R.id.action_verificationFragment_to_successFragment, bundle)
            } else {
                Toast.makeText(requireContext(), "Please enter complete code", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupOtpNavigation() {
        val inputs = listOf(binding.etOtp1, binding.etOtp2, binding.etOtp3, binding.etOtp4)
        
        inputs.forEachIndexed { index, editText ->
            editText.addTextChangedListener(object : android.text.TextWatcher {
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

                override fun afterTextChanged(s: android.text.Editable?) {}
            })

            // Handle backspace when field is already empty
            editText.setOnKeyListener { _, keyCode, event ->
                if (keyCode == android.view.KeyEvent.KEYCODE_DEL && event.action == android.view.KeyEvent.ACTION_DOWN) {
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
