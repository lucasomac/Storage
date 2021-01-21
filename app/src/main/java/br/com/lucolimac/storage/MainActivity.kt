package br.com.lucolimac.storage

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.app.AlertDialog
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import br.com.lucolimac.storage.databinding.ActivityMainBinding
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.storage.FileDownloadTask
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import dmax.dialog.SpotsDialog
import java.io.File


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

//    fun upload() {
//
//        val file: Uri = Uri.fromFile(File("path/to/images/rivers.jpg"))
//        val riversRef: StorageReference = storageRef.child("images/rivers.jpg")
//
//        riversRef.putFile(file)
//            .addOnSuccessListener { taskSnapshot -> // Get a URL to the uploaded content
//                val downloadUrl: Uri = taskSnapshot.getDownloadUrl()
//            }
//            .addOnFailureListener {
//                // Handle unsuccessful uploads
//                // ...
//            }
//    }
//
//    fun download() {
//        val localFile = File.createTempFile("images", "jpg")
//        riversRef.getFile(localFile)
//            .addOnSuccessListener(OnSuccessListener<FileDownloadTask.TaskSnapshot?> {
//                // Successfully downloaded data to local file
//                // ...
//            }).addOnFailureListener(OnFailureListener {
//                // Handle failed download
//                // ...
//            })
//    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.floatingActionButton -> setIntent()
        }
    }

    fun config() {
        alertDialog = SpotsDialog.Builder().setContext(this).build()
        mStorageRef = FirebaseStorage.getInstance().getReference("prod_img")
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