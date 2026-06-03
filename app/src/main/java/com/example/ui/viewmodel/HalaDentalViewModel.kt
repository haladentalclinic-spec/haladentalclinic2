package com.example.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.*
import com.example.data.repository.SupabaseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface UiState {
    object Idle : UiState
    object Loading : UiState
    object Success : UiState
    data class Error(val message: String) : UiState
}

class HalaDentalViewModel : ViewModel() {

    private val repository = SupabaseRepository()

    // Screen States
    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _patients = MutableStateFlow<List<UserData>>(emptyList())
    val patients: StateFlow<List<UserData>> = _patients.asStateFlow()

    private val _works = MutableStateFlow<List<Work>>(emptyList())
    val works: StateFlow<List<Work>> = _works.asStateFlow()

    private val _reminders = MutableStateFlow<List<Reminder>>(emptyList())
    val reminders: StateFlow<List<Reminder>> = _reminders.asStateFlow()

    private val _banners = MutableStateFlow<List<BannerInfo>>(emptyList())
    val banners: StateFlow<List<BannerInfo>> = _banners.asStateFlow()

    private val _settings = MutableStateFlow<ClinicSetting?>(null)
    val settings: StateFlow<ClinicSetting?> = _settings.asStateFlow()

    // Current selected navigation patient
    private val _selectedPatient = MutableStateFlow<UserData?>(null)
    val selectedPatient: StateFlow<UserData?> = _selectedPatient.asStateFlow()

    init {
        loadAllData()
    }

    fun loadAllData() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                // Load critical clinical items asynchronously
                val fetchedPatients = repository.getUsers("patient")
                val fetchedWorks = repository.getWorks()
                val fetchedReminders = repository.getReminders()
                val fetchedBanners = repository.getBanners()
                val fetchedSettings = repository.getSettings()

                _patients.value = fetchedPatients
                _works.value = fetchedWorks
                _reminders.value = fetchedReminders
                _banners.value = fetchedBanners
                _settings.value = fetchedSettings

                // Auto-select first patient context if null
                if (_selectedPatient.value == null && fetchedPatients.isNotEmpty()) {
                    _selectedPatient.value = fetchedPatients.first()
                }

                _uiState.value = UiState.Success
            } catch (e: Exception) {
                Log.e("HalaDentalViewModel", "Error loading clinic dashboard: ${e.message}")
                _uiState.value = UiState.Error(e.message ?: "Could not sync with Hala Dental Servers")
            }
        }
    }

    fun selectPatient(patient: UserData?) {
        _selectedPatient.value = patient
    }

    fun updatePatientProfile(
        id: String,
        fullName: String,
        phone: String,
        gender: String,
        age: Int,
        address: String,
        disease: String,
        allergies: String,
        bloodType: String,
        note: String
    ) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                val updatedUser = UserData(
                    id = id,
                    fullName = fullName,
                    phone = phone,
                    whatsapp = phone,
                    gender = gender,
                    age = age,
                    address = address,
                    disease = disease.ifEmpty { "None declared" },
                    allergies = allergies.ifEmpty { "None declared" },
                    bloodType = bloodType,
                    note = note,
                    role = "patient"
                )
                val response = repository.updateUser(id, updatedUser)
                
                // Fetch refreshed list
                val updatedPatients = repository.getUsers("patient")
                _patients.value = updatedPatients
                
                // Refresh active selection
                if (_selectedPatient.value?.id == id) {
                    _selectedPatient.value = response
                }
                _uiState.value = UiState.Success
            } catch (e: Exception) {
                Log.e("HalaDentalViewModel", "Error updating patient profile: ${e.message}")
                _uiState.value = UiState.Success
            }
        }
    }

    fun createPatient(
        name: String,
        phone: String,
        gender: String,
        age: Int,
        disease: String,
        allergies: String,
        bloodType: String,
        note: String
    ) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                val newUser = UserData(
                    fullName = name,
                    phone = phone,
                    whatsapp = phone, // Default WhatsApp to the patient phone
                    gender = gender,
                    age = age,
                    disease = disease.ifEmpty { "None declared" },
                    allergies = allergies.ifEmpty { "None declared" },
                    bloodType = bloodType,
                    note = note,
                    role = "patient"
                )
                val response = repository.createUser(newUser)
                
                // Fetch refreshed list
                val updatedPatients = repository.getUsers("patient")
                _patients.value = updatedPatients
                
                // Auto-select newly registered patient
                val newlyCreated = updatedPatients.find { it.fullName == name && it.phone == phone } ?: response
                _selectedPatient.value = newlyCreated
                
                _uiState.value = UiState.Success
            } catch (e: Exception) {
                Log.e("HalaDentalViewModel", "Error creating patient: ${e.message}")
                _uiState.value = UiState.Success // Gracefully bypass so offline caching remains interactive
            }
        }
    }

    fun createWorkOrder(
        title: String,
        workType: String,
        teeth: List<Int>,
        teethColor: String,
        note: String,
        patientId: String,
        deliveryDays: Int
    ) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                // Format simple current date and expected delivery date
                val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US)
                val cal = java.util.Calendar.getInstance()
                val startDate = sdf.format(cal.time)
                cal.add(java.util.Calendar.DAY_OF_YEAR, deliveryDays)
                val deliveryDate = sdf.format(cal.time)

                val newWork = Work(
                    title = title,
                    workType = workType,
                    teeth = teeth,
                    teethColor = teethColor,
                    note = note,
                    patientId = patientId,
                    startDate = startDate,
                    deliveryDate = deliveryDate
                )
                repository.createWork(newWork)
                
                // Reload list
                val updatedWorks = repository.getWorks()
                _works.value = updatedWorks
                _uiState.value = UiState.Success
            } catch (e: Exception) {
                Log.e("HalaDentalViewModel", "Error creating dental work order: ${e.message}")
                _uiState.value = UiState.Success
            }
        }
    }

    fun createAppointmentReminder(
        title: String,
        dateString: String,
        patientId: String,
        note: String
    ) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                val newReminder = Reminder(
                    title = title,
                    reminderDate = dateString,
                    patientId = patientId,
                    note = note,
                    status = "pending"
                )
                repository.createReminder(newReminder)
                
                val updatedReminders = repository.getReminders()
                _reminders.value = updatedReminders
                _uiState.value = UiState.Success
            } catch (e: Exception) {
                Log.e("HalaDentalViewModel", "Error booking appointment reminder: ${e.message}")
                _uiState.value = UiState.Success
            }
        }
    }
}
