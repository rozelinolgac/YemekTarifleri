package com.rozelinolgac.yemektarifleri

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.navigation.Navigation

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Toast.makeText(this,":)",Toast.LENGTH_LONG).show()

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {    //Burada xml dosyasını bağladık..
       val MenuInflater=menuInflater
        MenuInflater.inflate(R.menu.yemek_ekle,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId==R.id.menuId){
            val action=ListeDirections.actionListeToTarifler("menudengeldim",0)
            Navigation.findNavController(this,R.id.fragment2).navigate(action)

        }
        return super.onOptionsItemSelected(item)
    }




}

