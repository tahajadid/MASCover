package tahadeta.example.mascover.ui.setting

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Context.CLIPBOARD_SERVICE
import android.content.Intent
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.facebook.ads.AdSize
import com.facebook.ads.AdView
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
    //lateinit var adView: AdView
    private lateinit var flashImage: ImageView
    private lateinit var yellowImage: ImageView
    var flashIsOne = false
    private lateinit var cameraId: String

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

        flashImage = root.findViewById(R.id.flash_iv)
        yellowImage = root.findViewById(R.id.yellow_iv)

        initFlash()

        // Set the version number
        versionNumber.setText(BuildConfig.VERSION_NAME)

        //val adContainer = root.findViewById<View>(R.id.banner_container) as LinearLayout
        //adView = AdView(requireContext(), "602184014891911_602185281558451", AdSize.BANNER_HEIGHT_50)
        //adContainer.addView(adView)
        //adView.loadAd()

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
            Toast.makeText(context, "text copié", Toast.LENGTH_SHORT).show()
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

    private fun initFlash() {
        var cameraManager = requireActivity().getSystemService(Context.CAMERA_SERVICE) as CameraManager
        try {
            cameraId = cameraManager.cameraIdList[0]
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }

        flashImage.setOnClickListener {

            if (flashIsOne) {
                flashIsOne = false
                flashImage.setImageResource(R.drawable.flash_empty)
                try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        cameraManager.setTorchMode(cameraId, false)
                    }
                } catch (e: CameraAccessException) {
                    e.printStackTrace()
                }
            } else {
                flashIsOne = true
                flashImage.setImageResource(R.drawable.flash_fill)
                try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        cameraManager.setTorchMode(cameraId, true)
                    }
                } catch (e: CameraAccessException) {
                    e.printStackTrace()
                }
            }
        }

        yellowImage.setOnClickListener {
            findNavController().navigate(R.id.yellowScreenFragment)
        }
    }
}
