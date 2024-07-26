package com.obedcodes.managemycontact

data class GoogleContact(
    val names: List<Name>,
    val phoneNumbers: List<PhoneNumber>
) {
    data class Name(val displayName: String)
    data class PhoneNumber(val value: String)
}
