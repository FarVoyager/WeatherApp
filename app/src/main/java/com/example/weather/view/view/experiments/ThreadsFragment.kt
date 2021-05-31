package com.example.weather.view.view.experiments

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.view.*
import androidx.fragment.app.Fragment
import androidx.appcompat.widget.AppCompatTextView
import com.example.weather.R
import com.example.weather.databinding.FragmentThreadsBinding
import java.util.*
import java.util.concurrent.TimeUnit


class ThreadsFragment : Fragment() {

    private var _binding: FragmentThreadsBinding? = null
    private val binding get() = _binding!!

    private var counterThread = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentThreadsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Handler_Thread button
        val handlerThread = HandlerThread("HandlerThread")
        handlerThread.start()
        val handler = Handler(handlerThread.looper)
        binding.calcThreadHandler.setOnClickListener {
            binding.mainContainer.addView(AppCompatTextView(it.context).apply {
                text = String.format(getString(R.string.calculate_in_thread), handlerThread.name)
                textSize = resources.getDimension(R.dimen.main_container_text_size)
            })
            handler.post {
                startCalculations(binding.editText.text.toString().toInt())
                binding.mainContainer.post {
                    binding.mainContainer.addView(AppCompatTextView(it.context).apply {
                        text = String.format(
                            getString(R.string.calculate_in_thread),
                            Thread.currentThread().name
                        )
                        textSize = resources.getDimension(R.dimen.main_container_text_size)
                    })
                }
            }
        }

        //Расчет button
        binding.button.setOnClickListener {
            binding.textView.text = startCalculations(binding.editText.text.toString().toInt())
            binding.mainContainer.addView(AppCompatTextView(it.context).apply {
                text = getString(R.string.in_main_thread)
                textSize = resources.getDimension(R.dimen.main_container_text_size)
            })
        }

        //Расчет в потоке button
        binding.calcThreadBtn.setOnClickListener {
            Thread {
                counterThread++
                val calculatedText = startCalculations(binding.editText.text.toString().toInt())
                activity?.runOnUiThread {
                    binding.textView.text = calculatedText
                    binding.mainContainer.addView(AppCompatTextView(it.context).apply {
                        text = getString(R.string.from_thread)
                        textSize = resources.getDimension(R.dimen.main_container_text_size)
                    })
                }

            }.start()
        }

        binding.serviceButton.setOnClickListener {
            context?.let {
                it.startService(
                    Intent(it, MainService::class.java)
                        .putExtra(
                            MAIN_SERVICE_STRING_EXTRA,
                            getString(R.string.hello_from_thread_fragment)
                        )
                )
            }
        }

    }

    private fun startCalculations(seconds: Int): String {
        val date = Date()
        var diffInSec: Long
        do {
            val currentDate = Date()
            val diffInMs: Long = currentDate.time - date.time
            diffInSec = TimeUnit.MILLISECONDS.toSeconds(diffInMs)
        } while (diffInSec < seconds)
        return diffInSec.toString()

    }

    companion object {
        @JvmStatic
        fun newInstance() = ThreadsFragment()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.thread_screen_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_threads_back -> {
                parentFragmentManager.popBackStack()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}