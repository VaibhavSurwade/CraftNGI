package com.example.craftscanner.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ProductDetail(
    @Json(name = "id") val id: Int? = null,
    @Json(name = "barcode") val barcode: String,
    @Json(name = "product_name") val productName: String,
    @Json(name = "category") val category: String? = null,

    // Section 1: Fabric & Yarn
    @Json(name = "fabric_composition") val fabricComposition: String,
    @Json(name = "yarn_spinning") val yarnSpinning: String,
    @Json(name = "fabric_weave") val fabricWeave: String,
    @Json(name = "embroidery_yarn") val embroideryYarn: String,

    // Section 2: Product Specifications
    @Json(name = "design") val design: String,
    @Json(name = "size") val size: String,
    @Json(name = "weight") val weight: String,
    @Json(name = "embroidery") val embroidery: String,

    // Section 3: GI & Artisan Origin Details
    @Json(name = "authorized_gi_user") val authorizedGiUser: String,
    @Json(name = "address_authorized_gi_user") val addressAuthorizedGiUser: String,
    @Json(name = "artisan_or_weaver") val artisanOrWeaver: String,
    @Json(name = "address_artisan_or_weaver") val addressArtisanOrWeaver: String
)
