package com.obedcodes.managemycontact

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.ContactsContract
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.widget.Button
import android.widget.Toast

class MainActivity : AppCompatActivity() {

    private lateinit var contactAdapter: ContactAdapter
    private val selectedContacts = mutableListOf<Contact>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_CONTACTS), 1)
        } else {
            loadContacts()
        }

        // Initialize the adapter and RecyclerView
        contactAdapter = ContactAdapter(emptyList()) { contact, isSelected ->
            if (isSelected) {
                selectedContacts.add(contact)
            } else {
                selectedContacts.remove(contact)
            }
        }

        val recyclerView: RecyclerView = findViewById(R.id.contacts_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = contactAdapter

        // Set up sync button click listener
        findViewById<Button>(R.id.sync_button).setOnClickListener {
            val contactsToSync = contactAdapter.getSelectedContacts()
            if (contactsToSync.isNotEmpty()) {
                val intent = Intent(this, SyncContactsActivity::class.java)
                intent.putParcelableArrayListExtra("contacts_to_sync", ArrayList(contactsToSync))
                startActivity(intent)
            } else {
                Toast.makeText(this, "No contacts selected to sync", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            loadContacts()
        }
    }

    private fun loadContacts() {
        val contactList = mutableListOf<Contact>()
        val cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null)

        cursor?.use {
            if (it.count > 0) {
                while (it.moveToNext()) {
                    val id = it.getString(it.getColumnIndex(ContactsContract.Contacts._ID))
                    val name = it.getString(it.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))

                    val phoneCursor = contentResolver.query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                        arrayOf(id),
                        null
                    )

                    phoneCursor?.use { pc ->
                        while (pc.moveToNext()) {
                            val phone = pc.getString(pc.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                            contactList.add(Contact(name, phone))
                        }
                    }
                }
            }
        }

        contactAdapter = ContactAdapter(contactList) { contact, isSelected ->
            if (isSelected) {
                selectedContacts.add(contact)
            } else {
                selectedContacts.remove(contact)
            }
        }

        val recyclerView: RecyclerView = findViewById(R.id.contacts_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = contactAdapter
    }
}
