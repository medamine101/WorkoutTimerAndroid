package com.example.workouttimer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment



/**
 * A simple [Fragment] subclass.
 * Use the [TimerChanger.newInstance] factory method to
 * create an instance of this fragment.
 */
class TimerChanger : Fragment() {

    //private var parentActivity: MainActivity? = null
    private var okButton: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        arguments?.let {
//            param1 = it.getString(ARG_PARAM1)
//            param2 = it.getString(ARG_PARAM2)
//        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment

        val view =  inflater.inflate(R.layout.fragment_timer_changer, container, false)

        okButton = view.findViewById(R.id.okButton)
        okButton?.setOnClickListener { onOkButtonClick(okButton as View) }

        return view
    }
    @Suppress("UNUSED_PARAMETER")
    private fun onOkButtonClick(view: View){

        (activity as MainActivity).closeTimerChangerFragment()

    }

    companion object {

        @JvmStatic
        fun newInstance() =
            TimerChanger().apply {
//                arguments = Bundle().apply {
//                    putString(ARG_PARAM1, param1)
//                }

                //parentActivity = parent

            }
    }
}