package com.rozelinolgac.yemektarifleri

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import androidx.core.content.ContextCompat
import androidx.navigation.Navigation
import kotlinx.android.synthetic.main.fragment_tarifler.*
import java.io.ByteArrayOutputStream

import kotlin.Exception


class Tarifler : Fragment() {
    var secilenGorsel: Uri? =null
    var secilenbitmap: Bitmap? =null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_tarifler, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        button.setOnClickListener {
            kaydet(it)

        }


        imageView.setOnClickListener {
            gorselSec(it)

        }

        imageDelete.setOnClickListener{
            delete(it)
        }
         
        arguments?.let {
            var gelenBilgi=TariflerArgs.fromBundle(it).bilgi
            if(gelenBilgi.equals("menudengeldim")){
                yemekIsmiId.setText("")
                tarifId.setText("")
                button.visibility=View.VISIBLE
                val gorselSecmeArkaPlani=BitmapFactory.decodeResource(context?.resources,R.drawable.yemekekle)
                imageView.setImageBitmap(gorselSecmeArkaPlani)
                imageDelete.visibility=View.INVISIBLE
            }
            else{
                imageView.visibility=View.VISIBLE
                button.visibility=View.INVISIBLE
                val gelenId=TariflerArgs.fromBundle(it).id


                context?.let {
                   try{
                       val db=it.openOrCreateDatabase("Yemekler",Context.MODE_PRIVATE,null)
                       val cursor=db.rawQuery("SELECT * FROM yemekler WHERE id=? ", arrayOf(gelenId.toString()))


                       val yemekIsmiIndex =cursor.getColumnIndex("yemekismi")
                       val yemekMalzemeIndex=cursor.getColumnIndex("yemektarifi")
                       val yemekGorseli = cursor.getColumnIndex("gorsel")


                       while(cursor.moveToNext()){
                           yemekIsmiId.setText(cursor.getString(yemekIsmiIndex))
                           tarifId.setText(cursor.getString(yemekMalzemeIndex))

                           //sqlde gorseli alırken byte dizisi olarak alıyoruz daha sonra onu bitmape çeviriyoruz

                           val byteDizisi=cursor.getBlob(yemekGorseli)
                           val bitmap=BitmapFactory.decodeByteArray(byteDizisi,0,byteDizisi.size)

                           imageView.setImageBitmap(bitmap)


                       }
                       cursor.close()
                                         }

                   catch (e:Exception){
                       e.printStackTrace()
                   }
               }


            }

        }
    }
              fun delete(view: View){
        arguments?.let {
            val gelenId=TariflerArgs.fromBundle(it).id

            context?.let {
                val db=it.openOrCreateDatabase("Yemekler",Context.MODE_PRIVATE,null)
             val sqlString="DELETE FROM  yemekler WHERE id=?"
                val statement =db.compileStatement(sqlString)
                statement.bindString(1,gelenId.toString())
                statement.execute()


              //  db.execSQL("DELETE FROM yemekler WHERE id=?",arrayOf(gelenId.toString()))  ... Alternatif

            }

            val action=TariflerDirections.actionTariflerToListe()
            Navigation.findNavController(view).navigate(action)
        }                
              }
    
    
        fun kaydet(view: View) {
        //bu kısım sql
        val yemekIsmi=yemekIsmiId.text.toString()
        val yemekMalzemeleri=tarifId.text.toString()

        if(secilenbitmap!=null){
            val kucukBitmap=kucukBitmapOlustur(secilenbitmap!!,300)
            val outputStream=ByteArrayOutputStream()
            kucukBitmap.compress(Bitmap.CompressFormat.PNG,50,outputStream)
            val byteDizisi = outputStream.toByteArray()

            try { //contextin boş gelme ihtimali var
                context?.let {  
                val database=it.openOrCreateDatabase("Yemekler", Context.MODE_PRIVATE,null)
                database.execSQL("CREATE TABLE IF NOT EXISTS yemekler (id INTEGER PRIMARY KEY, yemekismi VARCHAR, yemektarifi VARCHAR, gorsel BLOB) ")

                val sqlString="INSERT INTO yemekler (yemekismi,yemektarifi,gorsel) VALUES (?,?,?)"
                val statement =database.compileStatement(sqlString)
                statement.bindString(1,yemekIsmi)
                statement.bindString(2,yemekMalzemeleri)
                statement.bindBlob(3,byteDizisi)
                statement.execute()
                  }

            }
            catch (e:Exception){
                e.printStackTrace()
            }
        }

          val action=TariflerDirections.actionTariflerToListe()
          Navigation.findNavController(view).navigate(action)


    }

        fun gorselSec(view: View) {   //bu kısım ise izin kısmı
        activity?.let {
            if (ContextCompat.checkSelfPermission(it.applicationContext, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
            ) //izin alındı'ya esit degilse
            {
                requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1)   //bu kodla iznimizi aldık
            }

            else {
                val galeriIntent =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(galeriIntent, 2)
            }
        }
    }

    override fun onRequestPermissionsResult(          //Izin verilirse ne yapacağız?
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray             )

    {
        if (requestCode==1){
            if(grantResults.size>0 && grantResults[0]== PackageManager.PERMISSION_GRANTED) //bunlar sağlandıysa ne yapacağını söylüyoruz
            {
                val galeriIntent= Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(galeriIntent,2)

            }

        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    //yukarıda ki kodda izin alınacağı zaman ne yapacağımızı yazdık(galeriye git dedik)
    //şimdi ise galeriye gidince ne yapacağımızı söylüyoruz

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if(requestCode==2 && resultCode== Activity.RESULT_OK && data!=null) {   //burada data gorselin yeri
            secilenGorsel = data.data

             try{  //try-catch yapmanın nedeni:dosyaya çevirirken hata olabilir..görsel silinmiş ya da sd kart bozulmuş olabilir...
                context?.let {
                    if(secilenGorsel!=null){
                        if(Build.VERSION.SDK_INT>=28){
                            val source= ImageDecoder.createSource(it.contentResolver,secilenGorsel!!)
                            secilenbitmap= ImageDecoder.decodeBitmap(source)
                            imageView.setImageBitmap(secilenbitmap)
                        }   //buraya kadar api=>28 olan tellerde image alırken önce bir kaynak dosyasına çevirdik daha sonrada bitmape çevirdik

                        else{        //şimdi ise api<28 olan tellerde direkt kaynak kod oluşturmadan image'ı bitmape çevirip image'ı appe ekliyoruz
                            secilenbitmap=
                                MediaStore.Images.Media.getBitmap(it.contentResolver,secilenGorsel)
                            imageView.setImageBitmap(secilenbitmap)
                        }


                    }
                }
           }
            catch (e: Exception){
                e.printStackTrace()
            }
        }
        
       }

    fun kucukBitmapOlustur(kullanicininYolladigiBitmap:Bitmap,maximumBoyut:Int):Bitmap{
        var width=kullanicininYolladigiBitmap.width
        var height=kullanicininYolladigiBitmap.height

        val bitmapOrani:Double=width.toDouble()/height.toDouble()

        if(bitmapOrani>1){  //gorselimiz yatay
            width=maximumBoyut
            var kisaltilmisHeight=maximumBoyut/bitmapOrani
            height=kisaltilmisHeight.toInt()   //bizden int olarak isteniyor

        }

        else {  //gorsel dikey
            height=maximumBoyut
            var kisaltilmisWidth=maximumBoyut*bitmapOrani
            width=kisaltilmisWidth.toInt()

        }
    return Bitmap.createScaledBitmap(kullanicininYolladigiBitmap,width,height,true)
       }
}
