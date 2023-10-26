package com.example.firebasefirestorecrudapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.firebasefirestorecrudapp.databinding.ActivityMainBinding
import com.google.firebase.firestore.FirebaseFirestore
import java.sql.Timestamp

class MainActivity : AppCompatActivity() {
    private val db = FirebaseFirestore.getInstance()
    private val dataCollection = db.collection("data")
    private val data = mutableListOf<Data>()
    private lateinit var adapter: DataAdapter
    private lateinit var binding : ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        binding=ActivityMainBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        adapter = DataAdapter(data, this)
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(this)

        binding.addBtn.setOnClickListener {
            val title = binding.titleEtxt.text.toString()
            val description = binding.descEtxt.text.toString()

            if (title.isNotEmpty() && description.isNotEmpty()) {
                addData(title, description)
            }
        }
        fetchData()

    }

    private fun fetchData() {
        dataCollection
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener {
                data.clear()
                for(document in it){
                    val item = document.toObject(Data::class.java)
                    item.id = document.id
                    data.add(item)
                }
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Data fetched failed", Toast.LENGTH_SHORT).show()
            }
    }

    private fun addData(name: String, email: String, sub: String, birthday: Int) {
        val newData = Data(name = name, email = email, sub = sub, birthday = birthday,timestamp = Timestamp.now())
        dataCollection.add(newData)
            .addOnSuccessListener {
                newData.id = it.id
                data.add(newData)
                adapter.notifyDataSetChanged()
                binding.titleEtxt.text?.clear()
                binding.descEtxt.text?.clear()
                fetchData()
                Toast.makeText(this, "Data added successfully", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Data added failed", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onEditItemClick(data: Data) {
        binding.titleEtxt.setText(data.title)
        binding.descEtxt.setText(data.description)
        binding.addBtn.text = "Update"

        binding.addBtn.setOnClickListener {
            val updateTitle = binding.titleEtxt.text.toString()
            val updateDescription = binding.descEtxt.text.toString()

            if(updateTitle.isNotEmpty() && updateDescription.isNotEmpty()){
                val updateData = Data(data.id, updateTitle, updateDescription, Timestamp.now())

                dataCollection.document(data.id!!)
                    .set(updateData)
                    .addOnSuccessListener {
                        binding.titleEtxt.text?.clear()
                        binding.descEtxt.text?.clear()
                        Toast.makeText(this, "Data Updated", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Data updated failed", Toast.LENGTH_SHORT).show()
                    }
            }
        }
    }

    override fun onDeleteItemClick(data: Data) {
        dataCollection.document(data.id!!)
            .delete()
            .addOnSuccessListener {
                adapter.notifyDataSetChanged()
                fetchData()
                Toast.makeText(this, "Data deleted", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Data deletion failed", Toast.LENGTH_SHORT).show()
            }
    }


}