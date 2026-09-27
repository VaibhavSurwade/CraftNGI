package com.example.craftscanner.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.craftscanner.data.model.ProductDetail
import com.example.craftscanner.ui.components.DetailSectionCard
import com.example.craftscanner.ui.components.SectionItem
import com.example.craftscanner.ui.theme.ForestGreenGI
import com.example.craftscanner.ui.theme.GoldAccent
import com.example.craftscanner.ui.theme.GreenBadgeBg
import com.example.craftscanner.ui.theme.TerracottaPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    product: ProductDetail,
    onBackToScanner: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Verified Product Details",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackToScanner) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back to Scanner",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = TerracottaPrimary
                )
            )
        },
        bottomBar = {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 8.dp
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Button(
                        onClick = onBackToScanner,
                        colors = ButtonDefaults.buttonColors(containerColor = TerracottaPrimary),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.QrCodeScanner,
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Scan Another Product",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
                .padding(bottom = 20.dp)
        ) {
            // Header summary banner
            Surface(
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                shadowElevation = 2.dp
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // GI Authenticity Tag
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .background(GreenBadgeBg, shape = RoundedCornerShape(20.dp))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Verified,
                            contentDescription = "Verified GI",
                            tint = ForestGreenGI,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Authentic GI Certified Product",
                            color = ForestGreenGI,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = product.productName,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )

                    if (!product.category.isNullOrBlank()) {
                        Text(
                            text = product.category,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Scanned Barcode badge
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .background(
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                shape = RoundedCornerShape(8.dp)
                            )
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.QrCode,
                            contentDescription = "Barcode",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Barcode: ${product.barcode}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            // ==========================================
            // SECTION 1: Fabric & Yarn Specifications
            // ==========================================
            DetailSectionCard(
                sectionNumber = 1,
                title = "Fabric & Yarn Composition",
                icon = Icons.Default.Texture,
                accentColor = TerracottaPrimary,
                items = listOf(
                    SectionItem("Fabric Composition", product.fabricComposition),
                    SectionItem("Yarn Spinning", product.yarnSpinning),
                    SectionItem("Fabric Weave", product.fabricWeave),
                    SectionItem("Embroidery Yarn", product.embroideryYarn)
                )
            )

            // ==========================================
            // SECTION 2: Product Specifications
            // ==========================================
            DetailSectionCard(
                sectionNumber = 2,
                title = "Design & Craft Specifications",
                icon = Icons.Default.Straighten,
                accentColor = GoldAccent,
                items = listOf(
                    SectionItem("Design", product.design),
                    SectionItem("Size", product.size),
                    SectionItem("Weight", product.weight),
                    SectionItem("Embroidery", product.embroidery)
                )
            )

            // ==========================================
            // SECTION 3: GI & Artisan Origin Details
            // ==========================================
            DetailSectionCard(
                sectionNumber = 3,
                title = "GI Registry & Artisan Traceability",
                icon = Icons.Default.Place,
                accentColor = ForestGreenGI,
                items = listOf(
                    SectionItem("Authorized GI User", product.authorizedGiUser),
                    SectionItem("Address of Authorized GI User", product.addressAuthorizedGiUser),
                    SectionItem("Artisan / Weaver", product.artisanOrWeaver),
                    SectionItem("Address of Artisan / Weaver", product.addressArtisanOrWeaver)
                )
            )
        }
    }
}
