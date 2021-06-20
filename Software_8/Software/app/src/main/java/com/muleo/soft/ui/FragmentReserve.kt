package com.muleo.soft.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.google.android.material.datepicker.MaterialDatePicker
import com.muleo.soft.R
import com.muleo.soft.databinding.FragmentReserveBinding
import java.text.SimpleDateFormat
import java.util.*

class FragmentReserve : Fragment() {

    private lateinit var startDate: EditText
    private lateinit var endDate: EditText
    private lateinit var count: EditText


    private var _binding: FragmentReserveBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReserveBinding.inflate(inflater, container, false)
        val view = binding.root
        binding.apply {
            startDate = etFrom
            endDate = etTo
            count = etCount

            tiFrom.apply {
                val datePicker =
                    MaterialDatePicker.Builder.datePicker()
                        .setTitleText("Select date")
                        .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                        .build()
                        .apply {
                            addOnPositiveButtonClickListener {
                                etFrom.setText(
                                    SimpleDateFormat(
                                        "MMM dd, yyyy",
                                        Locale.KOREA
                                    ).format(it)
                                )
                            }
                        }

                datePicker.show(parentFragmentManager, "selectStartDate")
            }
            tiTo.apply {
                val datePicker =
                    MaterialDatePicker.Builder.datePicker()
                        .setTitleText("Select date")
                        .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                        .build()
                        .apply {
                            addOnPositiveButtonClickListener {
                                etTo.setText(
                                    SimpleDateFormat("MMM dd, yyyy", Locale.KOREA).format(
                                        it
                                    )
                                )
                            }
                        }

                datePicker.show(parentFragmentManager, "selectEndDate")
            }
        }

        count()

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    fun count() {
        val items = (1..100).toList()
        val adapter = ArrayAdapter(requireContext(), R.layout.count, items)
        (count as? AutoCompleteTextView)?.setAdapter(adapter)
    }
}