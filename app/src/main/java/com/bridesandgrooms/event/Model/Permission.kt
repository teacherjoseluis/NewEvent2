package com.bridesandgrooms.event.Model

enum class Permission(
    val code: String,
    val name_en: String,
    val name_es: String,
    val permission_type_en: String,
    val permission_type_es: String,
    val info_type_en: String,
    val info_type_es: String,

    val drawable: String,
    var permission_wording_en: String,
    var permission_wording_es: String
) {
    Contact(
        "contact",
        "contacts",
        "contactos",
        "Contact List",
        "Lista de Contactos",
        "name, phone number and email",
        "nombre, numero de telefono y correo electronico",
        "icons8_contacts_80",
        "",
        ""
    ),
    Calendar(
        "calendar",
        "calendar",
        "calendario",
        "Calendar",
        "Calendario",
        "calendar and events created by this app",
        "calendario y eventos creados por esta app",
        "icons8_calendar_96",
        "",
        ""
    ),
    Storage(
        "storage",
        "internal data",
        "datos internos",
        "Internal Storage",
        "Almacenamiento Interno",
        "internal storage for data created by this app",
        "almacenamiento interno para datos creados por esta app",
        "icons8_hdd_100",
        "",
        ""
    ),
    Location(
        "location",
        "location",
        "ubicacion",
        "Location",
        "Ubicacion",
        "user approximate location",
        "ubicacion aproximada del usuario",
        "baseline_location_on_black_36",
        "",
        ""
    );

    companion object {
        fun getPermission(code: String): Permission {
            val permission: Permission = when (code) {
                "contact" -> Contact
                "calendar" -> Calendar
                "storage" -> Storage
                "location" -> Location
                else -> Contact
            }
            permission.permission_wording_en =
                "Providing Brides & Grooms access to your ${permission.permission_type_en} allows it to see information related to your ${permission.name_en} such as ${permission.info_type_en}.\n This is necessary in order to continue.\n\nYou'd need to grant access to your ${permission.name_en}  in Settings"
            permission.permission_wording_es =
                "Proveer acceso para Brides & Grooms a tu ${permission.permission_type_es} le permite ver informacion relacionada con tu ${permission.name_es} tal como ${permission.info_type_es}.\n Esto es necesario para poder continuar.\n\nNecesitas proveer acceso a tus ${permission.name_es}  en la Configuracion de tu dispositivo"
            return permission
        }
    }
}

