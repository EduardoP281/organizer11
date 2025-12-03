package com.example.organizer11.data.repository

import com.example.organizer11.data.database.ReminderDao
import com.example.organizer11.data.model.Reminder
import com.google.firebase.auth.FirebaseAuth // <-- Importante
import kotlinx.coroutines.flow.Flow

class ReminderRepository(private val reminderDao: ReminderDao) {

    // Helper para obtener el ID actual de Firebase
    // Si no hay usuario (ej. logout), devuelve una cadena vacía para que no muestre datos de otros
    private val currentUserId: String
        get() = FirebaseAuth.getInstance().currentUser?.uid ?: ""

<<<<<<< Updated upstream
    // --- LIVEDATA / FLOWS ---

    // Ahora pasamos el 'currentUserId' al DAO
    val allReminders: Flow<List<Reminder>>
        get() = reminderDao.getAllReminders(currentUserId)

    val starredReminders: Flow<List<Reminder>>
        get() = reminderDao.getStarredReminders(currentUserId)

    // --- FUNCIONES SUSPENDIDAS ---
=======
    private fun getCollection() =
        db.collection("users").document(currentUserId).collection("reminders")

    // --- LEER TODOS ---
    val allReminders: Flow<List<Reminder>> = callbackFlow {
        if (currentUserId.isEmpty()) {
            trySend(emptyList())
            awaitClose { }
            return@callbackFlow
        }

        val subscription = getCollection()
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("Repo", "Error", error)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val reminders = snapshot.toObjects(Reminder::class.java)
                    trySend(reminders)
                }
            }
        awaitClose { subscription.remove() }
    }

    // --- LEER DESTACADOS ---
    val starredReminders: Flow<List<Reminder>> = callbackFlow {
        if (currentUserId.isEmpty()) {
            trySend(emptyList())
            awaitClose { }
            return@callbackFlow
        }

        // ▼▼▼ CORRECCIÓN AQUÍ ▼▼▼
        val subscription = getCollection()
            .whereEqualTo("isStarred", true) // <-- DEBE DECIR "isStarred"
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null) {
                    val reminders = snapshot.toObjects(Reminder::class.java)
                    trySend(reminders)
                }
            }
        awaitClose { subscription.remove() }
    }
>>>>>>> Stashed changes

    suspend fun insert(reminder: Reminder) {
<<<<<<< Updated upstream
        reminderDao.insertReminder(reminder)
=======
        if (currentUserId.isEmpty()) return
        val docRef = getCollection().document()
        val reminderWithId = reminder.copy(id = docRef.id)
        docRef.set(reminderWithId).await()
>>>>>>> Stashed changes
    }

    suspend fun update(reminder: Reminder) {
<<<<<<< Updated upstream
        reminderDao.updateReminder(reminder)
=======
        if (currentUserId.isEmpty()) return
        getCollection().document(reminder.id).set(reminder).await()
>>>>>>> Stashed changes
    }

    suspend fun delete(reminder: Reminder) {
<<<<<<< Updated upstream
        reminderDao.deleteReminder(reminder)
    }

    fun getReminder(id: Int): Flow<Reminder> {
        return reminderDao.getReminderById(id)
=======
        if (currentUserId.isEmpty()) return
        getCollection().document(reminder.id).delete().await()
    }

    // --- OBTENER UNO ---
    suspend fun getReminder(id: String): Reminder? {
        if (currentUserId.isEmpty()) return null
        return try {
            val doc = getCollection().document(id).get().await()
            doc.toObject(Reminder::class.java)
        } catch (e: Exception) { null }
>>>>>>> Stashed changes
    }
}