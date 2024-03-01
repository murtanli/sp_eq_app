package com.example.special_equip_app.ui.home

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import com.bumptech.glide.Glide
import androidx.core.content.ContextCompat
import com.example.special_equip_app.R
import com.example.special_equip_app.api.api_resource
import com.makeramen.roundedimageview.RoundedImageView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale
import android.text.Html
import androidx.core.app.ShareCompat.IntentReader
import com.example.special_equip_app.MainActivity

class All_sp_eq : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_sp_eq)

        val container = findViewById<LinearLayout>(R.id.Container)
        val progressBar = findViewById<ProgressBar>(R.id.progressBar)

        progressBar.visibility = View.VISIBLE
        GlobalScope.launch(Dispatchers.Main) {
            try {
                progressBar.visibility = View.VISIBLE
                val data = api_resource()
                val result = data.get_all_equip()
                if (result.isNotEmpty()) {
                    val title = TextView(this@All_sp_eq)
                    title.text = "Спецтехника"
                    title.setPadding(100,100,100,100)
                    title.gravity = Gravity.CENTER
                    title.setTextColor(ContextCompat.getColor(this@All_sp_eq, R.color.white))
                    title.textSize = 30F

                    container.addView(title)

                    for (spec_technick in result) {

                        val imageView = RoundedImageView(this@All_sp_eq)
                        Glide.with(this@All_sp_eq)
                            .load(spec_technick.photo)
                            .into(imageView)
                        Log.e("777", spec_technick.photo)

                        val block = create_block_ser(
                            spec_technick.pk,
                            spec_technick.name,
                            spec_technick.description,
                            spec_technick.rental_price,
                            spec_technick.count_available,
                            imageView
                        )
                        container.addView(block)
                        container.gravity = Gravity.CENTER
                    }

                } else {
                    // Обработка случая, когда список пуст
                    Log.e("BusActivity", "Response failed - result is empty")

                    //val error = createBusEpty()
                    //BusesContainer.addView(error)
                }
            } catch (e: Exception) {
                // Ловим и обрабатываем исключения, например, связанные с сетевыми ошибками
                Log.e("BusActivity", "Error during response", e)
                e.printStackTrace()
            }
            progressBar.visibility = View.GONE
        }

    }

    private fun create_block_ser(id: Int, name:String, description: String, rental_price: Int, count_available: Int, image: RoundedImageView): LinearLayout {
        //общий блок
        val block = LinearLayout(this)

        val blockParams = LinearLayout.LayoutParams(
            1000,
            1500
        )
        blockParams.setMargins(0, 0, 10, 0)
        blockParams.bottomMargin = 100
        block.layoutParams = blockParams
        //block.gravity = Gravity.CENTER
        block.orientation = LinearLayout.VERTICAL
        val backgroundDrawable = ContextCompat.getDrawable(this, R.drawable.rounded_view)
        block.background = backgroundDrawable

        //image
        val imageLayputParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            500
        )

        //imageLayputParams.setMargins(20, 20, 20, 20)
        image.cornerRadius = 20F
        image.layoutParams = imageLayputParams
        image.scaleType = ImageView.ScaleType.CENTER_CROP

        //название машины
        val brand_text = TextView(this)
        brand_text.text = name
        brand_text.setTextAppearance(R.style.Title_style)

        brand_text.setPadding(30,50,30,0)
        brand_text.gravity = Gravity.LEFT

        //блок информация
        val block_inf = LinearLayout(this)
        val block_inf_params = LinearLayout.LayoutParams(
            900,
            1000
        )
        block_inf_params.setMargins(50, 0, 0, 0)
        block_inf_params.bottomMargin = 10
        block_inf.layoutParams = block_inf_params
        //block.gravity = Gravity.CENTER
        block_inf.orientation = LinearLayout.VERTICAL


        val mileage_text = TextView(this)
        mileage_text.text = Html.fromHtml("&#8226; <b>Описание:</b> ${description} км", Html.FROM_HTML_MODE_COMPACT)
        mileage_text.setTextAppearance(R.style.Text_style)

        mileage_text.setPadding(30,20,30,0)
        mileage_text.gravity = Gravity.LEFT


        //количетсво свободных машин

        val cindition_text = TextView(this)
        cindition_text.text = Html.fromHtml("&#8226; <b>Количетсво свободного транспорта:</b> ${count_available}", Html.FROM_HTML_MODE_COMPACT)
        cindition_text.setTextAppearance(R.style.Text_style)

        cindition_text.setPadding(30,20,30,0)
        cindition_text.gravity = Gravity.LEFT

        //владельцы
        val formater = NumberFormat.getNumberInstance(Locale.getDefault())
        val formattedRentalPrice  = formater.format(rental_price)

        val text_price = TextView(this)
        text_price.text = Html.fromHtml("Цена - ${formattedRentalPrice} руб", Html.FROM_HTML_MODE_COMPACT)
        text_price.setTextAppearance(R.style.Title_style)

        text_price.setPadding(40,50,0,0)
        text_price.gravity = Gravity.LEFT



        block.addView(image)
        block.addView(brand_text)


        block_inf.addView(mileage_text)
        block_inf.addView(cindition_text)
        block.addView(text_price)

        block.addView(block_inf)


        block.setOnClickListener {
            val sharedPreference = getSharedPreferences("myPreferences", Context.MODE_PRIVATE)
            val editor = sharedPreference.edit()
            editor.remove("sp_eq_id")
            editor.remove("price")
            editor.putString("sp_eq_id", id.toString())
            editor.putString("price", rental_price.toString())
            editor.apply()

            Log.e("7666", "$id  $rental_price")
            val intent = Intent(this@All_sp_eq, MainActivity::class.java)
            startActivity(intent)
        }


        return block

    }
}