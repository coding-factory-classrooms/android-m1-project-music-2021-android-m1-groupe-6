package com.notspotify.project_music.ui.splashscreen

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.notspotify.project_music.R
import com.notspotify.project_music.factory.SplashViewModelFactory
import com.notspotify.project_music.api.RetrofitFactory
import com.notspotify.project_music.api.service.APIAccount
import com.notspotify.project_music.ui.splashscreen.viewmodel.SplashViewModel

import java.lang.Exception

class SplashFragment : Fragment() {

    private lateinit var splashViewModel: SplashViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_splash, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val apiAccount: APIAccount = RetrofitFactory(requireContext()).createService(APIAccount::class.java)
        splashViewModel = ViewModelProvider(this, SplashViewModelFactory(requireActivity().application,apiAccount)).get(
            SplashViewModel::class.java)

        splashViewModel.getConnected().observe(this, Observer { userAuth(it) })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val background = object : Thread(){
            override fun run() {
                super.run()
                try {
                    sleep(1000)
                    splashViewModel.isConnected()
                }catch (e:Exception){
                    Log.v("test","e : $e")
                }
            }
        }

        background.start()
    }

    private fun userAuth(connect: Boolean){
        if(connect){
            findNavController().navigate(R.id.action_splashFragment_to_main)
        }else{
            findNavController().navigate(R.id.action_splashFragment_to_sign)
        }
    }
}
