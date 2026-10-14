package com.barbarians.global.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.barbarians.global.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeveloperGuideScreen(
    isSwahili: Boolean,
    onReturnToMenu: () -> Unit
) {
    Scaffold(
        containerColor = ArenaBlack,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isSwahili) "PAKUA & MWONGOZO WA SIMU" else "INSTALL & DOWNLOAD GUIDE",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.2.sp
                        )
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onReturnToMenu,
                        modifier = Modifier.testTag("dev_guide_back_button")
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Return",
                            tint = BarbarianGold
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = ArenaDarkGray,
                    titleContentColor = TextPrimary
                )
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // How to Install on Android Device Card
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = BarbarianDeepRed.copy(alpha = 0.35f)),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.5.dp, BarbarianGold),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = if (isSwahili) "📲 JINSI YA KUPAKUA NA KUSANIKISHA (INSTALL)" else "📲 HOW TO DOWNLOAD & INSTALL ON YOUR PHONE",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Black,
                                color = BarbarianGold,
                                letterSpacing = 0.5.sp
                            )
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (isSwahili) {
                                "Hatua za kuweka app hii kwenye simu yako ya Android moja kwa moja:\n\n" +
                                "1️⃣ Pakua Faili ya APK: Kwenye menyu ya juu ya AI Studio au GitHub, bonyeza 'Export Project' au pakua 'app-debug.apk' / 'app-release.apk'.\n\n" +
                                "2️⃣ Fungua Faili: Kwenye simu yako, fungua 'Downloads' au 'File Manager' kisha bonyeza faili la APK.\n\n" +
                                "3️⃣ Ruhusu Usakinishaji: Simu ikikuuliza, chagua 'Allow from this source' au ruhusu usakinishaji wa programu kutoka kwa chanzo hiki.\n\n" +
                                "4️⃣ Bofya 'INSTALL': Bonyeza Install na baada ya sekunde chache Barbarian Arena itakuwa tayari kucheza wakati wowote hata bila intaneti!"
                            } else {
                                "Steps to install and run this app on any Android device:\n\n" +
                                "1️⃣ Download APK File: Use the top settings menu in AI Studio to 'Export Project' or download 'app-debug.apk' / 'app-release.apk'.\n\n" +
                                "2️⃣ Open Downloaded File: On your Android phone, open your Downloads folder in Files app and tap the APK.\n\n" +
                                "3️⃣ Enable Unknown Sources: If prompted, enable 'Allow from this source' in your device settings.\n\n" +
                                "4️⃣ Tap 'INSTALL': Tap install and open Barbarian Arena. All game data is stored securely offline!"
                            },
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = TextPrimary,
                                lineHeight = 19.sp
                            )
                        )
                    }
                }
            }

            // APK Build & Signing Specifications
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = ArenaCardBg),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, ArenaBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = if (isSwahili) "MUUNDO WA APK NA TEKNOLOJIA" else "APK SPECS & NATIVE RUNTIME",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = BarbarianGold
                            )
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = if (isSwahili) {
                                "App hii imejengwa kwa Kotlin 2.2.10 na Jetpack Compose (M3) ya kisasa kabisa. Imesainiwa rasmi kwa ajili ya usakinishaji kwenye simu yoyote ya Android kuanzia toleo la 8.0 (API 26) hadi 16 (API 36)."
                            } else {
                                "Native Kotlin 2.2.10 with Jetpack Compose (Material 3). Pre-signed with debug keystore for instant side-loading across Android 8.0 (API 26) to 16 (API 36)."
                            },
                            style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Surface(
                            color = ArenaBlack,
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, ArenaBorder),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Package: com.barbarians.global\nAPK: app/build/outputs/apk/debug/app-debug.apk\nStatus: Pre-signed & Ready for Install",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 12.sp,
                                color = TextPrimary,
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    }
                }
            }

            // Offline & Cloud Sync
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = ArenaCardBg),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, ArenaBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = if (isSwahili) "HIFADHI YA NDANI (OFFLINE STORAGE)" else "OFFLINE LOCAL PERSISTENCE",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = HealthGreen
                            )
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = if (isSwahili) {
                                "Maendeleo yote ya mchezaji (Dhahabu, Viwango, Ushindi, Silaha) yanahifadhiwa kwenye kumbukumbu ya simu (SharedPreferences). Unaweza kucheza bila bando au intaneti bila kupoteza data yako."
                            } else {
                                "Player stats, gold, level, class, and weapons are saved locally on device. Play seamlessly offline without worrying about lost progress."
                            },
                            style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                        )
                    }
                }
            }

            // MIT License
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = ArenaCardBg),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, BarbarianCrimson.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "MIT LICENSE",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = BarbarianGold
                            )
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Copyright (c) 2026 Eliwiz efraim\n\nPermission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the \"Software\"), to deal in the Software without restriction.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = TextSecondary,
                                lineHeight = 18.sp
                            )
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}
