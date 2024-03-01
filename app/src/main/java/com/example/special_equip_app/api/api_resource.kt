package com.example.special_equip_app.api

import android.graphics.Bitmap
import android.util.Log
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.DataOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder


class api_resource {

    suspend fun logIn(login: String, password: String): loginResponse {
        val apiUrl = "http://194.67.68.5:8100/login/"
        val url = URL(apiUrl)

        return withContext(Dispatchers.IO) {
            try {
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "POST"  // Используйте POST вместо GET
                connection.setRequestProperty("Content-Type", "application/json")
                connection.doOutput = true

                // Создаем JSON-строку с логином и паролем
                val jsonInputString = "{\"login\":\"$login\",\"password\":\"$password\"}"

                // Отправляем JSON в тело запроса
                val outputStream = connection.outputStream
                outputStream.write(jsonInputString.toByteArray())
                outputStream.close()

                val inputStream = connection.inputStream
                val reader = BufferedReader(InputStreamReader(inputStream))
                val response = StringBuilder()
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    response.append(line)
                }

                val gson = Gson()
                gson.fromJson(response.toString(), loginResponse::class.java)
            } catch (e: Exception) {
                Log.e("LoginError", "Error fetching or parsing login data ", e)
                throw e
            }
        }
    }

    suspend fun sign_in(login: String, password: String, number_phone: String): signinResponse {
        val apiUrl = "http://194.67.68.5:8100/sign_in/"
        val url = URL(apiUrl)

        return withContext(Dispatchers.IO) {
            try {
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "POST"  // Используйте POST вместо GET
                connection.setRequestProperty("Content-Type", "application/json")
                connection.doOutput = true

                // Создаем JSON-строку с логином и паролем
                val jsonInputString = "{\"login\":\"$login\",\"password\":\"$password\",\"number_phone\":\"$number_phone\"}"

                // Отправляем JSON в тело запроса
                val outputStream = connection.outputStream
                outputStream.write(jsonInputString.toByteArray())
                outputStream.close()

                val inputStream = connection.inputStream
                val reader = BufferedReader(InputStreamReader(inputStream))
                val response = StringBuilder()
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    response.append(line)
                }

                val gson = Gson()
                gson.fromJson(response.toString(), signinResponse::class.java)
            } catch (e: Exception) {
                Log.e("LoginError", "Error fetching or parsing login data ", e)
                throw e
            }
        }
    }


    suspend fun get_all_equip(): List<all_equip> {
        val apiUrl = "http://194.67.68.5:8100/get_all_sp_eq/"
        val url = URL(apiUrl)

        return withContext(Dispatchers.IO) {
            try {
                val connection = url.openConnection() as HttpURLConnection

                val inputStream = connection.inputStream
                val reader = BufferedReader(InputStreamReader(inputStream))
                val response = StringBuilder()
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    response.append(line)
                }

                val gson = Gson()
                val newsResponse = gson.fromJson(response.toString(), Array<all_equip>::class.java)
                newsResponse.toList()
            } catch (e: Exception) {
                Log.e("NewsError", "Error fetching or parsing news data", e)
                throw e
            }
        }
    }

    suspend fun get_all_equip_user(user_id: Int?): List<Saved_orders> {
        val apiUrl = "http://194.67.68.5:8100/get_all_user_orders/"
        val url = URL(apiUrl)

        return withContext(Dispatchers.IO) {
            try {
                val connection = url.openConnection() as HttpURLConnection

                connection.requestMethod = "POST"  // Используйте POST вместо GET
                connection.setRequestProperty("Content-Type", "application/json")
                connection.doOutput = true

                // Создаем JSON-строку с логином и паролем
                val jsonInputString = "{\"user_id\":\"$user_id\"}"

                // Отправляем JSON в тело запроса
                val outputStream = connection.outputStream
                outputStream.write(jsonInputString.toByteArray())
                outputStream.close()

                val inputStream = connection.inputStream
                val reader = BufferedReader(InputStreamReader(inputStream))
                val response = StringBuilder()
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    response.append(line)
                }

                val gson = Gson()
                val newsResponse = gson.fromJson(response.toString(), Array<Saved_orders>::class.java)
                newsResponse.toList()
            } catch (e: Exception) {
                Log.e("NewsError", "Error fetching or parsing news data", e)
                throw e
            }
        }
    }


    suspend fun createOrder(
        user_id: Int?,
        equipment_id: Int?,
        rental_date: String?,
        rental_time: Int?,
        total_price: Double?,
        order_status: String?,
        type_works: String?,
        user_photo: File?
    ): signinResponse {
        val apiUrl = "http://194.67.68.5:8100/create_order/"
        val url = URL(apiUrl)

        return withContext(Dispatchers.IO) {
            try {
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "POST"
                connection.setRequestProperty("Content-Type", "multipart/form-data;boundary=boundary")
                connection.doOutput = true

                // Создаем поток для записи в тело запроса
                val outputStream = DataOutputStream(connection.outputStream)
                Log.e("666", "$user_id $equipment_id $rental_date $rental_time ${total_price} ${order_status} $type_works $user_photo")

                // Пишем JSON-данные в тело запроса
                writeFormField(outputStream, "user_id", user_id.toString())
                writeFormField(outputStream, "equipment_id", equipment_id.toString())
                writeFormField(outputStream, "rental_date", rental_date ?: "")
                writeFormField(outputStream, "rental_time", rental_time?.toString() ?: "")
                writeFormField(outputStream, "total_price", total_price?.toString() ?: "")
                writeFormField(outputStream, "order_status", order_status?.toByteArray(Charsets.UTF_8)?.toString(Charsets.ISO_8859_1) ?: "")
                writeFormField(outputStream, "type_works", type_works?.toByteArray(Charsets.UTF_8)?.toString(Charsets.ISO_8859_1) ?: "")



                // Если есть фотография, пишем ее в тело запроса
                if (user_photo != null) {
                    outputStream.writeBytes("--boundary\r\n")
                    outputStream.writeBytes("Content-Disposition: form-data; name=\"user_photo\";filename=\"${user_photo.name}\"\r\n")
                    outputStream.writeBytes("\r\n")
                    user_photo.inputStream().use { input ->
                        input.copyTo(outputStream)
                    }
                    outputStream.writeBytes("\r\n")
                }

                // Закрываем поток
                outputStream.writeBytes("--boundary--\r\n")
                outputStream.flush()
                outputStream.close()

                // Получаем ответ от сервера
                val responseCode = connection.responseCode
                val message = if (responseCode == HttpURLConnection.HTTP_CREATED) {
                    "Order created successfully"
                } else {
                    "Failed to create order"
                }
                signinResponse(message)
            } catch (e: Exception) {
                Log.e("OrderError", "Error creating order", e)
                signinResponse("Error creating order")
            }
        }
    }

    private fun writeFormField(outputStream: DataOutputStream, fieldName: String, value: String) {
        outputStream.writeBytes("--boundary\r\n")
        outputStream.writeBytes("Content-Disposition: form-data; name=\"$fieldName\"\r\n")
        outputStream.writeBytes("\r\n")
        outputStream.writeBytes(value)
        outputStream.writeBytes("\r\n")
    }









}