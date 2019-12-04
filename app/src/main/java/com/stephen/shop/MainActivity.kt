package com.stephen.shop

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.auth.AuthMethodPickerLayout
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.stephen.shop.model.Category
import com.stephen.shop.model.Item
import com.stephen.shop.view.ItemHolder
import com.stephen.shop.view.ItemViewModel

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import java.util.*

class MainActivity : AppCompatActivity(), FirebaseAuth.AuthStateListener {

    private val SI_REQUEST_CODE = 100
    private val TAG = MainActivity::class.java.simpleName
//    private lateinit var adapter: FirestoreRecyclerAdapter<Item, ItemHolder>
    var categories = mutableListOf<Category>()
    private lateinit var adapter: ItemAdapter
    lateinit var itemViewModel: ItemViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }

        verify_email.setOnClickListener {
            FirebaseAuth.getInstance().currentUser?.sendEmailVerification()?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Snackbar.make(it, "Verify email sent", Snackbar.LENGTH_LONG).show()
                }
            }
        }
        FirebaseFirestore.getInstance().collection("categories")
            .get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    task.result?.let {
                        categories.add(Category("", "不分類"))
                        for (doc in it) {
                            categories.add(
                                Category(
                                    doc.id,
                                    doc.data.get("name").toString()
                                )
                            )
                        }
                        spinner.adapter = ArrayAdapter<Category>(this@MainActivity,
                            android.R.layout.simple_spinner_item, categories)
                            .apply {
                                setDropDownViewResource(android.R.layout.simple_dropdown_item_1line)
                            }
                        spinner.setSelection(0, false)
                        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                            override fun onNothingSelected(parent: AdapterView<*>?) {

                            }

                            override fun onItemSelected(parent: AdapterView<*>?,
                                view: View?, position: Int, id: Long) {
//                                setupAdapter()
                                itemViewModel.setCategory(categories.get(position).id)
                            }

                        }
                    }
                }
            }

        //setupRecyclerView
        recycler.setHasFixedSize(true)
        recycler.layoutManager = LinearLayoutManager(this)
        adapter = ItemAdapter(mutableListOf<Item>())
        recycler.adapter = adapter
        itemViewModel = ViewModelProviders.of(this).get(ItemViewModel::class.java)
        itemViewModel.getItems().observe(this, androidx.lifecycle.Observer {
            Log.d(TAG, "observe: ${it.size}")
            adapter.items = it
            adapter.notifyDataSetChanged()
        })
//        setupAdapter()
    }

    inner class ItemAdapter(var items: List<Item>) : RecyclerView.Adapter<ItemHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
            return ItemHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_row, parent, false)
            )
        }

        override fun getItemCount(): Int {
            return items.size
        }

        override fun onBindViewHolder(holder: ItemHolder, position: Int) {
            holder.bindTo(items.get(position))
            holder.itemView.setOnClickListener {
                itemClicked(items.get(position), position)
            }
        }

    }

    /*private fun setupAdapter() {
        val selected = spinner.selectedItemPosition
        var query = if (selected >0) {
            adapter.stopListening()
            FirebaseFirestore.getInstance()
                .collection("items")
                .whereEqualTo("category", categories.get(selected).id)
                .orderBy("viewCount", Query.Direction.DESCENDING)
                .limit(10)
        } else {
            FirebaseFirestore.getInstance()
                .collection("items")
                .orderBy("viewCount", Query.Direction.DESCENDING)
                .limit(10)
        }
        val options = FirestoreRecyclerOptions.Builder<Item>()
            .setQuery(query, Item::class.java)
            .build()
        adapter = object : FirestoreRecyclerAdapter<Item, ItemHolder>(options) {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
                return ItemHolder(
                    LayoutInflater.from(parent.context).inflate(
                        R.layout.item_row,
                        parent,
                        false
                    )
                )
            }

            override fun onBindViewHolder(holder: ItemHolder, position: Int, model: Item) {
                model.id = snapshots.getSnapshot(position).id
                holder.bindTo(model)
                holder.itemView.setOnClickListener {
                    itemClicked(model, position)
                }
            }
        }

        recycler.adapter = adapter
        adapter.startListening()
    }*/

    private fun itemClicked(model: Item, position: Int) {
        Log.d(TAG, "itemClicked : ${model.title} / $position");
        val intent = Intent(this, DetailActivity::class.java)
        intent.putExtra("ITEM", model)
        startActivity(intent)
    }

    override fun onAuthStateChanged(auth: FirebaseAuth) {
        val user = FirebaseAuth.getInstance().currentUser
        if ( user != null) {
            user_info.setText("Email: ${user.email} /  ${user.isEmailVerified}")
            verify_email.visibility = if (user.isEmailVerified) View.GONE else View.VISIBLE
        } else {
            user_info.setText("Not login")
            verify_email.visibility = View.GONE
        }
    }

    override fun onStart() {
        super.onStart()
        FirebaseAuth.getInstance().addAuthStateListener(this)
//        adapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        FirebaseAuth.getInstance().removeAuthStateListener(this)
//        adapter.stopListening()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            R.id.action_signin -> {
                val whiteList = listOf<String>("tw", "hk", "cn", "au")
                val myLayout = AuthMethodPickerLayout.Builder(R.layout.sign_up)
                    .setEmailButtonId(R.id.signup_email)
                    .setGoogleButtonId(R.id.signup_google)
                    .setPhoneButtonId(R.id.signup_sms)
                    .setFacebookButtonId(R.id.signup_facebook)
                    .build()
                startActivityForResult(AuthUI.getInstance()
                    .createSignInIntentBuilder()
                    .setAvailableProviders(Arrays.asList(
                        AuthUI.IdpConfig.EmailBuilder().build(),
                        AuthUI.IdpConfig.GoogleBuilder().build(),
                        AuthUI.IdpConfig.FacebookBuilder().build(),
                        AuthUI.IdpConfig.PhoneBuilder()
                            .setWhitelistedCountries(whiteList)
                            .setDefaultCountryIso("tw")
                            .build()
                    ))
                    .setIsSmartLockEnabled(false)
                    .setLogo(R.drawable.shop)
                    .setTheme(R.style.SignUp)
                    .setAuthMethodPickerLayout(myLayout)
                    .build(),
                    SI_REQUEST_CODE)
//                startActivityForResult(Intent(this, SignInActivity::class.java), SI_REQUEST_CODE)
                true
            }
            R.id.action_signout -> {
                FirebaseAuth.getInstance().signOut()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
