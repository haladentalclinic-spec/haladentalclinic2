package com.example.data.repository

import android.util.Log
import com.example.data.api.SupabaseClient
import com.example.data.api.SupabaseService
import com.example.data.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SupabaseRepository {

    private val service: SupabaseService by lazy {
        SupabaseClient.createService()
    }

    // Default mock data for clinical patient databases setup when offline/empty
    private var localPatients = mutableListOf(
        UserData(
            id = "f56b27e8-e9f8-4e89-bd7a-9097beef3001",
            fullName = "Sarah Jenkins",
            patientCode = "HN-2026-90",
            phone = "+9647701234567",
            whatsapp = "+9647701234567",
            gender = "Female",
            age = 29,
            birthDate = "1997-04-12",
            address = "Karrada District, Baghdad",
            disease = "Asthma, mild dental anxiety",
            allergies = "Penicillin",
            bloodType = "O+",
            note = "Enamel hypoplasia on upper central incisors. Prefers evening appointments.",
            role = "patient"
        ),
        UserData(
            id = "f56b27e8-e9f8-4e89-bd7a-9097beef3002",
            fullName = "Khalid Mansour",
            patientCode = "HN-2026-91",
            phone = "+9647509876543",
            whatsapp = "+9647509876543",
            gender = "Male",
            age = 45,
            birthDate = "1981-11-22",
            address = "Mansour Boulevard, Baghdad",
            disease = "Hypertension (controlled)",
            allergies = "No known drug allergies",
            bloodType = "A-",
            note = "Requires lower molar crown placement. Tooth color shade selected was A2.",
            role = "patient"
        ),
        UserData(
            id = "f56b27e8-e9f8-4e89-bd7a-9097beef3003",
            fullName = "Lina Al-Saeed",
            patientCode = "HN-2026-92",
            phone = "+9647805553331",
            whatsapp = "+9647805553331",
            gender = "Female",
            age = 34,
            birthDate = "1992-07-01",
            address = "Zayouna Area, Baghdad",
            disease = "None",
            allergies = "Sulfa drugs",
            bloodType = "B+",
            note = "Orthodontic follow up. Monthly retainer compliance checking.",
            role = "patient"
        ),
        UserData(
            id = "f56b27e8-e9f8-4e89-bd7a-9097beef3004",
            fullName = "Tareq Mahmoud",
            patientCode = "HN-2026-93",
            phone = "+9647712233445",
            whatsapp = "+9647712233445",
            gender = "Male",
            age = 52,
            birthDate = "1974-09-15",
            address = "Adhamiyah District, Baghdad",
            disease = "Type II Diabetes",
            allergies = "Aspirin",
            bloodType = "AB+",
            note = "High risk Gingi-periodontitis. Double cleaning sessions booked.",
            role = "patient"
        )
    )

    private var localWorks = mutableListOf(
        Work(
            id = "3421bbd1-4d1a-4dff-bc11-fbdf0182ae01",
            title = "Zirconia Fixed Bridge",
            workType = "Bridge",
            teeth = listOf(11, 12, 13),
            teethColor = "A1",
            note = "Make sure margins are thin and translucent. Standard high translucency block.",
            startDate = "2026-06-01",
            deliveryDate = "2026-06-07",
            patientId = "f56b27e8-e9f8-4e89-bd7a-9097beef3001"
        ),
        Work(
            id = "3421bbd1-4d1a-4dff-bc11-fbdf0182ae02",
            title = "Porcelain Inlay Restoration",
            workType = "Inlay",
            teeth = listOf(46),
            teethColor = "A2",
            note = "Prep outline is conservative. Keep occlusal anatomy deep.",
            startDate = "2026-06-02",
            deliveryDate = "2026-06-05",
            patientId = "f56b27e8-e9f8-4e89-bd7a-9097beef3002"
        ),
        Work(
            id = "3421bbd1-4d1a-4dff-bc11-fbdf0182ae03",
            title = "Implant Abutment Screw-Retained",
            workType = "Crown",
            teeth = listOf(24),
            teethColor = "B1",
            note = "Engaging abutment, model cast checked. Fast delivery required.",
            startDate = "2026-06-03",
            deliveryDate = "2026-06-08",
            patientId = "f56b27e8-e9f8-4e89-bd7a-9097beef3004"
        )
    )

    private var localReminders = mutableListOf(
        Reminder(
            id = "e113b292-fd0e-436f-b2f1-2856f7091201",
            title = "Deep Gingival Scaling & Root Planing",
            reminderDate = "2026-06-03 10:00 AM",
            status = "pending",
            note = "Prepare topical anesthetic beforehand.",
            patientId = "f56b27e8-e9f8-4e89-bd7a-9097beef3004",
            patientName = "Tareq Mahmoud"
        ),
        Reminder(
            id = "e113b292-fd0e-436f-b2f1-2856f7091202",
            title = "Zirconia Crown Permanent Cementation",
            reminderDate = "2026-06-03 01:30 PM",
            status = "pending",
            note = "Ensure custom lab crown model is on tray.",
            patientId = "f56b27e8-e9f8-4e89-bd7a-9097beef3002",
            patientName = "Khalid Mansour"
        ),
        Reminder(
            id = "e113b292-fd0e-436f-b2f1-2856f7091203",
            title = "Initial Braces Bracket Fitting",
            reminderDate = "2026-06-04 11:30 AM",
            status = "pending",
            note = "Needs extra standard intraoral photography beforehand.",
            patientId = "f56b27e8-e9f8-4e89-bd7a-9097beef3003",
            patientName = "Lina Al-Saeed"
        )
    )

    private var localBanners = listOf(
        BannerInfo(
            id = "1",
            title = "Advanced Clear Aligner Solutions",
            imageUrl = "https://images.unsplash.com/photo-1588776814546-1ffcf47267a5?auto=format&fit=crop&q=80&w=600",
            linkUrl = "https://haladentalclinic.com"
        ),
        BannerInfo(
            id = "2",
            title = "Painless Laser Therapy Gateways",
            imageUrl = "https://images.unsplash.com/photo-1629909613654-28e377c37b09?auto=format&fit=crop&q=80&w=600",
            linkUrl = "https://haladentalclinic.com/laser"
        )
    )

    private var localSettings = ClinicSetting(
        id = "h1",
        clinicName = "Hala Dental Clinic & Lab Ecosystem",
        phone1 = "+9647701234567",
        address = "Level 2, Medical Towers, Karrada, Baghdad",
        currency = "USD"
    )

    suspend fun getClinics(): List<Clinic> = withContext(Dispatchers.IO) {
        try {
            service.getClinics()
        } catch (e: Exception) {
            Log.e("SupabaseRepository", "Error getting clinics, falling back: ${e.message}")
            listOf(Clinic(id = "c1", name = "Hala Dental Main Clinic", phone = localSettings.phone1, address = localSettings.address))
        }
    }

    suspend fun getUsers(role: String = "patient"): List<UserData> = withContext(Dispatchers.IO) {
        try {
            // PostgREST filtering format column=eq.value
            val list = service.getUsers(roleFilter = "eq.$role")
            if (list.isNotEmpty()) {
                // Merge remote results to stay synced if needed
                list
            } else {
                localPatients.filter { it.role == role }
            }
        } catch (e: Exception) {
            Log.e("SupabaseRepository", "Error getting users from Supabase, returning local state: ${e.message}")
            localPatients.filter { it.role == role }
        }
    }

    suspend fun createUser(user: UserData): UserData = withContext(Dispatchers.IO) {
        val nextCode = "HN-2026-" + (90 + localPatients.size)
        val preparedUser = user.copy(
            id = user.id ?: java.util.UUID.randomUUID().toString(),
            patientCode = user.patientCode ?: nextCode
        )
        try {
            val response = service.createUser(preparedUser)
            if (response.isNotEmpty()) {
                val created = response.first()
                localPatients.add(0, created)
                created
            } else {
                localPatients.add(0, preparedUser)
                preparedUser
            }
        } catch (e: Exception) {
            Log.e("SupabaseRepository", "Error inserting user to Supabase: ${e.message}. Saving to local memory Cache.")
            localPatients.add(0, preparedUser)
            preparedUser
        }
    }

    suspend fun updateUser(id: String, user: UserData): UserData = withContext(Dispatchers.IO) {
        try {
            val response = service.updateUser("eq.$id", user)
            val updated = if (response.isNotEmpty()) response.first() else user
            val idx = localPatients.indexOfFirst { it.id == id }
            if (idx != -1) {
                localPatients[idx] = updated
            } else {
                localPatients.add(0, updated)
            }
            updated
        } catch (e: Exception) {
            Log.e("SupabaseRepository", "Error updating user in Supabase: ${e.message}")
            val idx = localPatients.indexOfFirst { it.id == id }
            if (idx != -1) {
                localPatients[idx] = user
            }
            user
        }
    }

    suspend fun getWorks(): List<Work> = withContext(Dispatchers.IO) {
        try {
            val list = service.getWorks()
            if (list.isNotEmpty()) list else localWorks
        } catch (e: Exception) {
            Log.e("SupabaseRepository", "Error getting works: ${e.message}")
            localWorks
        }
    }

    suspend fun createWork(work: Work): Work = withContext(Dispatchers.IO) {
        val preparedWork = work.copy(
            id = work.id ?: java.util.UUID.randomUUID().toString(),
            startDate = work.startDate ?: "2026-06-03"
        )
        try {
            val response = service.createWork(preparedWork)
            if (response.isNotEmpty()) {
                val created = response.first()
                localWorks.add(0, created)
                created
            } else {
                localWorks.add(0, preparedWork)
                preparedWork
            }
        } catch (e: Exception) {
            Log.e("SupabaseRepository", "Error saving laboratory works: ${e.message}")
            localWorks.add(0, preparedWork)
            preparedWork
        }
    }

    suspend fun getReminders(): List<Reminder> = withContext(Dispatchers.IO) {
        try {
            val list = service.getReminders()
            if (list.isNotEmpty()) {
                // Populate display patient name from local list matching ID
                list.map { rem ->
                    if (rem.patientName.isNullOrEmpty() && rem.patientId != null) {
                        val p = localPatients.find { it.id == rem.patientId }
                        rem.copy(patientName = p?.fullName ?: "Unknown Patient")
                    } else rem
                }
            } else {
                localReminders
            }
        } catch (e: Exception) {
            Log.e("SupabaseRepository", "Error getting reminders: ${e.message}")
            localReminders
        }
    }

    suspend fun createReminder(reminder: Reminder): Reminder = withContext(Dispatchers.IO) {
        val prepared = reminder.copy(id = reminder.id ?: java.util.UUID.randomUUID().toString())
        try {
            val response = service.createReminder(prepared)
            if (response.isNotEmpty()) {
                val created = response.first()
                val patientName = localPatients.find { it.id == created.patientId }?.fullName
                val res = created.copy(patientName = patientName)
                localReminders.add(0, res)
                res
            } else {
                val patientName = localPatients.find { it.id == prepared.patientId }?.fullName
                val res = prepared.copy(patientName = patientName)
                localReminders.add(0, res)
                res
            }
        } catch (e: Exception) {
            Log.e("SupabaseRepository", "Error saving reminder: ${e.message}")
            val patientName = localPatients.find { it.id == prepared.patientId }?.fullName
            val res = prepared.copy(patientName = patientName)
            localReminders.add(0, res)
            res
        }
    }

    suspend fun getBanners(): List<BannerInfo> = withContext(Dispatchers.IO) {
        try {
            val list = service.getBanners()
            if (list.isNotEmpty()) list else localBanners
        } catch (e: Exception) {
            Log.e("SupabaseRepository", "Error getting banners: ${e.message}")
            localBanners
        }
    }

    suspend fun getSettings(): ClinicSetting = withContext(Dispatchers.IO) {
        try {
            val list = service.getSettings()
            if (list.isNotEmpty()) list.first() else localSettings
        } catch (e: Exception) {
            Log.e("SupabaseRepository", "Error getting settings: ${e.message}")
            localSettings
        }
    }
}
