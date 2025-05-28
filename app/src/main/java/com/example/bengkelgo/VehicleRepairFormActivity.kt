package com.example.bengkelgo

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import java.text.SimpleDateFormat
import java.util.*

class VehicleRepairFormActivity : AppCompatActivity() {

    private lateinit var actVehicleType: AutoCompleteTextView
    private lateinit var actServiceType: AutoCompleteTextView
    private lateinit var actLocation: AutoCompleteTextView
    private lateinit var edtServiceSchedule: TextInputEditText
    private lateinit var tilServiceSchedule: TextInputLayout
    private lateinit var actPaymentMethod: AutoCompleteTextView
    private lateinit var btnNext: Button
    private lateinit var btnBack: ImageButton

    private var selectedDateTimeMillis: Long = 0
    private val calendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vehicle_repair_form)

        actVehicleType = findViewById(R.id.actVehicleType)
        actServiceType = findViewById(R.id.actServiceType)
        actLocation = findViewById(R.id.actLocation)
        edtServiceSchedule = findViewById(R.id.edtServiceSchedule)
        tilServiceSchedule = findViewById(R.id.tilServiceSchedule)
        actPaymentMethod = findViewById(R.id.actPaymentMethod)
        btnNext = findViewById(R.id.btnNext)
        btnBack = findViewById(R.id.btnBackVehicleRepair)

        setupSpinners()
        setupDateTimePicker()

        btnBack.setOnClickListener {
            finish()
        }

        btnNext.setOnClickListener {
            if (validateInputs()) {
                val intent = Intent(this, OrderSummaryActivity::class.java).apply {
                    putExtra("VEHICLE_TYPE", actVehicleType.text.toString())
                    putExtra("SERVICE_TYPE", actServiceType.text.toString())
                    putExtra("LOCATION_AREA", actLocation.text.toString())
                    putExtra("SERVICE_SCHEDULE_FORMATTED", edtServiceSchedule.text.toString())
                    putExtra("SERVICE_SCHEDULE_MILLIS", selectedDateTimeMillis)
                    putExtra("PAYMENT_METHOD", actPaymentMethod.text.toString())
                }
                startActivity(intent)
            }
        }
    }

    private fun setupSpinners() {
        // Vehicle Types
        val vehicleTypes = resources.getStringArray(R.array.vehicle_types)
        val vehicleAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, vehicleTypes)
        actVehicleType.setAdapter(vehicleAdapter)

        actVehicleType.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            val selectedVehicle = vehicleTypes[position]
            updateServiceTypeSpinner(selectedVehicle)
        }

        // Locations
        val locations = resources.getStringArray(R.array.locations)
        val locationAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, locations)
        actLocation.setAdapter(locationAdapter)

        // Payment Methods
        val paymentMethods = resources.getStringArray(R.array.payment_methods)
        val paymentAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, paymentMethods)
        actPaymentMethod.setAdapter(paymentAdapter)

        // Initialize Service Type Spinner (default or based on initial vehicle type if any)
        if (vehicleTypes.isNotEmpty()) {
            // actVehicleType.setText(vehicleTypes[0], false) // Set default if needed
            // updateServiceTypeSpinner(vehicleTypes[0])
        } else {
            updateServiceTypeSpinner(null) // Handle empty case
        }
    }

    private fun updateServiceTypeSpinner(vehicleType: String?) {
        val serviceTypesArrayId = when (vehicleType) {
            "Mobil" -> R.array.service_types_car
            "Motor" -> R.array.service_types_motorcycle
            else -> null // Handle no selection or other types
        }

        if (serviceTypesArrayId != null) {
            val serviceTypes = resources.getStringArray(serviceTypesArrayId)
            val serviceAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, serviceTypes)
            actServiceType.setAdapter(serviceAdapter)
            actServiceType.setText("", false) // Clear previous selection
            actServiceType.isEnabled = true
        } else {
            actServiceType.setAdapter(null) // Clear adapter
            actServiceType.setText("", false)
            actServiceType.isEnabled = false
            actServiceType.hint = getString(R.string.service_type_prompt)
        }
    }


    private fun setupDateTimePicker() {
        edtServiceSchedule.setOnClickListener {
            showDatePicker()
        }
        tilServiceSchedule.setEndIconOnClickListener {
            showDatePicker()
        }
    }

    private fun showDatePicker() {
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
            calendar.set(Calendar.YEAR, selectedYear)
            calendar.set(Calendar.MONTH, selectedMonth)
            calendar.set(Calendar.DAY_OF_MONTH, selectedDay)
            showTimePicker()
        }, year, month, day)
        datePickerDialog.datePicker.minDate = System.currentTimeMillis() - 1000 // Set minimal date to today
        datePickerDialog.setTitle(getString(R.string.select_date))
        datePickerDialog.show()
    }

    private fun showTimePicker() {
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(this, { _, selectedHour, selectedMinute ->
            calendar.set(Calendar.HOUR_OF_DAY, selectedHour)
            calendar.set(Calendar.MINUTE, selectedMinute)
            selectedDateTimeMillis = calendar.timeInMillis
            val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            edtServiceSchedule.setText(sdf.format(calendar.time))
        }, hour, minute, true) // true for 24-hour format
        timePickerDialog.setTitle(getString(R.string.select_time))
        timePickerDialog.show()
    }

    private fun validateInputs(): Boolean {
        if (actVehicleType.text.isNullOrEmpty() ||
            actServiceType.text.isNullOrEmpty() ||
            actLocation.text.isNullOrEmpty() ||
            edtServiceSchedule.text.isNullOrEmpty() ||
            actPaymentMethod.text.isNullOrEmpty()) {
            Toast.makeText(this, getString(R.string.please_fill_all_fields), Toast.LENGTH_SHORT).show()
            return false
        }
        if (selectedDateTimeMillis == 0L) {
            Toast.makeText(this, getString(R.string.please_select_schedule), Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }
}