package cat.smartcoding.mendez.freedating.ui.profiles.details

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.navArgs
import cat.smartcoding.mendez.freedating.R
import cat.smartcoding.mendez.freedating.Utils
import cat.smartcoding.mendez.freedating.databinding.ProfileDetailsFragmentBinding
import cat.smartcoding.mendez.freedating.ui.profiles.ProfileItem

class ProfileDetailsFragment : Fragment() {

    companion object {
        fun newInstance() = ProfileDetailsFragment()
    }

    private lateinit var viewModel: ProfileDetailsViewModel
    lateinit var binding : ProfileDetailsFragmentBinding
    val args: ProfileDetailsFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val info = args.info
        val userId = info.userId
        val image = info.image
        var bitmap: Bitmap? = null



        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.profile_details_fragment,
            container,
            false
        )
        viewModel = ViewModelProvider(this).get(ProfileDetailsViewModel::class.java)

        if(info != null) {

            Utils.obtenirBannerPic(this, userId)
            //binding.ivUserProfile = image
            //binding.iwUserBanner.setImageBitmap(image)
            if(image!= null) {
                bitmap = image
                binding.ivUserProfile.setImageBitmap(bitmap)
            }
            binding.tvUserName.text = info.name
            binding.tvUserAge.text = info.birthdate
            binding.tvUserGender.text = info.gender
            binding.tvUserLocation.text = if (info?.location != "") info?.location else "Not specify";
            binding.tvUserOther.text = if (info?.otherThings != "") info?.otherThings else "Nothing more";
            binding.tvUserDescription.text = if (info?.description != "") info?.description else "This person has no description yet";


        }


        return binding.root
    }

    private fun getUserdata() {

    }
}