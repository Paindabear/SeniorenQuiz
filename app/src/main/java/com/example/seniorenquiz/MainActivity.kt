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
                R.id.nav_images -> {
                    loadFragment(ImageStartFragment())
                    true
                }
                R.id.nav_audio -> {
                    loadFragment(AudioStartFragment())
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
        val dialogView = layoutInflater.inflate(R.layout.dialog_update_progress, null)
        val progressBar = dialogView.findViewById<android.widget.ProgressBar>(R.id.progressBar)
        val tvPercent = dialogView.findViewById<android.widget.TextView>(R.id.tvProgressPercent)

        val dialog = AlertDialog.Builder(this)
            .setTitle("Update wird heruntergeladen")
            .setView(dialogView)
            .setCancelable(false)
            .create()
        
        if (!isFinishing) {
            dialog.show()
        }

        Thread {
            try {
                val file = File(filesDir, "update.apk")
                
                // Stream download directly to file
                downloadToFile(apkUrl, file) { progress ->
                    runOnUiThread {
                        if (!isFinishing) {
                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                                progressBar.setProgress(progress, true)
                            } else {
                                progressBar.progress = progress
                            }
                            tvPercent.text = "$progress%"
                        }
                    }
                }

                if (file.exists() && file.length() > 0) {
                    runOnUiThread { 
                        if (!isFinishing) {
                            dialog.dismiss()
                            installApk(file) 
                        }
                    }
                } else {
                    throw Exception("Download fehlgeschlagen (Datei leer)")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                runOnUiThread {
                    if (!isFinishing) {
                        try { dialog.dismiss() } catch (ex: Exception) {}
                        Toast.makeText(this, "Fehler: ${e.message}", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }.start()
    }

    private fun downloadToFile(urlString: String, destination: File, onProgress: (Int) -> Unit) {
        val url = URL(urlString)
        val connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "GET"
        connection.setRequestProperty("User-Agent", "SeniorenQuiz/1.0 (Android)")
        connection.connectTimeout = 30_000
        connection.readTimeout = 60_000
        connection.instanceFollowRedirects = true
        connection.connect()

        val responseCode = connection.responseCode
        if (responseCode != HttpURLConnection.HTTP_OK) {
            throw Exception("Server antwortete mit Code $responseCode")
        }

        val totalSize = connection.contentLength
        
        connection.inputStream.use { input ->
            java.io.FileOutputStream(destination).use { output ->
                val buffer = ByteArray(8192)
                var bytesRead: Int
                var totalRead = 0L
                
                while (input.read(buffer).also { bytesRead = it } != -1) {
                    output.write(buffer, 0, bytesRead)
                    totalRead += bytesRead
                    
                    if (totalSize > 0) {
                        val percent = (totalRead * 100 / totalSize).toInt()
                        onProgress(percent)
                    }
                }
            }
        }
        connection.disconnect()
    }

    private fun installApk(file: File) {
        try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                if (!packageManager.canRequestPackageInstalls()) {
                    Toast.makeText(this, "Bitte 'Unbekannte Apps installieren' erlauben", Toast.LENGTH_LONG).show()
                    startActivity(Intent(android.provider.Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES).apply {
                        data = android.net.Uri.parse("package:$packageName")
                    })
                    return
                }
            }

            val uri = FileProvider.getUriForFile(this, "${packageName}.fileprovider", file)
            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, "application/vnd.android.package-archive")
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Install-Fehler: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}

private data class VersionInfo(val versionCode: Int, val apkUrl: String)