package com.example.craftadmin.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.craftadmin.data.model.ProductCreateRequest
import com.example.craftadmin.ui.components.BarcodeScannerDialog
import com.example.craftadmin.ui.theme.ForestGreen
import com.example.craftadmin.ui.theme.GoldAccent
import com.example.craftadmin.ui.theme.NavyPrimary
import com.example.craftadmin.ui.theme.Terracotta
import com.example.craftadmin.viewmodel.AdminViewModel
import com.example.craftadmin.viewmodel.RegisterState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterProductScreen(
    viewModel: AdminViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    val registerState by viewModel.registerState.collectAsState()

    var showScannerDialog by remember { mutableStateOf(false) }

    // Intercept Android System Navigation Back button
    BackHandler {
        if (showScannerDialog) {
            showScannerDialog = false
        } else {
            onBack()
        }
    }

    // Form fields
    var barcode by remember { mutableStateOf("") }
    var productName by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Handloom / GI Craft") }

    // Section 1
    var fabricComposition by remember { mutableStateOf("") }
    var yarnSpinning by remember { mutableStateOf("") }
    var fabricWeave by remember { mutableStateOf("") }
    var embroideryYarn by remember { mutableStateOf("") }

    // Section 2
    var design by remember { mutableStateOf("") }
    var size by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }
    var embroidery by remember { mutableStateOf("") }

    // Section 3
    var authorizedGiUser by remember { mutableStateOf("") }
    var addressAuthorizedGiUser by remember { mutableStateOf("") }
    var artisanOrWeaver by remember { mutableStateOf("") }
    var addressArtisanOrWeaver by remember { mutableStateOf("") }

    var validationError by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Register GI Craft Product", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = NavyPrimary,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },
        bottomBar = {
            Surface(shadowElevation = 8.dp) {
                Box(modifier = Modifier.padding(16.dp)) {
                    Button(
                        onClick = {
                            if (barcode.isBlank() || productName.isBlank() || fabricComposition.isBlank() || authorizedGiUser.isBlank()) {
                                validationError = "Please fill in all essential required fields (Barcode, Name, Fabric, GI User)."
                                return@Button
                            }
                            validationError = null
                            viewModel.registerProduct(
                                ProductCreateRequest(
                                    barcode = barcode.trim(),
                                    productName = productName.trim(),
                                    category = category.trim(),
                                    fabricComposition = fabricComposition.trim(),
                                    yarnSpinning = yarnSpinning.trim().ifBlank { "Traditional Hand Spinning" },
                                    fabricWeave = fabricWeave.trim().ifBlank { "Authentic Handloom Weave" },
                                    embroideryYarn = embroideryYarn.trim().ifBlank { "Natural Silk Thread" },
                                    design = design.trim().ifBlank { "Heritage GI Pattern" },
                                    size = size.trim().ifBlank { "Standard Size" },
                                    weight = weight.trim().ifBlank { "Standard Weight" },
                                    embroidery = embroidery.trim().ifBlank { "Handcrafted Needlework" },
                                    authorizedGiUser = authorizedGiUser.trim(),
                                    addressAuthorizedGiUser = addressAuthorizedGiUser.trim().ifBlank { "Official GI Registered Address" },
                                    artisanOrWeaver = artisanOrWeaver.trim().ifBlank { "Master Artisan" },
                                    addressArtisanOrWeaver = addressArtisanOrWeaver.trim().ifBlank { "Weaver Guild Address" }
                                )
                            )
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        enabled = registerState !is RegisterState.Submitting
                    ) {
                        if (registerState is RegisterState.Submitting) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Text("Registering in Database...")
                        } else {
                            Icon(Icons.Default.CloudUpload, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Save & Register Product", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
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
                .padding(16.dp)
        ) {
            // Header
            Text(
                text = "Product Details Registry",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            if (validationError != null) {
                Spacer(modifier = Modifier.height(10.dp))
                Surface(
                    color = MaterialTheme.colorScheme.errorContainer,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = validationError ?: "",
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.padding(10.dp),
                        fontSize = 13.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // ==========================================
            // IDENTIFIERS & BASIC INFO
            // ==========================================
            FormSectionCard(title = "Product Identification & Barcode", color = NavyPrimary) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = barcode,
                        onValueChange = { barcode = it },
                        label = { Text("Barcode Number *") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = { showScannerDialog = true },
                        modifier = Modifier
                            .background(NavyPrimary, RoundedCornerShape(8.dp))
                            .size(50.dp)
                    ) {
                        Icon(Icons.Default.QrCodeScanner, contentDescription = "Scan Barcode", tint = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = productName,
                    onValueChange = { productName = it },
                    label = { Text("Product Name *") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = category,
                    onValueChange = { category = it },
                    label = { Text("Category") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ==========================================
            // SECTION 1: Fabric & Yarn Specifications
            // ==========================================
            FormSectionCard(title = "Section 1: Fabric & Yarn Details", color = Terracotta) {
                OutlinedTextField(
                    value = fabricComposition,
                    onValueChange = { fabricComposition = it },
                    label = { Text("Fabric Composition * (e.g. 100% Changthangi Wool)") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = yarnSpinning,
                    onValueChange = { yarnSpinning = it },
                    label = { Text("Yarn Spinning (e.g. Hand Spun Charkha)") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = fabricWeave,
                    onValueChange = { fabricWeave = it },
                    label = { Text("Fabric Weave (e.g. Handloom Diamond Weave)") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = embroideryYarn,
                    onValueChange = { embroideryYarn = it },
                    label = { Text("Embroidery Yarn (e.g. Pure Mulberry Silk Thread)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ==========================================
            // SECTION 2: Product Specifications
            // ==========================================
            FormSectionCard(title = "Section 2: Product Specifications", color = GoldAccent) {
                OutlinedTextField(
                    value = design,
                    onValueChange = { design = it },
                    label = { Text("Design / Motif (e.g. Traditional Paisley motif)") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = size,
                    onValueChange = { size = it },
                    label = { Text("Size / Dimensions (e.g. 200 cm x 70 cm)") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = weight,
                    onValueChange = { weight = it },
                    label = { Text("Weight (e.g. 145 grams)") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = embroidery,
                    onValueChange = { embroidery = it },
                    label = { Text("Embroidery Technique (e.g. Sozni Needlework)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ==========================================
            // SECTION 3: GI Registry & Artisan Origin
            // ==========================================
            FormSectionCard(title = "Section 3: GI & Artisan Origin Details", color = ForestGreen) {
                OutlinedTextField(
                    value = authorizedGiUser,
                    onValueChange = { authorizedGiUser = it },
                    label = { Text("Authorized GI User & Reg No. *") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = addressAuthorizedGiUser,
                    onValueChange = { addressAuthorizedGiUser = it },
                    label = { Text("Address of Authorized GI User") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2
                )
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = artisanOrWeaver,
                    onValueChange = { artisanOrWeaver = it },
                    label = { Text("Artisan / Weaver Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = addressArtisanOrWeaver,
                    onValueChange = { addressArtisanOrWeaver = it },
                    label = { Text("Address of Artisan / Weaver") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2
                )
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }

    // Camera Barcode Scanner Dialog
    if (showScannerDialog) {
        BarcodeScannerDialog(
            onDismiss = { showScannerDialog = false },
            onBarcodeCaptured = { scannedCode ->
                barcode = scannedCode
                showScannerDialog = false
            }
        )
    }

    // Success / Error Alerts
    when (val state = registerState) {
        is RegisterState.Success -> {
            AlertDialog(
                onDismissRequest = { viewModel.resetRegisterState() },
                icon = { Icon(Icons.Default.CheckCircle, contentDescription = null, tint = ForestGreen) },
                title = { Text("Product Registered Successfully!", fontWeight = FontWeight.Bold) },
                text = {
                    Column {
                        Text("Product has been registered in the database:")
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("• Barcode: ${state.product.barcode}", fontWeight = FontWeight.SemiBold)
                        Text("• Name: ${state.product.productName}")
                        Text("• ID: #${state.product.id}")
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.resetRegisterState()
                            onBack()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary)
                    ) {
                        Text("Done")
                    }
                }
            )
        }
        is RegisterState.Error -> {
            AlertDialog(
                onDismissRequest = { viewModel.resetRegisterState() },
                icon = { Icon(Icons.Default.Error, contentDescription = null, tint = MaterialTheme.colorScheme.error) },
                title = { Text("Registration Failed", fontWeight = FontWeight.Bold) },
                text = { Text(state.message) },
                confirmButton = {
                    Button(onClick = { viewModel.resetRegisterState() }) {
                        Text("OK")
                    }
                }
            )
        }
        else -> Unit
    }
}

@Composable
fun FormSectionCard(
    title: String,
    color: Color,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.3f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Spacer(modifier = Modifier.height(12.dp))
            content()
        }
    }
}
