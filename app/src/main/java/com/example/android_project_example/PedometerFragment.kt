package com.example.android_project_example

import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.android_project_example.databinding.FragmentPedometerBinding

class PedometerFragment : Fragment(), SensorEventListener {

    private lateinit var _binding: FragmentPedometerBinding

    private val ACTIVITY_RECOGNITION_REQUEST_CODE = 100

    private lateinit var sensorManager: SensorManager
    private var stepCountSensor: Sensor? = null
    private var totalSteps = 0f
    private var previousTotalSteps = 0f
    private lateinit var stepCountTextView: TextView
    private lateinit var resetButton: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPedometerBinding.inflate(inflater)

        stepCountTextView = _binding.stepCountTextView
        resetButton = _binding.resetButton

        sensorManager = requireContext().getSystemService(Context.SENSOR_SERVICE) as SensorManager
        stepCountSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

        // 활동 인식 권한 확인
        if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.ACTIVITY_RECOGNITION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(android.Manifest.permission.ACTIVITY_RECOGNITION), ACTIVITY_RECOGNITION_REQUEST_CODE)
        } else {
            setupStepCounter()
        }

        resetButton.setOnClickListener {
            resetStepCount()
        }

        return _binding.root
    }

    override fun onResume() {
        super.onResume()
        if (stepCountSensor != null) {
            sensorManager.registerListener(this, stepCountSensor, SensorManager.SENSOR_DELAY_UI)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == ACTIVITY_RECOGNITION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setupStepCounter()
            } else {
                Toast.makeText(context, "센서를 감지하지 못했습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupStepCounter() {
        sensorManager = requireContext().getSystemService(Context.SENSOR_SERVICE) as SensorManager
        stepCountSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event != null)
            if (event.sensor == stepCountSensor) {
                totalSteps = event.values[0] // event.valuse[0]은 기기가 부팅된 이후의 총 걸음수 반환
                val currentSteps = totalSteps.toInt() - previousTotalSteps.toInt()
                stepCountTextView.text = currentSteps.toString()
            }
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {

    }

    private fun resetStepCount() {
        previousTotalSteps = totalSteps
        stepCountTextView.text = "0"
    }

}