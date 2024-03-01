package com.example.special_equip_app.auth

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.example.special_equip_app.R
import com.example.special_equip_app.api.api_resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class Sign_in : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)


        val button_sign = findViewById<Button>(R.id.button_sign)
        val login_text_sign = findViewById<EditText>(R.id.login_text_sign)
        val pasword_text_sign = findViewById<EditText>(R.id.pasword_text_sign)
        val pasword_text_sign2 = findViewById<EditText>(R.id.pasword_text_sign2)
        val error_text = findViewById<TextView>(R.id.textView3)
        val number_phone_edit = findViewById<TextView>(R.id.number_phone_text)

        supportActionBar?.hide()

        button_sign.setOnClickListener {
            if (!login_text_sign.text.isNullOrEmpty() && !pasword_text_sign.text.isNullOrEmpty() && !pasword_text_sign2.text.isNullOrEmpty()) {

                val loginText = login_text_sign?.text?.toString()
                val passwordText = pasword_text_sign?.text?.toString()
                GlobalScope.launch(Dispatchers.Main) {
                    try {
                        val data = api_resource()
                        val result = data.sign_in(
                            loginText.toString(),
                            passwordText.toString(),
                            number_phone_edit.text.toString())

                        if (result != null) {
                            val intent = Intent(this@Sign_in, Login::class.java)
                            startActivity(intent)
                            error_text.text = result.message

                            val sharedPreferences = getSharedPreferences("myPreferences", Context.MODE_PRIVATE)
                            val editor = sharedPreferences.edit()
                            editor.putString("login", "false")
                            editor.apply()

                        } else {
                            // Обработка случая, когда result равен null
                            Log.e("LoginActivity", "Login failed - result is null")
                            error_text.text = "Ошибка в процессе авторизации ${result.message}"
                        }
                    } catch (e: Exception) {
                        // Ловим и обрабатываем исключения, например, связанные с сетевыми ошибками
                        Log.e("LoginActivity", "Error during login", e)
                        e.printStackTrace()
                        error_text.text = "Ошибка входа: Неправильный пароль или профиль уже существует"
                    }
                }
            } else {
                error_text.text = "Пустые поля ! либо пороли не совпадают"
            }
        }

    }
}