package com.rozelinolgac.yemektarifleri

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.contentValuesOf
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.recycler_row.view.*

class ListeRecyclerAdapter(val yemekIsmi:ArrayList<String>,val yemekId:ArrayList<Int>): RecyclerView.Adapter<yemektarifleriHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): yemektarifleriHolder {   //burada xml dosyasını view'e dönüştürüyoruz
        val layoutInflater=LayoutInflater.from(parent.context)
        val view= layoutInflater.inflate(R.layout.recycler_row,parent,false)
        return yemektarifleriHolder(view)

    }

    override fun onBindViewHolder(holder: yemektarifleriHolder, position: Int) {
                holder.itemView.Text_yemekId.text=yemekIsmi[position]
                holder.itemView.setOnClickListener{
                    val action=ListeDirections.actionListeToTarifler("recyclerdengeldim",yemekId[position])
                    Navigation.findNavController(it).navigate(action)
                }
    }

    override fun getItemCount(): Int {
      return yemekIsmi.size
    }


}





class yemektarifleriHolder(val view:View) : RecyclerView.ViewHolder(view){

}