package com.example.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Clinic(
    @Json(name = "id") val id: String? = null,
    @Json(name = "name") val name: String,
    @Json(name = "phone") val phone: String? = null,
    @Json(name = "address") val address: String? = null,
    @Json(name = "url") val url: String? = null,
    @Json(name = "is_active") val isActive: Boolean? = true
)

@JsonClass(generateAdapter = true)
data class UserData(
    @Json(name = "id") val id: String? = null,
    @Json(name = "clinic_id") val clinicId: String? = null,
    @Json(name = "username") val username: String? = null,
    @Json(name = "full_name") val fullName: String,
    @Json(name = "phone") val phone: String? = null,
    @Json(name = "role") val role: String? = "patient",
    @Json(name = "image") val image: String? = null,
    @Json(name = "is_active") val isActive: Boolean? = true,
    @Json(name = "patient_code") val patientCode: String? = null,
    @Json(name = "whatsapp") val whatsapp: String? = null,
    @Json(name = "gender") val gender: String? = null,
    @Json(name = "age") val age: Int? = null,
    @Json(name = "birth_date") val birthDate: String? = null,
    @Json(name = "address") val address: String? = null,
    @Json(name = "disease") val disease: String? = null,
    @Json(name = "allergies") val allergies: String? = null,
    @Json(name = "blood_type") val bloodType: String? = null,
    @Json(name = "note") val note: String? = null,
    @Json(name = "reference_name") val referenceName: String? = null
)

@JsonClass(generateAdapter = true)
data class Work(
    @Json(name = "id") val id: String? = null,
    @Json(name = "clinic_id") val clinicId: String? = null,
    @Json(name = "patient_id") val patientId: String? = null,
    @Json(name = "doctor_id") val doctorId: String? = null,
    @Json(name = "lab_id") val labId: String? = null,
    @Json(name = "title") val title: String,
    @Json(name = "work_type") val workType: String? = null,
    @Json(name = "teeth") val teeth: List<Int>? = null, // Can serialise tooth integers
    @Json(name = "teeth_color") val teethColor: String? = null,
    @Json(name = "note") val note: String? = null,
    @Json(name = "start_date") val startDate: String? = null,
    @Json(name = "delivery_date") val deliveryDate: String? = null,
    @Json(name = "created_at") val createdAt: String? = null
)

@JsonClass(generateAdapter = true)
data class Reminder(
    @Json(name = "id") val id: String? = null,
    @Json(name = "clinic_id") val clinicId: String? = null,
    @Json(name = "patient_id") val patientId: String? = null,
    @Json(name = "title") val title: String,
    @Json(name = "reminder_date") val reminderDate: String,
    @Json(name = "status") val status: String? = "pending",
    @Json(name = "note") val note: String? = null,
    @Json(name = "patient_name") val patientName: String? = null // local display helper
)

@JsonClass(generateAdapter = true)
data class BannerInfo(
    @Json(name = "id") val id: String? = null,
    @Json(name = "clinic_id") val clinicId: String? = null,
    @Json(name = "title") val title: String? = null,
    @Json(name = "image_url") val imageUrl: String,
    @Json(name = "link_url") val linkUrl: String? = null,
    @Json(name = "is_active") val isActive: Boolean? = true
)

@JsonClass(generateAdapter = true)
data class ClinicSetting(
    @Json(name = "id") val id: String? = null,
    @Json(name = "clinic_id") val clinicId: String? = null,
    @Json(name = "clinic_name") val clinicName: String? = null,
    @Json(name = "phone_1") val phone1: String? = null,
    @Json(name = "address") val address: String? = null,
    @Json(name = "currency") val currency: String? = "USD"
)
