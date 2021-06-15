package com.notspotify.project_music.ui.login

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.notspotify.project_music.R
import com.notspotify.project_music.common.formatHtml
import com.notspotify.project_music.common.makeToast
import com.notspotify.project_music.factory.LoginViewModelFactory
import com.notspotify.project_music.api.RetrofitFactory
import com.notspotify.project_music.api.service.APIAccount
import com.notspotify.project_music.ui.login.viewmodel.LoginFragmentState
import com.notspotify.project_music.ui.login.viewmodel.LoginViewModel
import kotlinx.android.synthetic.main.login_fragment.*
import kotlinx.android.synthetic.main.login_fragment.password


class LoginFragment : Fragment() {

    private lateinit var loginViewModel: LoginViewModel

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.login_fragment, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        loginViewModel = ViewModelProvider(viewModelStore,LoginViewModelFactory(RetrofitFactory(requireContext())
            .createService(APIAccount::class.java), activity?.application!!
        )).get(LoginViewModel::class.java)

        loginViewModel.getState().observe(viewLifecycleOwner, Observer { updateUI(it) })

        btn_signIn.setOnClickListener {
            loginViewModel.connection(username.text.toString(), password.text.toString())
        }
    }

    private fun updateUI(state: LoginFragmentState){
        when(state){
            is LoginFragmentState.Failure ->{
                makeToast("Error")
            }
            is LoginFragmentState.Loading ->{
                makeToast("Loading")
            }
            is LoginFragmentState.Success ->{
                findNavController().navigate(R.id.action_login_to_bottomNav)
            }
        }
    }



}
