package tahadeta.example.mascover.ui.setting

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context.CLIPBOARD_SERVICE
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.zeugmasolutions.localehelper.LocaleHelperActivityDelegate
import com.zeugmasolutions.localehelper.LocaleHelperActivityDelegateImpl
import com.zeugmasolutions.localehelper.Locales
import tahadeta.example.mascover.BuildConfig
import tahadeta.example.mascover.R
import tahadeta.example.mascover.util.Constants
import tahadeta.example.mascover.util.Constants.LINK_STORE
import tahadeta.example.mascover.util.Constants.LINK_TELEGRAM
import tahadeta.example.mascover.util.ModelPreferencesManager


class SettingFragment : Fragment() {

    private val localeDelegate: LocaleHelperActivityDelegate = LocaleHelperActivityDelegateImpl()

    lateinit var homeImage: View
    lateinit var favouriteImage: View
    lateinit var switchArab: Switch
    lateinit var switchFrensh: Switch
    lateinit var shareTlgrmImage: ImageView
    lateinit var copyTlgrmImage: ImageView
    lateinit var shareAppImage: ImageView
    lateinit var versionNumber: TextView

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

        shareTlgrmImage = root.findViewById(R.id.imageViewShareTlgrm)
        copyTlgrmImage = root.findViewById(R.id.imageViewCopy)
        shareAppImage = root.findViewById(R.id.imageViewShare)
        versionNumber = root.findViewById(R.id.versionNumber)

        // Set the version number
        versionNumber.setText(BuildConfig.VERSION_NAME)

        // Share app link
        shareAppImage.setOnClickListener {
            try {
                val shareIntent = Intent(Intent.ACTION_SEND)
                shareIntent.type = "text/plain"
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, "MAS Cover")
                var shareMessage = "\nJe te recommande cette application\n\n"
                shareMessage =
                    """
                $shareMessage $LINK_STORE
                    """.trimIndent()
                shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
                startActivity(Intent.createChooser(shareIntent, "choose one"))
            } catch (e: Exception) {
                // e.toString();
            }
        }

        // Share Telegram Link
        shareTlgrmImage.setOnClickListener {
            try {
                val shareIntent = Intent(Intent.ACTION_SEND)
                shareIntent.type = "text/plain"
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Telegram -MAS Cover-")
                var shareMessage = "\nJe te recommande cette chaine Telegram\n\n"
                shareMessage =
                    """
                $shareMessage $LINK_TELEGRAM
                    """.trimIndent()
                shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
                startActivity(Intent.createChooser(shareIntent, "choose one"))
            } catch (e: Exception) {
                // e.toString();
            }
        }

        // Copy link Telegram
        copyTlgrmImage.setOnClickListener {
            val clipboardManager = requireActivity().getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
            val clipData = ClipData.newPlainText("label", LINK_TELEGRAM)
            Toast.makeText(context, "text copi??", Toast.LENGTH_SHORT).show()
            clipboardManager.setPrimaryClip(clipData)
        }

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
