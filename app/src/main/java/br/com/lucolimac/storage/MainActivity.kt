package br.com.lucolimac.storage

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import br.com.lucolimac.storage.databinding.ActivityMainBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import dmax.dialog.SpotsDialog
import java.util.*


class MainActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivityMainBinding
    private lateinit var mStorageRef: StorageReference

    private lateinit var alertDialog: AlertDialog
    private val CODE_IMAGE = 1000
    override fun onCreate(savedInstanceState: Bundle?) {
        mStorageRef = FirebaseStorage.getInstance().getReference()
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        config()
        binding.floatingActionButton.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.floatingActionButton -> setIntent()
        }
    }

    fun config() {
        alertDialog = SpotsDialog.Builder().setContext(this).build()
        mStorageRef = FirebaseStorage.getInstance().getReference(Date().toString())
    }

    //Configura a imagem para
    fun setIntent() {
        val intent = Intent()
        intent.type = "image/"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Captura Imagem"), CODE_IMAGE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CODE_IMAGE) {
            alertDialog.show()
            val uploadTask = mStorageRef.putFile(data!!.data!!)
            uploadTask.continueWithTask { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Chegando", Toast.LENGTH_LONG).show()
                }
                mStorageRef!!.downloadUrl
            }.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val downloadUri = task.result
                    val url = downloadUri!!.toString()
                        .substring(0, downloadUri.toString().indexOf("&token"))
                    Log.i("URI", url)
                    alertDialog.dismiss()
                    Picasso.get().load(url).into(binding.imageView)
                }

            }
        }
    }
}