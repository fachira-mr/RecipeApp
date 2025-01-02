package com.example.recipeapp2

import android.content.ContentValues.TAG
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class CreateRecipeActivity : AppCompatActivity() {

    lateinit var NamaResep: EditText
    lateinit var Kategori: EditText
    lateinit var Deskripsi: EditText
    lateinit var Bahan: EditText
    lateinit var Cara: EditText
    lateinit var btnSaveRecipe: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_recipe)

        NamaResep = findViewById(R.id.etNamaResep)
        Kategori = findViewById(R.id.etKategori)
        Deskripsi = findViewById(R.id.etDeskripsi)
        Bahan = findViewById(R.id.etBahan)
        Cara = findViewById(R.id.etCara)
        btnSaveRecipe = findViewById(R.id.btnSaveRecipe)

        var editMode = false
        val id = intent.getStringExtra("id")
        if (!id.isNullOrBlank()) {
            editMode = true
            NamaResep.setText(intent.getStringExtra("title"))
            Kategori.setText(intent.getStringExtra("category"))
            Deskripsi.setText(intent.getStringExtra("description"))
            Bahan.setText(intent.getStringExtra("bahan"))
            Cara.setText(intent.getStringExtra("cara"))
        }

        btnSaveRecipe.setOnClickListener {
            if (NamaResep.text.isEmpty()) {
                showToast("Harap masukkan nama resep")
                return@setOnClickListener
            }

            if (Kategori.text.isEmpty()) {
                showToast("Harap masukkan kategori resep (Makanan/Minuman)")
                return@setOnClickListener
            }

            val validKategori = listOf("Makanan", "Minuman")
            if (!validKategori.contains(Kategori.text.toString())) {
                showToast("Kategori hanya boleh diisi dengan 'Makanan' atau 'Minuman'")
                return@setOnClickListener
            }

            if (Deskripsi.text.isEmpty()) {
                showToast("Harap isi deskripsi resep")
                return@setOnClickListener
            }

            if (Bahan.text.isEmpty()) {
                showToast("Harap masukkan bahan-bahan resep")
                return@setOnClickListener
            }

            if (Cara.text.isEmpty()) {
                showToast("Harap masukkan cara-cara membuat resep")
                return@setOnClickListener
            }

            val foodItem = FoodItem(
                id = if (editMode) id else null,
                title = NamaResep.text.toString(),
                category = Kategori.text.toString(),
                description = Deskripsi.text.toString(),
                bahan = Bahan.text.toString(),
                cara = Cara.text.toString()
            )

            if (editMode) {
                update(foodItem)
            } else {
                create(foodItem)
            }
        }

        if (editMode) {
            btnSaveRecipe.text = "Ubah"
        }
    }

    private fun update(foodItem: FoodItem) {
        val db = Firebase.firestore
        val sharedPreferences = getSharedPreferences("app_preference", Context.MODE_PRIVATE)
        val uid = sharedPreferences.getString("id", null)

        if (uid == null) {
            showToast("User tidak terautentikasi. Silakan login kembali.")
            finish()
            return
        }

        val data = hashMapOf(
            "title" to foodItem.title,
            "category" to foodItem.category,
            "description" to foodItem.description,
            "bahan" to foodItem.bahan,
            "cara" to foodItem.cara,
            "uid" to uid
        )

        db.collection("recipe").document(foodItem.id!!).set(data)
            .addOnSuccessListener {
                showToast("Berhasil merubah Resep!")
                finish()
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error updating document", e)
            }
    }

    private fun create(foodItem: FoodItem) {
        val db = Firebase.firestore
        val sharedPreferences = getSharedPreferences("app_preference", Context.MODE_PRIVATE)
        val uid = sharedPreferences.getString("id", null)

        if (uid == null) {
            showToast("User tidak terautentikasi. Silakan login kembali.")
            finish()
            return
        }

        val data = hashMapOf(
            "title" to foodItem.title,
            "category" to foodItem.category,
            "description" to foodItem.description,
            "bahan" to foodItem.bahan,
            "cara" to foodItem.cara,
            "uid" to uid
        )

        db.collection("recipe").add(data)
            .addOnSuccessListener {
                showToast("Berhasil menambahkan Resep!")
                finish()
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
            }
    }

    private fun showToast(message: String) {
        Toast.makeText(applicationContext, message, Toast.LENGTH_LONG).show()
    }
}
