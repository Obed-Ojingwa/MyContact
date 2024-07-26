package com.obedcodes.managemycontact

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SyncContactsActivity : AppCompatActivity() {
    private val apiService = RetrofitClient.instance.create(GooglePeopleApiService::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sync_contacts)

        val contactsToSync = intent.getParcelableArrayListExtra<Contact>("contacts_to_sync")
        contactsToSync?.let {
            syncContactsToGoogle(it)
        }
    }

    private fun syncContactsToGoogle(contacts: List<Contact>) {
        val account = GoogleSignIn.getLastSignedInAccount(this)
        val authToken = "Bearer ${account?.idToken}"

        for (contact in contacts) {
            val googleContact = GoogleContact(
                names = listOf(GoogleContact.Name(contact.name)),
                phoneNumbers = listOf(GoogleContact.PhoneNumber(contact.phone))
            )

            apiService.createContact(authToken, googleContact).enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        Log.d(TAG, "Contact synced: ${contact.name}")
                    } else {
                        Log.e(TAG, "Failed to sync contact: ${contact.name}")
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Log.e(TAG, "Error syncing contact: ${t.message}")
                }
            })
        }
    }

    companion object {
        const val TAG = "SyncContactsActivity"
    }
}
