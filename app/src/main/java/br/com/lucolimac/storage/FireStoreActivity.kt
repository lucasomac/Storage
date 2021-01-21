package br.com.lucolimac.storage

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import br.com.lucolimac.storage.databinding.ActivityFireStoreBinding
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore


class FireStoreActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFireStoreBinding
    private lateinit var db: FirebaseFirestore
    private lateinit var cr: CollectionReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFireStoreBinding.inflate(layoutInflater)
        setContentView(binding.root)
        config()
        binding.btnSave.setOnClickListener {
            val prod = getData()
            sendProd(prod)
        }
        binding.btnUpdate.setOnClickListener {
            var prod = getData()
            updateProd(prod)
        }
        binding.btnUpdate.setOnClickListener {
            deleteProd()
        }
        readProd()
    }

    fun config() {
        db = FirebaseFirestore.getInstance()
        cr = db.collection("produtos")
    }

    fun getData(): MutableMap<String, Any> {
        val prod: MutableMap<String, Any> = HashMap()

        prod["nome"] = binding.edNomeProd.text.toString()
        prod["qtd"] = binding.edQtdProd.text.toString()
        prod["preco"] = binding.edPrecoProd.text.toString()
        sendProd(prod)
        return prod
    }

    fun updateProd(prod: MutableMap<String, Any>) {
        cr.document("Café ").update(prod)
    }

    fun deleteProd() {
        cr.document("Café ").delete().addOnSuccessListener {

        }
    }

    private fun sendProd(prod: MutableMap<String, Any>) {
        val nome = binding.edNomeProd.text.toString()
        cr.document(nome).set(prod).addOnSuccessListener {
//            Log.i("STORE", it.toString())
        }.addOnFailureListener {
//            Log.i("STORE", it.toString())
        }
    }

    fun readProd() {
        cr.get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    for (document in task.result!!) {
                        Log.d("TAG", document.id + " => " + document.data)
                    }
                } else {
                    Log.w("TA G", "Error getting documents.", task.exception)
                }
            }
    }
}