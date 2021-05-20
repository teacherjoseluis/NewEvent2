package Application

enum class CacheCategory(
    val cachename: String,
    val expiration: Int,
    val entity: String,
    val classtype: String
) {
    TaskJournal("taskjournal", 60,"Task", "TaskJournal.Class"),
    SingleTask("singletask", 60,"Task", "Task.Class"),
    ArrayTask("arrayalltask", 60,"Task", "Task.Class"),
}