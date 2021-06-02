package com.example.weather.view.view.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.weather.R
import com.example.weather.view.view.model.Weather

//адаптер для RecyclerView, в конструкторе (поле аргументов) создаем экземпляр интерфейса onItemClickListener
class MainFragmentAdapter(private var onItemViewClickListener: MainFragment.OnItemViewClickListener?) :
    RecyclerView.Adapter<MainFragmentAdapter.MainViewHolder>() {

    //создаем пустой список
    private var weatherData: List<Weather> = listOf()

    //метод получает на вход List<Weather>

    fun setWeather(data: List<Weather>) {
        //присваивает его значение переменной weatherData
        weatherData = data
        //и сообщает обсерверам что данные изменились
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MainViewHolder {
        return MainViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.fragment_main_recycler_item, parent, false)
                    as View
        )
    }

    //метод оnBindViewHolder нужен для отображения элемента на определенной позиции
    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        holder.bind(weatherData[position])
    }

    //этот метод обязателен к переопределению, в нем мы возвращаем кол-во элементов в RecyclerView
    override fun getItemCount(): Int {
        return weatherData.size
    }

    //метод присвоения лисенеру null чтобы не было утечек памяти, вызывается в onDestroy фрагмента
    fun removeListener() {
        onItemViewClickListener = null
    }

    //реализация ViewHolder внутри ViewHolderAdapter
    inner class MainViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        fun bind(weather: Weather) {
            //apply - Extension-функция, позволяет нагляднее отобразить код в случае присваивания значений
            itemView.apply {
                //Элементу списка присваивается имя согласно переданному Weather на определенной позиции
                //см. onBindViewHolder
                findViewById<TextView>(R.id.mainFragmentRecyclerItemTextView).text =
                    weather.city.name
                //Реализация действия при нажатии на элемент RecyclerView
                setOnClickListener {
                    //вызывается метод onItemViewClick который был определен в MainFragment
                    onItemViewClickListener?.onItemViewClick(weather)
                }
            }
        }
    }
}

