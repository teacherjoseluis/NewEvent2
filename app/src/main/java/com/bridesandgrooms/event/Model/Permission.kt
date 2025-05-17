package com.bridesandgrooms.event.Model

enum class Permission(
    val code: String,

    val name_en: String,
    val name_es: String,
    val name_fr: String,
    val name_pt: String,

    val permission_type_en: String,
    val permission_type_es: String,
    val permission_type_fr: String,
    val permission_type_pt: String,

    val info_type_en: String,
    val info_type_es: String,
    val info_type_fr: String,
    val info_type_pt: String,

    val drawable: String,

    var permission_wording_en: String,
    var permission_wording_es: String,
    var permission_wording_fr: String,
    var permission_wording_pt: String
) {
    Contact(
        "contact",
        "contacts", "contactos", "contacts", "contatos",
        "Contact List", "Lista de Contactos", "Liste de contacts", "Lista de contatos",
        "name, phone number and email", "nombre, numero de telefono y correo electronico", "nom, numéro de téléphone et e-mail", "nome, número de telefone e e-mail",
        "icons8_contacts_80",
        "", "", "", ""
    ),
    Calendar(
        "calendar",
        "calendar", "calendario", "calendrier", "calendário",
        "Calendar", "Calendario", "Calendrier", "Calendário",
        "calendar and events created by this app", "calendario y eventos creados por esta app", "calendrier et événements créés par cette application", "calendário e eventos criados por este aplicativo",
        "icons8_calendar_96",
        "", "", "", ""
    ),
    Storage(
        "storage",
        "internal data", "datos internos", "données internes", "dados internos",
        "Internal Storage", "Almacenamiento Interno", "Stockage interne", "Armazenamento interno",
        "internal storage for data created by this app", "almacenamiento interno para datos creados por esta app", "stockage interne des données créées par cette application", "armazenamento interno para dados criados por este aplicativo",
        "icons8_hdd_100",
        "", "", "", ""
    ),
    Location(
        "location",
        "location", "ubicacion", "localisation", "localização",
        "Location", "Ubicacion", "Localisation", "Localização",
        "user approximate location", "ubicacion aproximada del usuario", "localisation approximative de l'utilisateur", "localização aproximada do usuário",
        "baseline_location_on_24",
        "", "", "", ""
    );

    companion object {
        fun getPermission(code: String): Permission {
            val permission = when (code) {
                "calendar" -> Calendar
                "storage" -> Storage
                "location" -> Location
                else -> Contact
            }

            permission.permission_wording_en =
                "Providing Brides & Grooms access to your ${permission.permission_type_en} allows it to see information related to your ${permission.name_en} such as ${permission.info_type_en}.\nThis is necessary in order to continue.\n\nYou need to grant access to your ${permission.name_en} in Settings."

            permission.permission_wording_es =
                "Proveer acceso para Brides & Grooms a tu ${permission.permission_type_es} le permite ver información relacionada con tu ${permission.name_es} tal como ${permission.info_type_es}.\nEsto es necesario para poder continuar.\n\nNecesitas proveer acceso a tus ${permission.name_es} en la Configuración de tu dispositivo."

            permission.permission_wording_fr =
                "Accorder à Brides & Grooms l'accès à votre ${permission.permission_type_fr} lui permet de voir des informations relatives à vos ${permission.name_fr} telles que ${permission.info_type_fr}.\nCeci est nécessaire pour continuer.\n\nVous devez accorder l'accès à vos ${permission.name_fr} dans les paramètres."

            permission.permission_wording_pt =
                "Conceder acesso ao Brides & Grooms ao seu ${permission.permission_type_pt} permite que ele veja informações relacionadas aos seus ${permission.name_pt}, como ${permission.info_type_pt}.\nIsso é necessário para continuar.\n\nVocê precisa conceder acesso aos seus ${permission.name_pt} nas Configurações."

            return permission
        }
    }
}
