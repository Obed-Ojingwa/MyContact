package com.obedcodes.managemycontact

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ContactAdapter(private val contacts: List<Contact>, private val onContactSelected: (Contact, Boolean) -> Unit) :
    RecyclerView.Adapter<ContactAdapter.ContactViewHolder>() {

    private val selectedContacts = mutableSetOf<Contact>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_contact, parent, false)
        return ContactViewHolder(view)
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        val contact = contacts[position]
        holder.bind(contact)

        holder.checkBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                selectedContacts.add(contact)
            } else {
                selectedContacts.remove(contact)
            }
            onContactSelected(contact, isChecked)
        }
    }

    override fun getItemCount(): Int {
        return contacts.size
    }

    fun getSelectedContacts(): List<Contact> {
        return selectedContacts.toList()
    }

    class ContactViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.contact_name)
        val phoneTextView: TextView = itemView.findViewById(R.id.contact_phone)
        val checkBox: CheckBox = itemView.findViewById(R.id.contact_checkbox)

        fun bind(contact: Contact) {
            nameTextView.text = contact.name
            phoneTextView.text = contact.phone
        }
    }
}
