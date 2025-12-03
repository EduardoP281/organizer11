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

    // --- LEER TODOS ---
    val allReminders: Flow<List<Reminder>> = callbackFlow {
        if (currentUserId.isEmpty()) {
            trySend(emptyList())
            awaitClose { }
            return@callbackFlow
        }

        val collectionRef = db.collection("users").document(currentUserId).collection("reminders")

        val subscription = collectionRef
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("Firestore", "Error: ", error)
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
        val subscription = db.collection("users").document(currentUserId).collection("reminders")
            .whereEqualTo("starred", true)
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
        val docRef = db.collection("users").document(currentUserId).collection("reminders").document()
        val reminderWithId = reminder.copy(id = docRef.id)
        docRef.set(reminderWithId).await()
    }

    // --- ACTUALIZAR ---
    suspend fun update(reminder: Reminder) {
        if (currentUserId.isEmpty()) return
        db.collection("users").document(currentUserId).collection("reminders")
            .document(reminder.id).set(reminder).await()
    }

    // --- BORRAR ---
    suspend fun delete(reminder: Reminder) {
        if (currentUserId.isEmpty()) return
        db.collection("users").document(currentUserId).collection("reminders")
            .document(reminder.id).delete().await()
    }

    // --- OBTENER UNO ---
    suspend fun getReminder(id: String): Reminder? {
        if (currentUserId.isEmpty()) return null
        return try {
            val doc = db.collection("users").document(currentUserId)
                .collection("reminders").document(id).get().await()
            doc.toObject(Reminder::class.java)
        } catch (e: Exception) { null }
    }
}