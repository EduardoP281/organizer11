package com.example.organizer11.data.repository

import android.util.Log
import com.example.organizer11.data.model.Reminder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class ReminderRepository {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val currentUserId: String
        get() = auth.currentUser?.uid ?: ""

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

    // --- GUARDAR ---
    suspend fun insert(reminder: Reminder) {
        if (currentUserId.isEmpty()) return
        val docRef = getCollection().document()
        val reminderWithId = reminder.copy(id = docRef.id)
        docRef.set(reminderWithId).await()
    }

    // --- ACTUALIZAR ---
    suspend fun update(reminder: Reminder) {
        if (currentUserId.isEmpty()) return
        getCollection().document(reminder.id).set(reminder).await()
    }

    // --- BORRAR ---
    suspend fun delete(reminder: Reminder) {
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
    }
}