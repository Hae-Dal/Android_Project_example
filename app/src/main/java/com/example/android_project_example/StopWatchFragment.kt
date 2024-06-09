package com.example.android_project_example

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.renderscript.ScriptGroup.Binding
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.example.android_project_example.databinding.FragmentStopWatchBinding

class StopWatchFragment : Fragment() {

    private lateinit var _binding: FragmentStopWatchBinding

    private lateinit var stopWatchTextView: TextView
    private lateinit var startStopButton: Button
    private var isRunning = false
    private var elapsedTime = 0L
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var resetButton: Button
    private lateinit var lapButton: Button
    private lateinit var lapTextView: TextView
    private val lapTimes = mutableListOf<String>()
    private var lapCounter = 1

    // 시간 계산 Runnable 객체
    private val runnable = object: Runnable {
        override fun run() {
            val hours = elapsedTime / 3600000
            val minutes = (elapsedTime % 3600000) / 60000
            val seconds = (elapsedTime % 60000) / 1000
            val milliseconds = (elapsedTime % 1000) / 10
            val timeString = String.format("%02d:%02d:%02d.%02d", hours, minutes, seconds, milliseconds)
            stopWatchTextView.text = timeString
            elapsedTime += 10
            // 10밀리초 이후에 Runnable 다시 실행
            handler.postDelayed(this, 10)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStopWatchBinding.inflate(inflater)

        stopWatchTextView = _binding.stopwatchTextView
        startStopButton = _binding.startStopButton
        resetButton = _binding.resetButton
        lapButton = _binding.lapButton
        lapTextView = _binding.lapTextView

        startStopButton.setOnClickListener(onClickListener)
        resetButton.setOnClickListener(onClickListener)
        lapButton.setOnClickListener(onClickListener)

        return _binding.root
    }

    private val onClickListener = View.OnClickListener { view ->
        when (view.id) {
            R.id.startStopButton -> {
                if (isRunning) {
                    pauseStopwatch()
                } else {
                    startStopwatch()
                }
            }
            R.id.resetButton -> resetStopwatch()
            R.id.lapButton -> lapStopwatch()
        }
    }

    private fun startStopwatch() {
        isRunning = true
        startStopButton.text = "Stop"
        handler.post(runnable)
    }

    private fun pauseStopwatch() {
        isRunning = false
        startStopButton.text = "Start"
        // 예약된 Runnable 객체 삭제
        handler.removeCallbacks(runnable)
    }

    private fun resetStopwatch() {
        pauseStopwatch()
        elapsedTime = 0L
        stopWatchTextView.text = "00:00:00.00"
        lapTimes.clear()
        lapTextView.text = ""
    }

    private fun lapStopwatch() {
        val lapTime = stopWatchTextView.text.toString()
        val lapEntry = "${lapCounter++}. $lapTime"
        lapTimes.add(lapEntry)
        val lapText = lapTimes.joinToString("\n")
        lapTextView.text = lapText
    }

    override fun onDestroyView() {
        super.onDestroyView()
        handler.removeCallbacks(runnable)
    }

}