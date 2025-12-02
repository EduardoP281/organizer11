package com.example.organizer11.data.repository

import com.example.organizer11.data.model.Reminder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class ReminderRepository { // Ya no recibe 'dao' en el constructor

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val currentUserId: String
        get() = auth.currentUser?.uid ?: ""

    // Referencia a la colección del usuario actual: "users/{uid}/reminders"
    private fun getUserRemindersCollection() =
        db.collection("users").document(currentUserId).collection("reminders")

    // --- LEER DATOS (En tiempo real) ---
    val allReminders: Flow<List<Reminder>> = callbackFlow {
        if (currentUserId.isEmpty()) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }

        // Nos suscribimos a cambios en Firestore
        val subscription = getUserRemindersCollection()
            //.orderBy("id", Query.Direction.DESCENDING) // Opcional: ordenar por fecha si tuvieras timestamp
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null) {
                    // Convertimos los documentos de Firestore a objetos Reminder
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
            close()
            return@callbackFlow
        }
        val subscription = getUserRemindersCollection()
            .whereEqualTo("starred", true) // Firestore busca el campo 'starred'
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null) {
                    val reminders = snapshot.toObjects(Reminder::class.java)
                    trySend(reminders)
                }
            }
        awaitClose { subscription.remove() }
    }

    // --- GUARDAR (Insertar) ---
    suspend fun insert(reminder: Reminder) {
        if (currentUserId.isEmpty()) return

        // 1. Crear documento nuevo para obtener un ID único automático
        val docRef = getUserRemindersCollection().document()

        // 2. Asignamos ese ID al objeto para guardarlo dentro
        val reminderWithId = reminder.copy(id = docRef.id)

        // 3. Guardamos en la nube
        docRef.set(reminderWithId).await()
    }

    // --- ACTUALIZAR ---
    suspend fun update(reminder: Reminder) {
        if (currentUserId.isEmpty()) return
        getUserRemindersCollection().document(reminder.id).set(reminder).await()
    }

    // --- BORRAR ---
    suspend fun delete(reminder: Reminder) {
        if (currentUserId.isEmpty()) return
        getUserRemindersCollection().document(reminder.id).delete().await()
    }

    // --- OBTENER UNO SOLO (Por ID) ---
    suspend fun getReminder(id: String): Reminder? {
        if (currentUserId.isEmpty()) return null
        try {
            val snapshot = getUserRemindersCollection().document(id).get().await()
            return snapshot.toObject(Reminder::class.java)
        } catch (e: Exception) {
            return null
        }
    }
}