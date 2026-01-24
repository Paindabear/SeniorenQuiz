package com.example.seniorenquiz

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.Gson
import java.io.ByteArrayOutputStream
import java.io.File
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : AppCompatActivity() {

    private val versionJsonUrl = "https://raw.githubusercontent.com/Petlus/SeniorenQuiz/master/version.json"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        // Load default fragment
        if (savedInstanceState == null) {
            loadFragment(CategorySelectionFragment())
        }

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_quiz -> {
                    loadFragment(CategorySelectionFragment())
                    true
                }
                R.id.nav_fairytale -> {
                    loadFragment(FairyTaleFragment())
                    true
                }
                else -> false
            }
        }

        checkForAppUpdate()
    }

    private fun checkForAppUpdate() {
        Thread {
            try {
                val json = URL(versionJsonUrl).readText()
                val info = Gson().fromJson(json, VersionInfo::class.java)
                if (info.versionCode > BuildConfig.VERSION_CODE && info.apkUrl.isNotBlank()) {
                    runOnUiThread { showUpdateDialog(info.apkUrl) }
                }
            } catch (e: Exception) {
                // version.json fehlt oder Netzwerkfehler – ignorieren
            }
        }.start()
    }

    private fun showUpdateDialog(apkUrl: String) {
        AlertDialog.Builder(this)
            .setTitle("Update verfügbar")
            .setMessage("Es ist eine neue Version verfügbar. Jetzt herunterladen?")
            .setPositiveButton("Update laden") { _, _ ->
                downloadAndInstallApk(apkUrl)
            }
            .setNegativeButton("Später", null)
            .show()
    }

    private fun downloadAndInstallApk(apkUrl: String) {
        Toast.makeText(this, "Lade herunter…", Toast.LENGTH_SHORT).show()
        Thread {
            try {
                var bytes = downloadWithHttp(apkUrl)
                // Google Drive: oft HTML "Virus-Scan"-Seite bei größeren Dateien – mit confirm=yes erneut versuchen
                if (apkUrl.contains("drive.google.com") && bytes != null && bytes.size < 100_000 && String(bytes, Charsets.UTF_8).trimStart().startsWith("<")) {
                    val retryUrl = if (apkUrl.contains("?")) "$apkUrl&confirm=yes" else "$apkUrl?confirm=yes"
                    bytes = downloadWithHttp(retryUrl)
                }
                if (bytes != null && bytes.isNotEmpty()) {
                    val file = File(filesDir, "update.apk")
                    file.writeBytes(bytes)
                    runOnUiThread { installApk(file) }
                } else {
                    throw Exception("Leere Antwort")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                runOnUiThread {
                    Toast.makeText(this, "Download fehlgeschlagen", Toast.LENGTH_LONG).show()
                }
            }
        }.start()
    }

    private fun downloadWithHttp(urlString: String): ByteArray? {
        val url = URL(urlString)
        (url.openConnection() as? HttpURLConnection)?.run {
            try {
                setRequestProperty("User-Agent", "SeniorenQuiz/1.0 (Android)")
                connectTimeout = 30_000
                readTimeout = 60_000
                instanceFollowRedirects = true
                inputStream.use { input ->
                    ByteArrayOutputStream().use { output ->
                        val buffer = ByteArray(8192)
                        var n: Int
                        while (input.read(buffer).also { n = it } != -1) output.write(buffer, 0, n)
                        return output.toByteArray()
                    }
                }
            } finally {
                disconnect()
            }
        }
        return null
    }

    private fun installApk(file: File) {
        try {
            val uri = FileProvider.getUriForFile(this, "${packageName}.fileprovider", file)
            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, "application/vnd.android.package-archive")
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(this, "Installation fehlgeschlagen", Toast.LENGTH_LONG).show()
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}

private data class VersionInfo(val versionCode: Int, val apkUrl: String)