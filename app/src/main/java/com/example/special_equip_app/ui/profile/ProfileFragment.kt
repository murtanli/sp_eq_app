package com.example.special_equip_app.ui.profile

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.special_equip_app.MainActivity
import com.example.special_equip_app.R
import com.example.special_equip_app.api.api_resource
import com.example.special_equip_app.auth.Login
import com.example.special_equip_app.databinding.FragmentProfileBinding
import com.makeramen.roundedimageview.RoundedImageView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val dashboardViewModel =
            ViewModelProvider(this).get(ProfileViewModel::class.java)

        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val container = binding.Container
        val progressBar = binding.progressBar
        val profile_name = binding.profileName
        val button_exit = binding.buttonExit

        progressBar.visibility = View.VISIBLE

        val sharedPreference = requireContext().getSharedPreferences("myPreferences", Context.MODE_PRIVATE)
        val user_id = sharedPreference.getString("user_id", "")?.toIntOrNull()
        val user_name = sharedPreference.getString("user_name", "")

        profile_name.text = user_name

        button_exit.setOnClickListener {
            val sharedPreferences = requireContext().getSharedPreferences("myPreferences", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()

            editor.remove("user_id")
            editor.remove("user_name")
            editor.putString("login", "false")
            editor.apply()

            val intent = Intent(requireContext(), Login::class.java)
            startActivity(intent)
        }

        GlobalScope.launch(Dispatchers.Main) {
            try {
                progressBar.visibility = View.VISIBLE
                val data = api_resource()
                val result = data.get_all_equip_user(user_id)
                if (result.isNotEmpty()) {
                    val title = TextView(requireContext())
                    title.text = "Спецтехника"
                    title.setPadding(100,100,100,100)
                    title.gravity = Gravity.CENTER
                    title.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                    title.textSize = 30F

                    container.addView(title)

                    for (spec_technick in result) {
                        Log.e("777", spec_technick.order_status)
                        val imageView = RoundedImageView(requireContext())
                        Glide.with(requireContext())
                            .load(spec_technick.equipment.photo)
                            .into(imageView)


                        val block = create_block_ser(
                            spec_technick.rental_time,
                            spec_technick.equipment.pk,
                            spec_technick.equipment.name,
                            spec_technick.equipment.description,
                            spec_technick.equipment.rental_price,
                            spec_technick.rental_date,
                            spec_technick.order_status,
                            spec_technick.total_price,
                            spec_technick.type_works,
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

        return root
    }

    private fun create_block_ser(rental_time: Int, id: Int, name: String, description: String, rental_price: Int,rental_date: String, order_status: String, total_price: Int, type_works: String, image: RoundedImageView): LinearLayout {
        //общий блок
        val block = LinearLayout(requireContext())

        val blockParams = LinearLayout.LayoutParams(
            1000,
            1700
        )
        blockParams.setMargins(0, 0, 10, 0)
        blockParams.bottomMargin = 100
        block.layoutParams = blockParams
        //block.gravity = Gravity.CENTER
        block.orientation = LinearLayout.VERTICAL
        val backgroundDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.rounded_view)
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
        val brand_text = TextView(requireContext())
        brand_text.text = name
        brand_text.setTextAppearance(R.style.Title_style)

        brand_text.setPadding(30,50,30,0)
        brand_text.gravity = Gravity.LEFT

        //блок информация
        val block_inf = LinearLayout(requireContext())
        val block_inf_params = LinearLayout.LayoutParams(
            900,
            1000
        )
        block_inf_params.setMargins(50, 0, 0, 0)
        block_inf_params.bottomMargin = 10
        block_inf.layoutParams = block_inf_params
        //block.gravity = Gravity.CENTER
        block_inf.orientation = LinearLayout.VERTICAL


        val mileage_text = TextView(requireContext())
        mileage_text.text = Html.fromHtml("&#8226; <b>Описание:</b> ${description} км", Html.FROM_HTML_MODE_COMPACT)
        mileage_text.setTextAppearance(R.style.Text_style)

        mileage_text.setPadding(30,20,30,0)
        mileage_text.gravity = Gravity.LEFT


        //статус аренды

        val cindition_text = TextView(requireContext())
        cindition_text.text = Html.fromHtml("&#8226; <b>Статус аренды - :</b> $order_status", Html.FROM_HTML_MODE_COMPACT)
        cindition_text.setTextAppearance(R.style.Text_style)

        cindition_text.setPadding(30,20,30,0)
        cindition_text.gravity = Gravity.LEFT

        //цена
        val formater = NumberFormat.getNumberInstance(Locale.getDefault())
        val formattedRentalPrice  = formater.format(total_price)

        val text_price = TextView(requireContext())
        text_price.text = Html.fromHtml("Цена за аренду - ${formattedRentalPrice} руб", Html.FROM_HTML_MODE_COMPACT)
        text_price.setTextAppearance(R.style.Title_style)

        text_price.setPadding(40,50,0,0)
        text_price.gravity = Gravity.LEFT


        val type_works_text = TextView(requireContext())
        type_works_text.text = Html.fromHtml("&#8226; <b>Тип работ - :</b> ${type_works}", Html.FROM_HTML_MODE_COMPACT)
        type_works_text.setTextAppearance(R.style.Text_style)

        type_works_text.setPadding(30,20,30,0)
        type_works_text.gravity = Gravity.LEFT

        val date_text = TextView(requireContext())
        date_text.text = Html.fromHtml("&#8226; <b>Дата аренды - :</b> ${rental_date}", Html.FROM_HTML_MODE_COMPACT)
        date_text.setTextAppearance(R.style.Text_style)

        date_text.setPadding(30,20,30,0)
        date_text.gravity = Gravity.LEFT

        val time_text = TextView(requireContext())
        time_text.text = Html.fromHtml("&#8226; <b>Время аренды - :</b> ${rental_time}", Html.FROM_HTML_MODE_COMPACT)
        time_text.setTextAppearance(R.style.Text_style)

        time_text.setPadding(30,20,30,0)
        time_text.gravity = Gravity.LEFT

        block.addView(image)
        block.addView(brand_text)


        block_inf.addView(mileage_text)
        block_inf.addView(cindition_text)
        block_inf.addView(type_works_text)
        block_inf.addView(date_text)
        block_inf.addView(time_text)

        block.addView(text_price)

        block.addView(block_inf)



        block.setOnClickListener {
            val sharedPreference = requireContext().getSharedPreferences("myPreferences", Context.MODE_PRIVATE)
            val editor = sharedPreference.edit()
            editor.remove("sp_eq_id")
            editor.remove("price")
            editor.putString("sp_eq_id", id.toString())
            editor.putString("price", rental_price.toString())
            editor.apply()

            Log.e("7666", "$id  $rental_price")
            val intent = Intent(requireContext(), MainActivity::class.java)
            startActivity(intent)
        }


        return block

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}