package com.example.udhaarpay.data.remote

import android.net.Uri
import com.example.udhaarpay.utils.ErrorHandler
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseStorageService @Inject constructor(
    private val errorHandler: ErrorHandler
) {

    private val storage = FirebaseStorage.getInstance()
    private val storageRef = storage.reference

    // Upload profile image
    suspend fun uploadProfileImage(userId: String, imageUri: Uri): Result<String> {
        return try {
            val profileRef = storageRef.child("profile_images/$userId.jpg")
            val uploadTask = profileRef.putFile(imageUri).await()
            val downloadUrl = profileRef.downloadUrl.await()
            Result.success(downloadUrl.toString())
        } catch (e: Exception) {
            errorHandler.handleError(e, "FirebaseStorageService.uploadProfileImage")
            Result.failure(e)
        }
    }

    // Upload file from local path
    suspend fun uploadFile(userId: String, localFilePath: String, fileName: String): Result<String> {
        return try {
            val file = Uri.fromFile(File(localFilePath))
            val fileRef = storageRef.child("user_files/$userId/$fileName")
            val uploadTask = fileRef.putFile(file).await()
            val downloadUrl = fileRef.downloadUrl.await()
            Result.success(downloadUrl.toString())
        } catch (e: Exception) {
            errorHandler.handleError(e, "FirebaseStorageService.uploadFile")
            Result.failure(e)
        }
    }

    // Download file to local path
    suspend fun downloadFile(downloadUrl: String, localFilePath: String): Result<Unit> {
        return try {
            val fileRef = storage.getReferenceFromUrl(downloadUrl)
            val localFile = File(localFilePath)
            fileRef.getFile(localFile).await()
            Result.success(Unit)
        } catch (e: Exception) {
            errorHandler.handleError(e, "FirebaseStorageService.downloadFile")
            Result.failure(e)
        }
    }

    // Delete file
    suspend fun deleteFile(fileUrl: String): Result<Unit> {
        return try {
            val fileRef = storage.getReferenceFromUrl(fileUrl)
            fileRef.delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            errorHandler.handleError(e, "FirebaseStorageService.deleteFile")
            Result.failure(e)
        }
    }

    // Get file metadata
    suspend fun getFileMetadata(fileUrl: String): Result<Long> {
        return try {
            val fileRef = storage.getReferenceFromUrl(fileUrl)
            val metadata = fileRef.metadata.await()
            Result.success(metadata.sizeBytes)
        } catch (e: Exception) {
            errorHandler.handleError(e, "FirebaseStorageService.getFileMetadata")
            Result.failure(e)
        }
    }

    // Check if file exists
    suspend fun fileExists(fileUrl: String): Boolean {
        return try {
            val fileRef = storage.getReferenceFromUrl(fileUrl)
            fileRef.metadata.await()
            true
        } catch (e: Exception) {
            false
        }
    }

    // Generate signed URL (if needed for temporary access)
    suspend fun generateSignedUrl(fileUrl: String, expirationTimeMillis: Long = 3600000): Result<String> {
        return try {
            val fileRef = storage.getReferenceFromUrl(fileUrl)
            val url = fileRef.downloadUrl.await().toString()
            // Note: Firebase Storage doesn't support signed URLs like AWS S3
            // This just returns the download URL
            Result.success(url)
        } catch (e: Exception) {
            errorHandler.handleError(e, "FirebaseStorageService.generateSignedUrl")
            Result.failure(e)
        }
    }
}
