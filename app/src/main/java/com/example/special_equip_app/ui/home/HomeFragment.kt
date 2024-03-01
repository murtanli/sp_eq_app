package com.example.special_equip_app.ui.home

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.special_equip_app.MainActivity
import com.example.special_equip_app.api.api_resource
import com.example.special_equip_app.databinding.FragmentHomeBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var currentPhotoPath: String
    private var selectedDate = ""
    private var fl_price = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        (activity as? MainActivity)?.act_bar()

        val type_works_edit = binding.typeWorksEdit
        val add_sp_eq = binding.addSpEq
        val button_set_date = binding.buttonSetDate
        val total_price = binding.totalPrice
        val button_rental = binding.buttonRental
        val photo_add = binding.photoAdd
        val time_edit = binding.timeEdit

        photo_add.setOnClickListener {
            dispatchTakePictureIntent()
        }

        add_sp_eq.setOnClickListener {
            val intent = Intent(requireContext(), All_sp_eq::class.java)
            startActivity(intent)
        }

        button_set_date.setOnClickListener {
            // Получаем текущую дату
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)

            // Создаем диалог выбора даты
            val datePickerDialog = DatePickerDialog(requireContext(), { _, selectedYear, selectedMonth, selectedDay ->
                // Формируем строку с выбранной датой
                selectedDate = "$selectedYear-${selectedMonth + 1}-$selectedDay"

                // Дальнейшие действия с выбранной датой
                // Например, установка этой даты в текстовое поле
                button_set_date.text = selectedDate
            }, year, month, dayOfMonth)

            // Показываем диалог выбора даты
            datePickerDialog.show()
        }
        val sharedPreference = requireContext().getSharedPreferences("myPreferences", Context.MODE_PRIVATE)

        val time = time_edit.text.toString()

        time_edit.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Ничего не делаем перед изменением текста
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Ничего не делаем в момент изменения текста
            }

            override fun afterTextChanged(s: Editable?) {
                val priceString = sharedPreference.getString("price", "")
                val timeString = time_edit.text.toString()

                if (!priceString.isNullOrEmpty() && timeString.isNotEmpty()) {
                    fl_price = priceString.toInt() * timeString.toInt()
                    total_price.text = "Цена - $fl_price"
                } else {
                    total_price.text = "Цена - 0"
                }
            }
        })



        button_rental.setOnClickListener {
            if (!::currentPhotoPath.isInitialized) {
                Toast.makeText(context, "Сначала сделайте фото", Toast.LENGTH_SHORT).show()
            } else {
                val sharedPreference = requireContext().getSharedPreferences("myPreferences", Context.MODE_PRIVATE)
                val user_id = sharedPreference.getString("user_id", "")?.toIntOrNull()
                val sp_eq_id = sharedPreference.getString("sp_eq_id", "")?.toIntOrNull()

                if (sp_eq_id == null){
                    Toast.makeText(context, "Выберите транспорт", Toast.LENGTH_SHORT).show()
                } else {
                    if(time_edit.text.isNullOrEmpty() && type_works_edit.text.isNullOrEmpty() && selectedDate.isEmpty()){
                        Toast.makeText(context, "Заполните поля !", Toast.LENGTH_SHORT).show()
                    } else {
                        val time = time_edit.text.toString()

                        val time_int = if (time.isNotBlank()) {
                            time.toInt()
                        } else {
                            // Обработка случая, когда текст отсутствует
                            0 // или какое-то другое значение по умолчанию
                        }


                        Log.e("666", "$user_id $sp_eq_id $selectedDate $time_int ${fl_price} ${type_works_edit.text.toString()}")
                        GlobalScope.launch {
                            val result = api_resource().createOrder(
                                user_id = user_id,
                                equipment_id = sp_eq_id,
                                rental_date = selectedDate,
                                rental_time = time_int,
                                total_price = fl_price.toDouble(),
                                order_status = "На рассмотрении",
                                type_works = type_works_edit.text.toString(),
                                user_photo = File(currentPhotoPath)
                            )
                            withContext(Dispatchers.Main) {
                                Toast.makeText(context, result.message, Toast.LENGTH_SHORT).show()
                            }
                        }
                    }

                }

            }
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(requireContext().packageManager)?.also {
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    Log.e("Error", "Error creating image file: ${ex.message}", ex)
                    null
                }
                photoFile?.also {
                    val photoURI = FileProvider.getUriForFile(
                        requireContext(),
                        "com.example.special_equip_app.fileprovider",
                        it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
                }
            }
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir: File? = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_",
            ".jpg",
            storageDir
        ).apply {
            currentPhotoPath = absolutePath
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            Toast.makeText(context, "Фото сохранено", Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        private const val REQUEST_IMAGE_CAPTURE = 1
    }


}
