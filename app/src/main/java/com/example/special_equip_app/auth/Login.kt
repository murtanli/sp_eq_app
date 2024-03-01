package com.example.special_equip_app.auth

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.special_equip_app.MainActivity
import com.example.special_equip_app.R
import com.example.special_equip_app.api.api_resource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

class Login : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val buttonLogin = findViewById<Button>(R.id.button_auth)
        val login_text = findViewById<EditText>(R.id.login_text)
        val password_text = findViewById<EditText>(R.id.password_text)
        val text_ext = findViewById<TextView>(R.id.text_ext)
        val errorText = findViewById<TextView>(R.id.textView2)

        val sharedPreferences = getSharedPreferences("myPreferences", Context.MODE_PRIVATE)
        val login_save = sharedPreferences.getString("login", "")

        supportActionBar?.hide()

        if (login_save == "true"){
            val intent = Intent(this@Login, MainActivity::class.java)
            startActivity(intent)
        }

        text_ext.setOnClickListener {
            val intent = Intent(this, Sign_in::class.java)
            startActivity(intent)
        }

        buttonLogin.setOnClickListener {
            val loginText = login_text?.text?.toString()
            val passwordText = password_text?.text?.toString()

            if (loginText.isNullOrBlank() || passwordText.isNullOrBlank()) {
                errorText.text = "Введите данные в поля"
            } else {
                GlobalScope.launch(Dispatchers.Main) {
                    try {
                        // Вызываем функцию logIn для выполнения запроса
                        val data = api_resource()
                        val result = data.logIn(loginText, passwordText)

                        if (result != null) {
                            if (result.message != "Пользователь не найден" && result.message != "Неправильный пароль") {
                                // Если успешно авторизованы, выводим сообщение об успешной авторизации и обрабатываем данные
                                Log.d("LoginActivity", "Login successful")
                                //Log.d("LoginActivity", "User ID: ${result.user_data.user_id}")
                                errorText.text = result.message

                                val sharedPreferences =
                                    getSharedPreferences("myPreferences", Context.MODE_PRIVATE)
                                val editor = sharedPreferences.edit()
                                try {
                                    editor.putString("user_id", result.user_id.toString())

                                    Log.e("555", "${result.user_id}  ${result.message}")
                                    editor.putString("user_name", loginText)
                                    editor.putString("login", "true")
                                    editor.apply()
                                } catch (e: Exception) {
                                    editor.putString("user_id", result.user_id.toString())
                                    editor.putString("user_name", loginText)
                                    editor.putString("login", "true")
                                    editor.apply()
                                }

                                val intent = Intent(this@Login, MainActivity::class.java)
                                startActivity(intent)
                                //ErrorText.setTextColor(R.color.blue)

                            } else {
                                // Если произошла ошибка, выводим сообщение об ошибке
                                Log.e("LoginActivity", "Login failed")
                                errorText.text = result.message
                                val sharedPreferences =
                                    getSharedPreferences("myPreferences", Context.MODE_PRIVATE)
                                val editor = sharedPreferences.edit()
                                editor.putString("login", "false")
                                editor.apply()
                            }
                        } else {
                            // Обработка случая, когда result равен null
                            Log.e("LoginActivity", "Login failed - result is null")
                            errorText.text = "Ошибка в процессе авторизации ${result.message}"
                        }
                    } catch (e: Exception) {
                        // Ловим и обрабатываем исключения, например, связанные с сетевыми ошибками
                        Log.e("LoginActivity", "Error during login", e)
                        e.printStackTrace()
                        errorText.text = "Ошибка входа: Неправильный пароль или профиль не найден"
                        val sharedPreferences =
                            getSharedPreferences("myPreferences", Context.MODE_PRIVATE)
                        val editor = sharedPreferences.edit()
                        editor.putString("login", "false")
                        editor.apply()
                    }
                }
            }
        }

    }
    override fun onBackPressed() {

    }

}
