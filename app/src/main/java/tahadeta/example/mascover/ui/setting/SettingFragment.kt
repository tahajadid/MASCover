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
    lateinit var langArab: TextView
    lateinit var langFrench: TextView
    lateinit var shareTlgrmImage: View
    lateinit var copyTlgrmImage: View
    lateinit var shareAppImage: View
    lateinit var versionNumber: TextView
    //lateinit var adView: AdView
    private lateinit var flashImage: ImageView
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
        langArab = root.findViewById(R.id.switch_ar)
        langFrench = root.findViewById(R.id.switch_fr)

        shareTlgrmImage = root.findViewById(R.id.imageViewShareTlgrm)
        copyTlgrmImage = root.findViewById(R.id.imageViewCopy)
        shareAppImage = root.findViewById(R.id.imageViewShare)
        versionNumber = root.findViewById(R.id.versionNumber)

        flashImage = root.findViewById(R.id.flash_iv)

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

        val isFrench = ModelPreferencesManager.get<Boolean>(Constants.IS_FRENSH) != false
        showSelectedLanguage(isFrench)

        langArab.setOnClickListener {
            if (ModelPreferencesManager.get<Boolean>(Constants.IS_FRENSH) == false) return@setOnClickListener
            localeDelegate.setLocale(this.requireActivity(), Locales.Arabic)
            ModelPreferencesManager.put(false, Constants.IS_FRENSH)
            showSelectedLanguage(false)
        }

        langFrench.setOnClickListener {
            if (ModelPreferencesManager.get<Boolean>(Constants.IS_FRENSH) != false) return@setOnClickListener
            localeDelegate.setLocale(this.requireActivity(), Locales.French)
            ModelPreferencesManager.put(true, Constants.IS_FRENSH)
            showSelectedLanguage(true)
        }

        homeImage.setOnClickListener {
            findNavController().navigate(R.id.homeFragment)
        }

        favouriteImage.setOnClickListener {
            findNavController().navigate(R.id.favouriteFragment)
        }

        return root
    }

    private fun showSelectedLanguage(french: Boolean) {
        langFrench.setBackgroundResource(
            if (french) R.drawable.bg_lang_selected else R.drawable.bg_lang_idle
        )
        langArab.setBackgroundResource(
            if (french) R.drawable.bg_lang_idle else R.drawable.bg_lang_selected
        )
        langFrench.setTextColor(requireContext().getColor(if (french) R.color.settings_title else R.color.settings_muted))
        langArab.setTextColor(requireContext().getColor(if (french) R.color.settings_muted else R.color.settings_title))
        langFrench.setTypeface(null, if (french) android.graphics.Typeface.BOLD else android.graphics.Typeface.NORMAL)
        langArab.setTypeface(null, if (french) android.graphics.Typeface.NORMAL else android.graphics.Typeface.BOLD)

    }

    private fun initFlash() {
        var cameraManager = requireActivity().getSystemService(Context.CAMERA_SERVICE) as CameraManager
        try {
            cameraId = cameraManager.cameraIdList[0]
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }

        flashImage.visibility = View.GONE
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

    }
}
