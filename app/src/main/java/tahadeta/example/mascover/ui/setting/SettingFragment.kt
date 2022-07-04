package tahadeta.example.mascover.ui.setting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.zeugmasolutions.localehelper.LocaleHelperActivityDelegate
import com.zeugmasolutions.localehelper.LocaleHelperActivityDelegateImpl
import com.zeugmasolutions.localehelper.Locales
import tahadeta.example.mascover.R
import tahadeta.example.mascover.util.Constants
import tahadeta.example.mascover.util.ModelPreferencesManager

class SettingFragment : Fragment() {

    private val localeDelegate: LocaleHelperActivityDelegate = LocaleHelperActivityDelegateImpl()

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

        switchArab.isChecked = false
        switchFrensh.isChecked = false

        // Set the actual language
        val isFrensh = ModelPreferencesManager.get<Boolean>(Constants.IS_FRENSH)
        if (isFrensh == false) {
            switchArab.isChecked = true
        } else {
            switchFrensh.isChecked = true
        }

        // Behavior of switch with click not with slide
        switchArab.setOnClickListener {
            if (switchArab.isChecked) {
                localeDelegate.setLocale(this.requireActivity(), Locales.Arabic)
                switchArab.isChecked = true
                switchFrensh.isChecked = false
                ModelPreferencesManager.put(false, Constants.IS_FRENSH)
            } else {
                switchArab.isChecked = true
            }
        }

        switchFrensh.setOnClickListener {
            if (switchFrensh.isChecked) {
                localeDelegate.setLocale(this.requireActivity(), Locales.French)
                switchArab.isChecked = false
                switchFrensh.isChecked = true
                ModelPreferencesManager.put(true, Constants.IS_FRENSH)
            } else {
                switchFrensh.isChecked = true
            }
        }

        homeImage.setOnClickListener {
            findNavController().navigate(R.id.homeFragment)
        }

        favouriteImage.setOnClickListener {
            findNavController().navigate(R.id.favouriteFragment)
        }

        return root
    }
}
