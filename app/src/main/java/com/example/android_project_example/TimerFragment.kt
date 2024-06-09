package com.example.android_project_example

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.android_project_example.databinding.FragmentTimerBinding

class TimerFragment : Fragment() {

    private lateinit var _binding: FragmentTimerBinding

    private val NOTIFICATION_PERMISSION_REQUEST_CODE = 100
    private val CHANNEL_ID = "timer_channel"

    private lateinit var timeEditText: EditText
    private lateinit var timeRemainingTextView: TextView
    private lateinit var startButton: Button
    private var countDownTimer: CountDownTimer? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTimerBinding.inflate(inflater)

        createNotificationChannel()
        timeEditText = _binding.timeEditText
        timeRemainingTextView = _binding.timeRemainingTextView
        startButton = _binding.startButton
        startButton.setOnClickListener {
            startTimer()
        }

        return _binding.root
    }

    private fun createNotificationChannel() {
        val name = "타이머 채널"
        val descriptionText = "타이머 알림을 위한 채널 허가"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
            description = descriptionText
        }
        val notificationManager = requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    private fun startTimer() {
        val time = timeEditText.text.toString().toLongOrNull()
        if (time != null) {
            countDownTimer?.cancel()
            countDownTimer = object : CountDownTimer(time * 1000, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    val secondsRemaining = millisUntilFinished / 1000
                    timeRemainingTextView.text = "남은 시간: $secondsRemaining 초"
                }

                override fun onFinish() {
                    // 타이머 끝난 후 수행할 작업
                    if (ContextCompat.checkSelfPermission(
                            requireContext(),
                            android.Manifest.permission.POST_NOTIFICATIONS
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        showNotification()
                    } else {
                        requestNotificationPermission()
                    }
                }
            }.start()
        }
    }

    private fun showNotification() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            val builder = NotificationCompat.Builder(requireContext(), "timer_channel")
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("타이머")
                .setContentText("시간 끝!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)

            val notificationManager = NotificationManagerCompat.from(requireContext())
            notificationManager.notify(0, builder.build())
        } else {
            requestNotificationPermission()
        }
    }

    private fun requestNotificationPermission() {
        if (shouldShowRequestPermissionRationale(android.Manifest.permission.POST_NOTIFICATIONS)) {
            Toast.makeText(context, "타이머를 이용하기 위해서는 권한을 허용해야 합니다.", Toast.LENGTH_SHORT).show()
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), NOTIFICATION_PERMISSION_REQUEST_CODE)
        } else {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), NOTIFICATION_PERMISSION_REQUEST_CODE)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == NOTIFICATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 권한 허용 시
                showNotification()
            } else {
                Toast.makeText(context, "타이머를 이용하기 위해서는 권한을 허용해야 합니다.", Toast.LENGTH_SHORT).show()
                ActivityCompat.requestPermissions(requireActivity(), arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), NOTIFICATION_PERMISSION_REQUEST_CODE)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        countDownTimer?.cancel()
    }

}