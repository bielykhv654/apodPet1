package com.example.apodpet

import com.example.apodpet.app_routes.ApodPhoto
import com.example.apodpet.app_routes.PhotoDetails
import database.NasaPhotoDto
import database.NasaPhotoEntity

expect val isNative: Boolean
expect val isDesktop: Boolean

fun NasaPhotoDto.toApodPhoto(): ApodPhoto = ApodPhoto(
    title = title ?: "NASA APOD",
    explanation = explanation ?: "",
    imageUrl = hdurl ?: url ?: "",
    date = date ?: "",
    copyright = copyright,
    mediaType = mediaType
)

fun NasaPhotoEntity.toApodPhoto(): ApodPhoto = ApodPhoto(
    title = title,
    explanation = explanation,
    imageUrl = url,
    date = date,
    copyright = copyright,
    mediaType = mediaType
)

fun PhotoDetails(dto: NasaPhotoDto): PhotoDetails = PhotoDetails(photo = dto.toApodPhoto())

fun PhotoDetails(entity: NasaPhotoEntity): PhotoDetails = PhotoDetails(photo = entity.toApodPhoto())

