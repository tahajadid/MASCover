package tahadeta.example.mascover.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import tahadeta.example.mascover.R

class YellowScreenFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val root = inflater.inflate(R.layout.fragment_yellow_screen, container, false)

        val window: Window = requireActivity().window

        if (window != null) {
            window.setStatusBarColor(ContextCompat.getColor(requireActivity(), R.color.main_yellow))
            window.navigationBarColor = ContextCompat.getColor(requireActivity(), R.color.main_yellow)
        }

        return root
    }
}
