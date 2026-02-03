package com.saveourwater.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.saveourwater.R
import com.saveourwater.databinding.FragmentAuthBinding

/**
 * AuthFragment - Handles user login and signup with Supabase
 */
class AuthFragment : Fragment() {

    private var _binding: FragmentAuthBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AuthViewModel by viewModels()

    private var isLoginMode = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAuthBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUI()
        observeViewModel()
    }

    private fun setupUI() {
        // Toggle between login and signup
        binding.btnToggleMode.setOnClickListener {
            isLoginMode = !isLoginMode
            updateUIMode()
        }

        // Auth button click
        binding.btnAuth.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (validateInput(email, password)) {
                if (isLoginMode) {
                    viewModel.login(email, password)
                } else {
                    viewModel.signUp(email, password)
                }
            }
        }

        // Skip button for offline mode
        binding.btnSkip.setOnClickListener {
            navigateToHome()
        }

        updateUIMode()
    }

    private fun updateUIMode() {
        if (isLoginMode) {
            binding.tvTitle.text = getString(R.string.auth_login_title)
            binding.btnAuth.text = getString(R.string.auth_login_button)
            binding.btnToggleMode.text = getString(R.string.auth_switch_to_signup)
            binding.tilConfirmPassword.visibility = View.GONE
        } else {
            binding.tvTitle.text = getString(R.string.auth_signup_title)
            binding.btnAuth.text = getString(R.string.auth_signup_button)
            binding.btnToggleMode.text = getString(R.string.auth_switch_to_login)
            binding.tilConfirmPassword.visibility = View.VISIBLE
        }
    }

    private fun validateInput(email: String, password: String): Boolean {
        var isValid = true

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.tilEmail.error = getString(R.string.auth_error_invalid_email)
            isValid = false
        } else {
            binding.tilEmail.error = null
        }

        if (password.isEmpty() || password.length < 6) {
            binding.tilPassword.error = getString(R.string.auth_error_weak_password)
            isValid = false
        } else {
            binding.tilPassword.error = null
        }

        if (!isLoginMode) {
            val confirmPassword = binding.etConfirmPassword.text.toString().trim()
            if (password != confirmPassword) {
                binding.tilConfirmPassword.error = getString(R.string.auth_error_password_mismatch)
                isValid = false
            } else {
                binding.tilConfirmPassword.error = null
            }
        }

        return isValid
    }

    private fun observeViewModel() {
        viewModel.authState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is AuthState.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.btnAuth.isEnabled = false
                }
                is AuthState.Success -> {
                    binding.progressBar.visibility = View.GONE
                    binding.btnAuth.isEnabled = true
                    Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                    navigateToHome()
                }
                is AuthState.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.btnAuth.isEnabled = true
                    Toast.makeText(requireContext(), state.message, Toast.LENGTH_LONG).show()
                }
                is AuthState.Idle -> {
                    binding.progressBar.visibility = View.GONE
                    binding.btnAuth.isEnabled = true
                }
            }
        }
    }

    private fun navigateToHome() {
        // Navigate to home fragment
        findNavController().navigate(R.id.action_authFragment_to_homeFragment)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
