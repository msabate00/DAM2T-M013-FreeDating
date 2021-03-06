package cat.smartcoding.mendez.freedating.ui.login


import android.app.DatePickerDialog
import android.content.ContentValues.TAG
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentManager
import androidx.navigation.fragment.NavHostFragment
import cat.smartcoding.mendez.freedating.MainActivity
import cat.smartcoding.mendez.freedating.R
import cat.smartcoding.mendez.freedating.Utils
import cat.smartcoding.mendez.freedating.databinding.RegisterFragmentBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase

class RegisterFragment : Fragment(), AdapterView.OnItemSelectedListener {



    private lateinit var viewModel: RegisterViewModel
    private lateinit var binding: RegisterFragmentBinding
    private lateinit var auth: FirebaseAuth;
    private lateinit var database: FirebaseDatabase;

    private lateinit var gender: String;





    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.register_fragment,
            container,
            false
        )
        viewModel = ViewModelProvider(this)[RegisterViewModel::class.java];
        database = FirebaseDatabase.getInstance("https://freedatingapp-66476-default-rtdb.europe-west1.firebasedatabase.app/")

        binding.viewModel = viewModel;
        binding.lifecycleOwner = viewLifecycleOwner;
        auth = viewModel.getAuth();
        auth.signOut();

        viewModel.onRegister.observe(viewLifecycleOwner,{
            if(viewModel.onRegister.value == true){

                if(binding.etRegisterAge.text.toString() != "") {
                    createAccount(
                        binding.etRegisterEmail.text.toString(),
                        binding.etRegisterPassword.text.toString(),
                        binding.etRegisterConfirmPassword.text.toString(),
                        binding.etRegisterName.text.toString(),
                        gender,
                        binding.etRegisterAge.text.toString()
                    );
                }else{
                    Toast.makeText(context, "You need to fill all the fields", Toast.LENGTH_SHORT).show()
                }

                viewModel.onRegisterButtonComplete();
            }
        })

        /*binding.etRegisterAge.setOnClickListener {
            showDatePickerDialog();
        }*/
        binding.etRegisterAge.setOnFocusChangeListener { v, hasFocus ->
            if(hasFocus){
                showDatePickerDialog();
                v.clearFocus();
            }
        }

        val spinner: Spinner = binding.ddRegisterGender
        spinner.onItemSelectedListener = this
// Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.gender_entries,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            spinner.adapter = adapter
        }

        return binding.root;
    }

    override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
        when(pos){
            0 -> gender = "Male";
            1 -> gender = "Female";
            2 -> gender = "Other..."
        }
        Log.i("AYUDA", pos.toString());
    }

    override fun onNothingSelected(parent: AdapterView<*>) {
        // Another interface callback
    }

     fun showDatePickerDialog() {
         val newFragment = Utils.DatePickerFragment.newInstance(DatePickerDialog.OnDateSetListener { _, year, month, day ->
             // +1 because January is zero
             val selectedDate = day.toString() + " / " + (month + 1) + " / " + year
             binding.etRegisterAge.setText(selectedDate)
         })

         newFragment.show(requireFragmentManager(), "datePicker")
     }

    override fun onStart() {
        super.onStart()
        (activity as MainActivity).setDrawer_Locked();
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
            NavHostFragment.findNavController(this).navigate(RegisterFragmentDirections.actionNavRegisterToNavGallery());

        }else{
            //COSAS QUE HACER PARA CUANDO NO ESTE LOGEADO
        }
    }

    private fun createAccount(email: String, password: String, passwordConfirm: String, name: String, gender: String, birthdate: String) {
        // [START create_user_with_email]
        binding.btnRegisterRegister.isEnabled = false;

        if(password == passwordConfirm) {
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener() { task ->
                    binding.btnRegisterRegister.isEnabled = true;
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "createUserWithEmail:success")

                        saveUser(name, email, gender, birthdate);
                        val user = auth.currentUser
                        updateUI(user)
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "createUserWithEmail:failure", task.exception)
                        Toast.makeText(
                            context, "Creation failed.",
                            Toast.LENGTH_SHORT
                        ).show()
                        updateUI(null)
                    }
                }
        }else{
            binding.btnRegisterRegister.isEnabled = true;
            Toast.makeText(
                context, "Passwords do not match.",
                Toast.LENGTH_SHORT
            ).show()
        }
        // [END create_user_with_email]
    }

    fun saveUser(name: String, email: String, gender: String, birthdate: String){
        val uid = FirebaseAuth.getInstance().currentUser?.uid;
        if( uid == null ) return

        val user = Utils.Companion.User(name, email, gender, birthdate);

        val myRef = database.getReference("/users/$uid")
        myRef.setValue(user)
    }




    /*data class newUser(
        var name: String? = "",
        var email: String? = "",
    )*/

}
