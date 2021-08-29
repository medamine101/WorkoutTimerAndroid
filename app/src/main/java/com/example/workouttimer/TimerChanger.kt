package com.example.workouttimer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.NumberPicker
import androidx.fragment.app.Fragment



enum class NumPicker {Hours, Minutes, Seconds}

//Fragment Class
class TimerChanger : Fragment() {

    //private var parentActivity: MainActivity? = null
    private var okButton: Button? = null

    //Number picker to pick the number of seconds
    private var secondsPicker: NumberPicker? = null

    private var minutesPicker: NumberPicker? = null



    //Called on the creation of the fragment object by function newInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        arguments?.let {
//            param1 = it.getString(ARG_PARAM1)
//            param2 = it.getString(ARG_PARAM2)
//        }



    }

    //Called on creation of the visible object of the fragment, when the fragment is made visible
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val view =  inflater.inflate(R.layout.fragment_timer_changer, container, false)

        okButton = view.findViewById(R.id.okButton)
        okButton?.setOnClickListener { onOkButtonClick(okButton as View) }

        secondsPicker = view?.findViewById(R.id.secondsPicker)
        minutesPicker = view?.findViewById(R.id.minutesPicker)

        secondsPicker?.setOnValueChangedListener(NumberPicker.OnValueChangeListener(

            fun(_, oldVal, newVal){
                val timerLeft = (activity as MainActivity).timeLeftMilliseconds
                val newTime = timerLeft - (oldVal * 1000) + (newVal * 1000)
                (activity as MainActivity).timeLeftMilliseconds = newTime - (newTime % 1000)
                (activity as MainActivity).originalTimerNumber = (activity as MainActivity).timeLeftMilliseconds
                (activity as MainActivity).updateTime()
            }

        ))

        minutesPicker?.setOnValueChangedListener(NumberPicker.OnValueChangeListener(

            fun(_, oldVal, newVal){
                val timerLeft = (activity as MainActivity).timeLeftMilliseconds
                val newTime = timerLeft - (oldVal * 60000) + (newVal * 60000)

                (activity as MainActivity).timeLeftMilliseconds = newTime - (newTime % 1000)
                (activity as MainActivity).originalTimerNumber = (activity as MainActivity).timeLeftMilliseconds
                (activity as MainActivity).updateTime()
            }

        ))

        setValues(secondsPicker, NumPicker.Seconds)
        setValues(minutesPicker, NumPicker.Minutes)

        return view
    }

    private fun setValues(picker: NumberPicker?, typeOfNumberPicker: NumPicker){
        when (typeOfNumberPicker){
            NumPicker.Seconds -> {
                picker?.maxValue = 59
                picker?.minValue = 0
                picker?.value = ((activity as MainActivity).timeLeftMilliseconds % 60000 / 1000).toInt()
            }
            NumPicker.Minutes -> {
                picker?.maxValue = 59
                picker?.minValue = 0
                picker?.value = ((activity as MainActivity).timeLeftMilliseconds / 60000).toInt()
            }
            NumPicker.Hours -> {
                picker?.maxValue = 24
                picker?.minValue = 0
            }
        }
    }


    //Called when OK Button is clicked
    @Suppress("UNUSED_PARAMETER")
    private fun onOkButtonClick(view: View){

        (activity as MainActivity).closeTimerChangerFragment()

    }


    companion object {

        //Fragment is created via static call for creating a new instance.
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