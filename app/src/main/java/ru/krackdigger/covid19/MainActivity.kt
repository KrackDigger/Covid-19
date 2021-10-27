package ru.krackdigger.covid19

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import ru.krackdigger.covid19.databinding.ActivityMainBinding
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*


class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    var onReload = false

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val webSettings: WebSettings = binding.webView.getSettings()
        webSettings.javaScriptEnabled = true
        binding.webView.webViewClient = MyWebViewClient()
        onCreateHtml()

        binding.swipeRefreshLayout.setOnRefreshListener {

            onReload = true
            onCreateHtml()
            binding.swipeRefreshLayout.isRefreshing = false
        }
    }

    inner class MyWebViewClient : WebViewClient() {
        @TargetApi(Build.VERSION_CODES.N)
        override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
            view.loadUrl(request.url.toString())
            return true
        }

        // Для старых устройств
        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
            view.loadUrl(url)
            return true
        }

        override fun onPageFinished(view: WebView?, url: String?) {

            if (onReload) {
                binding.webView.clearHistory()
                onReload = false
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun onCreateHtml() {

        binding.webView.loadUrl("https://yandex.ru/covid19/stat")
//        val currentTime: Date = Calendar.getInstance().time
//        binding.textView.text = currentTime.toString()

        var answer1 = ""
        var answer2 = ""
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val current = LocalDateTime.now()
            val formatter1 = DateTimeFormatter.ofPattern("dd.MM.yyyy")
            val formatter2 = DateTimeFormatter.ofPattern("HH:mm:ss")
            answer1 =  current.format(formatter1)
            answer2 =  current.format(formatter2)
        } else {
            val date = Date()
            val formatter1 = SimpleDateFormat("MMM dd yyyy")
            val formatter2 = SimpleDateFormat("HH:mma")
            answer1 = formatter1.format(date)
            answer2 = formatter2.format(date)
        }
        binding.textView.text = answer1
        binding.textView2.text = answer2
        binding.webView.clearHistory()
    }

    override fun onBackPressed() {
        if (binding.webView.canGoBack()) {
            binding.webView.goBack()
        } else {
            super.onBackPressed()
        }
    }
}