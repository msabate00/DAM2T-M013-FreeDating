package cat.smartcoding.mendez.freedating.ui.login

import android.content.ContentValues.TAG
import androidx.fragment.app.Fragment
import android.os.Bundle
import android.text.Layout
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import cat.smartcoding.mendez.freedating.MainActivity
import cat.smartcoding.mendez.freedating.R
import cat.smartcoding.mendez.freedating.Utils
import cat.smartcoding.mendez.freedating.databinding.FragmentLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser


class LoginFragment : Fragment() {
    private lateinit var viewModel: LoginViewModel
    private lateinit var binding: FragmentLoginBinding
    private lateinit var auth: FirebaseAuth;




    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_login,
            container,
            false
        )
        viewModel = ViewModelProvider(this)[LoginViewModel::class.java];
        binding.viewModel = viewModel;
        binding.lifecycleOwner = viewLifecycleOwner;
        auth = viewModel.getAuth();

       // auth.signOut();//QUITAR PARA EL FINAL

        viewModel.onLogin.observe(viewLifecycleOwner,{
            if(viewModel.onLogin.value == true){

                signIn(binding.etLoginEmail.text.toString(), binding.etLoginPassword.text.toString());
                viewModel.onLoginButtonComplete();
            }
        })

        binding.btnLoginRegister.setOnClickListener {
            NavHostFragment.findNavController(this).navigate(LoginFragmentDirections.actionNavLoginToNavRegister());
        }



        return binding.root;
    }


    override fun onStart() {
        super.onStart()
        (activity as MainActivity).setDrawer_Locked()
        val currentUser = auth.currentUser
        if(currentUser != null){
            reload();
        }

    }



    private fun reload() {
        val currentUser = auth.currentUser
        updateUI(currentUser)
    }
    private fun updateUI(user: FirebaseUser?) {
        if( user != null ) {

            (activity as MainActivity).setDrawer_Unlocked();
            Utils.obtenirDadesUsuari(activity as MainActivity);
            NavHostFragment.findNavController(this).navigate(LoginFragmentDirections.actionLoginFragmentToNavGallery());

        }else{
            //COSAS QUE HACER PARA CUANDO NO ESTE LOGEADO
        }
    }

    private fun signIn(email: String, password: String) {
        // [START sign_in_with_email]
        binding.btnLoginLogin.isEnabled = false; //added
        binding.btnLoginRegister.isEnabled = false; //added

        if(email != "" && password != "") {

            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener() { task ->
                    binding.btnLoginLogin.isEnabled = true; //added
                    binding.btnLoginRegister.isEnabled = true; //added
                    //btAutentifica.isEnabled = true
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithEmail:success")
                        Toast.makeText(
                            context, "Authentication Success.",
                            Toast.LENGTH_SHORT
                        ).show()
                        val user = auth.currentUser
                        updateUI(user)
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithEmail:failure", task.exception)
                        Toast.makeText(
                            context, "Authentication failed.",
                            Toast.LENGTH_SHORT
                        ).show()
                        updateUI(null)
                    }
                }
        }else{
            Toast.makeText(
                context, "Fill in all fields.",
                Toast.LENGTH_SHORT
            ).show()
        }
        // [END sign_in_with_email]
    }




}