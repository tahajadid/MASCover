package tahadeta.example.mascover.ui.setting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import tahadeta.example.mascover.R
import tahadeta.example.mascover.util.Constants
import tahadeta.example.mascover.util.ModelPreferencesManager

class SettingFragment : Fragment() {

    lateinit var homeImage: View
    lateinit var favouriteImage: View
    lateinit var switchArab: Switch
    lateinit var switchFrensh: Switch

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_setting, container, false)

        homeImage = root.findViewById(R.id.homeBack)
        favouriteImage = root.findViewById(R.id.favouriteBack)
        switchArab = root.findViewById(R.id.switch_ar)
        switchFrensh = root.findViewById(R.id.switch_fr)

        favouriteImage.setOnClickListener {
            findNavController().navigate(R.id.favouriteFragment)
        }

        switchArab.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                switchFrensh.isChecked = false
                ModelPreferencesManager.put(false, Constants.IS_FRENSH)
            }
        }

        switchFrensh.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                switchArab.isChecked = false
                ModelPreferencesManager.put(true, Constants.IS_FRENSH)
            }
        }

        homeImage.setOnClickListener {
            findNavController().navigate(R.id.homeFragment)
        }

        return root
    }
}
