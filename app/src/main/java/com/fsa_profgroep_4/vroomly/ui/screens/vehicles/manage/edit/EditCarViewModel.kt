package com.fsa_profgroep_4.vroomly.ui.screens.vehicles.manage.edit

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fsa_profgroep_4.vroomly.R
import com.fsa_profgroep_4.vroomly.data.vehicle.VehicleRepository
import com.fsa_profgroep_4.vroomly.navigation.Navigator
import com.fsa_profgroep_4.vroomly.ui.models.FormField
import com.fsa_profgroep_4.vroomly.ui.screens.vehicles.manage.common.VehicleFormValidator
import com.fsa_profgroep_4.vroomly.ui.screens.vehicles.manage.common.VehicleImage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

private const val TAG = "EditCarViewModel"

data class EditCarUiState(
    val licensePlate: FormField = FormField(),
    val brand: FormField = FormField(),
    val model: FormField = FormField(),
    val year: FormField = FormField(),
    val color: FormField = FormField(),
    val category: FormField = FormField(),
    val engineType: FormField = FormField(),
    val seats: FormField = FormField(),
    val costPerDay: FormField = FormField(),
    val odometerKm: FormField = FormField(),
    val motValidTill: FormField = FormField(),
    val vin: FormField = FormField(),
    val zeroToHundred: FormField = FormField(),
    val address: FormField = FormField(),
    val latitude: FormField = FormField(),
    val longitude: FormField = FormField(),
    val isLoading: Boolean = false,
    val isLoadingVehicle: Boolean = true,
    val generalError: String? = null,
    val existingImages: List<VehicleImage> = emptyList(),
    val selectedImageUris: List<Uri> = emptyList(),
    val isUploadingImage: Boolean = false
) {
    fun hasErrors() = listOf(
        licensePlate, brand, model, year, color, seats,
        costPerDay, odometerKm, motValidTill, vin, zeroToHundred, address
    ).any { it.error != null }
}

class EditCarViewModel(
    private val navigator: Navigator,
    private val vehicleRepository: VehicleRepository,
    private val application: Application,
    private val vehicleId: Int
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditCarUiState())
    val uiState: StateFlow<EditCarUiState> = _uiState.asStateFlow()

    init {
        loadVehicleDetails()
    }

    private fun loadVehicleDetails() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoadingVehicle = true, generalError = null)

            vehicleRepository.getVehicleById(vehicleId)
                .onSuccess { vehicle ->
                    Log.d(TAG, "Loaded vehicle with ${vehicle.images.size} images")
                    vehicle.images.forEach { img ->
                        Log.d(TAG, "Image: id=${img.id}, number=${img.number}, url=${img.url}")
                    }
                    val existingImages = vehicle.images
                        .sortedBy { it.number }
                        .mapNotNull { img ->
                            img.id?.let { id ->
                                VehicleImage(id = id, url = img.url, number = img.number)
                            }
                        }
                    _uiState.value = _uiState.value.copy(
                        licensePlate = FormField(value = vehicle.licensePlate),
                        brand = FormField(value = vehicle.brand),
                        model = FormField(value = vehicle.model),
                        year = FormField(value = vehicle.year.toString()),
                        color = FormField(value = vehicle.color),
                        category = FormField(value = vehicle.category.name),
                        engineType = FormField(value = vehicle.engineType.name),
                        seats = FormField(value = vehicle.seats.toString()),
                        costPerDay = FormField(value = vehicle.costPerDay.toString()),
                        odometerKm = FormField(value = vehicle.odometerKm.toString()),
                        motValidTill = FormField(value = vehicle.motValidTill),
                        vin = FormField(value = vehicle.vin),
                        zeroToHundred = FormField(value = vehicle.zeroToHundred.toString()),
                        address = FormField(value = vehicle.location?.address ?: ""),
                        latitude = FormField(value = vehicle.location?.latitude?.toString() ?: "52.3676"),
                        longitude = FormField(value = vehicle.location?.longitude?.toString() ?: "4.9041"),
                        existingImages = existingImages,
                        isLoadingVehicle = false
                    )
                }
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoadingVehicle = false,
                        generalError = e.message ?: "Failed to load vehicle"
                    )
                }
        }
    }

    fun onLicensePlateChange(value: String) {
        if (value.length > 15) return
        _uiState.value = _uiState.value.copy(licensePlate = _uiState.value.licensePlate.copy(value = value, error = null))
    }

    fun onBrandChange(value: String) {
        _uiState.value = _uiState.value.copy(brand = _uiState.value.brand.copy(value = value, error = null))
    }

    fun onModelChange(value: String) {
        _uiState.value = _uiState.value.copy(model = _uiState.value.model.copy(value = value, error = null))
    }

    fun onYearChange(value: String) {
        _uiState.value = _uiState.value.copy(year = _uiState.value.year.copy(value = value, error = null))
    }

    fun onColorChange(value: String) {
        _uiState.value = _uiState.value.copy(color = _uiState.value.color.copy(value = value, error = null))
    }

    fun onCategoryChange(value: String) {
        _uiState.value = _uiState.value.copy(category = _uiState.value.category.copy(value = value, error = null))
    }

    fun onEngineTypeChange(value: String) {
        _uiState.value = _uiState.value.copy(engineType = _uiState.value.engineType.copy(value = value, error = null))
    }

    fun onSeatsChange(value: String) {
        _uiState.value = _uiState.value.copy(seats = _uiState.value.seats.copy(value = value, error = null))
    }

    fun onCostPerDayChange(value: String) {
        _uiState.value = _uiState.value.copy(costPerDay = _uiState.value.costPerDay.copy(value = value, error = null))
    }

    fun onOdometerKmChange(value: String) {
        _uiState.value = _uiState.value.copy(odometerKm = _uiState.value.odometerKm.copy(value = value, error = null))
    }

    fun onMotValidTillChange(value: String) {
        _uiState.value = _uiState.value.copy(motValidTill = _uiState.value.motValidTill.copy(value = value, error = null))
    }

    fun onVinChange(value: String) {
        _uiState.value = _uiState.value.copy(vin = _uiState.value.vin.copy(value = value, error = null))
    }

    fun onZeroToHundredChange(value: String) {
        _uiState.value = _uiState.value.copy(zeroToHundred = _uiState.value.zeroToHundred.copy(value = value, error = null))
    }

    fun onAddressChange(value: String) {
        _uiState.value = _uiState.value.copy(address = _uiState.value.address.copy(value = value, error = null))
    }

    fun onImageSelected(uri: Uri) {
        _uiState.value = _uiState.value.copy(isUploadingImage = true)
        viewModelScope.launch {
            uploadNewImage(uri)
        }
    }

    fun onRemoveExistingImage(image: VehicleImage) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isUploadingImage = true)
            vehicleRepository.removeImageFromVehicle(vehicleId, image.id)
                .onSuccess {
                    _uiState.value = _uiState.value.copy(
                        existingImages = _uiState.value.existingImages.filter { it.id != image.id },
                        isUploadingImage = false
                    )
                }
                .onFailure { e ->
                    Log.e(TAG, "Failed to remove image", e)
                    _uiState.value = _uiState.value.copy(
                        isUploadingImage = false,
                        generalError = application.getString(R.string.image_upload_failed)
                    )
                }
        }
    }

    private suspend fun uploadNewImage(uri: Uri) {
        Log.d(TAG, "uploadNewImage called with uri: $uri, vehicleId: $vehicleId")
        val contentResolver = application.contentResolver
        try {
            val inputStream = contentResolver.openInputStream(uri)
            val imageBytes = inputStream?.readBytes()
            inputStream?.close()

            Log.d(TAG, "Image bytes read: ${imageBytes?.size ?: 0} bytes")

            if (imageBytes != null && imageBytes.isNotEmpty()) {
                val nextImageNumber = _uiState.value.existingImages.size + _uiState.value.selectedImageUris.size
                Log.d(TAG, "Uploading as image number: $nextImageNumber")

                val result = vehicleRepository.uploadAndAddImageToVehicle(
                    vehicleId = vehicleId,
                    imageBytes = imageBytes,
                    imageNumber = nextImageNumber
                )
                result.onSuccess { imageUrl ->
                    Log.d(TAG, "Upload successful: $imageUrl")
                    val newImage = VehicleImage(
                        id = 0,
                        url = imageUrl,
                        number = nextImageNumber
                    )
                    _uiState.value = _uiState.value.copy(
                        existingImages = _uiState.value.existingImages + newImage,
                        isUploadingImage = false
                    )
                }.onFailure { e ->
                    Log.e(TAG, "Upload failed", e)
                    _uiState.value = _uiState.value.copy(
                        isUploadingImage = false,
                        generalError = application.getString(R.string.image_upload_failed)
                    )
                }
            } else {
                Log.e(TAG, "Failed to read image bytes from URI")
                _uiState.value = _uiState.value.copy(
                    isUploadingImage = false,
                    generalError = application.getString(R.string.image_upload_failed)
                )
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception while uploading image", e)
            _uiState.value = _uiState.value.copy(
                isUploadingImage = false,
                generalError = application.getString(R.string.image_upload_failed)
            )
        }
    }

    fun saveVehicle() {
        val currentState = _uiState.value

        val validatedState = currentState.copy(
            licensePlate = currentState.licensePlate.copy(
                error = VehicleFormValidator.validateLicensePlate(
                    currentState.licensePlate.value,
                    application.getString(R.string.license_plate_is_required),
                    application.getString(R.string.invalid_license_plate_format)
                )
            ),
            brand = currentState.brand.copy(
                error = VehicleFormValidator.validateRequired(
                    currentState.brand.value,
                    application.getString(R.string.brand_is_required)
                )
            ),
            model = currentState.model.copy(
                error = VehicleFormValidator.validateRequired(
                    currentState.model.value,
                    application.getString(R.string.model_is_required)
                )
            ),
            year = currentState.year.copy(
                error = VehicleFormValidator.validateYear(
                    currentState.year.value,
                    application.getString(R.string.year_is_required),
                    application.getString(R.string.invalid_year)
                )
            ),
            color = currentState.color.copy(
                error = VehicleFormValidator.validateRequired(
                    currentState.color.value,
                    application.getString(R.string.color_is_required)
                )
            ),
            seats = currentState.seats.copy(
                error = VehicleFormValidator.validateSeats(
                    currentState.seats.value,
                    application.getString(R.string.seats_is_required),
                    application.getString(R.string.invalid_seats)
                )
            ),
            costPerDay = currentState.costPerDay.copy(
                error = VehicleFormValidator.validatePositiveNumber(
                    currentState.costPerDay.value,
                    application.getString(R.string.cost_per_day_is_required),
                    application.getString(R.string.invalid_cost_per_day)
                )
            ),
            odometerKm = currentState.odometerKm.copy(
                error = VehicleFormValidator.validatePositiveNumber(
                    currentState.odometerKm.value,
                    application.getString(R.string.odometer_is_required),
                    application.getString(R.string.invalid_odometer)
                )
            ),
            motValidTill = currentState.motValidTill.copy(
                error = VehicleFormValidator.validateRequired(
                    currentState.motValidTill.value,
                    application.getString(R.string.mot_valid_till_is_required)
                )
            ),
            vin = currentState.vin.copy(
                error = VehicleFormValidator.validateVin(
                    currentState.vin.value,
                    application.getString(R.string.vin_is_required),
                    application.getString(R.string.invalid_vin_format)
                )
            ),
            zeroToHundred = currentState.zeroToHundred.copy(
                error = VehicleFormValidator.validatePositiveNumber(
                    currentState.zeroToHundred.value,
                    application.getString(R.string.zero_to_hundred_is_required),
                    application.getString(R.string.invalid_zero_to_hundred)
                )
            ),
            address = currentState.address.copy(
                error = VehicleFormValidator.validateRequired(
                    currentState.address.value,
                    application.getString(R.string.address_is_required)
                )
            )
        )

        _uiState.value = validatedState
        if (validatedState.hasErrors()) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, generalError = null)

            val result = vehicleRepository.updateVehicle(
                vehicleId = vehicleId,
                licensePlate = validatedState.licensePlate.value,
                brand = validatedState.brand.value,
                model = validatedState.model.value,
                year = validatedState.year.value.toIntOrNull(),
                color = validatedState.color.value,
                category = validatedState.category.value,
                engineType = validatedState.engineType.value,
                seats = validatedState.seats.value.toIntOrNull(),
                costPerDay = validatedState.costPerDay.value.toDoubleOrNull(),
                odometerKm = validatedState.odometerKm.value.toDoubleOrNull(),
                motValidTill = validatedState.motValidTill.value,
                vin = validatedState.vin.value,
                zeroToHundred = validatedState.zeroToHundred.value.toDoubleOrNull(),
                address = validatedState.address.value,
                latitude = validatedState.latitude.value.toDoubleOrNull(),
                longitude = validatedState.longitude.value.toDoubleOrNull()
            )

            result.onSuccess {
                _uiState.value = _uiState.value.copy(isLoading = false)
                navigator.goBack()
            }.onFailure { error ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    generalError = error.message?.split(" : ")?.last()
                        ?: application.getString(R.string.vehicle_update_failed)
                )
            }
        }
    }

    fun onBackClicked() {
        navigator.goBack()
    }
}
