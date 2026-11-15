import java.io.ByteArrayInputStream
import java.net.URL
import java.security.MessageDigest
import java.util.zip.ZipInputStream

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
    id("org.jetbrains.kotlin.kapt")
    id("com.google.gms.google-services")
}


// Public assets are archived separately. Pin and verify the exact archive before use.
val hidayahResourceCommit = "fb122bd7670e77b0f10fdb2c2c6b6052975d0b0e"
val hidayahResourceArchiveSha256 = "77f6f9d37080159f2251228542262a74ccd26a0dc8c9e6b3e8084be8d48fcfc6"
val hidayahResourceDirectory = layout.buildDirectory.dir("generated/hidayahResources")
val hidayahLicenseDirectory = layout.buildDirectory.dir("generated/hidayahLicenseAssets")
val prepareHidayahResources by tasks.registering {
    outputs.dir(hidayahResourceDirectory)
    outputs.dir(hidayahLicenseDirectory)
    doLast {
        val url = URL("https://codeload.github.com/iNWEB-Official/iNWEB-Official-Public-Resources/zip/$hidayahResourceCommit")
        val connection = url.openConnection().apply {
            connectTimeout = 30000
            readTimeout = 120000
        }
        val archive = connection.getInputStream().use { it.readBytes() }
        val digest = MessageDigest.getInstance("SHA-256").digest(archive)
            .joinToString("") { "%02x".format(it.toInt() and 0xff) }
        check(digest == hidayahResourceArchiveSha256) { "Hidayah resource archive checksum mismatch" }
        val outputDir = hidayahResourceDirectory.get().asFile
        val licenseDir = hidayahLicenseDirectory.get().asFile
        project.delete(outputDir, licenseDir)
        check(outputDir.mkdirs() || outputDir.isDirectory) { "Cannot create resource output directory" }
        val archiveRoot = "iNWEB-Official-Public-Resources-$hidayahResourceCommit/Hidayah/"
        val prefix = "${archiveRoot}app/src/main/res/"
        var extracted = 0
        var licenseExtracted = false
        ZipInputStream(ByteArrayInputStream(archive)).use { zip ->
            while (true) {
                val entry = zip.nextEntry ?: break
                if (!entry.isDirectory && entry.name.startsWith(prefix)) {
                    val relative = entry.name.removePrefix(prefix)
                    val target = outputDir.resolve(relative).canonicalFile
                    check(target.toPath().startsWith(outputDir.canonicalFile.toPath()) && target != outputDir.canonicalFile) {
                        "Invalid resource archive path"
                    }
                    check(target.parentFile.mkdirs() || target.parentFile.isDirectory)
                    target.outputStream().use { zip.copyTo(it) }
                    extracted++
                } else if (!entry.isDirectory && entry.name == "${archiveRoot}NOTO-FONT-LICENSE.txt") {
                    val license = licenseDir.resolve("licenses/NOTO-FONT-LICENSE.txt")
                    check(license.parentFile.mkdirs() || license.parentFile.isDirectory)
                    license.outputStream().use { zip.copyTo(it) }
                    licenseExtracted = true
                }
                zip.closeEntry()
            }
        }
        check(extracted == 43) { "Unexpected Hidayah resource count: $extracted" }
        check(licenseExtracted) { "Noto font license missing" }
    }
}

android {
    sourceSets.getByName("main").res.srcDir(hidayahResourceDirectory)
    sourceSets.getByName("main").assets.srcDir(hidayahLicenseDirectory)
    namespace = "com.hidayah.app"
    compileSdk = 35
    buildToolsVersion = "35.0.0"

    defaultConfig {
        applicationId = "com.hidayah.app"
        minSdk = 26
        targetSdk = 35
        // year * 10000 + minor * 100 + patch; increment for every distributed build.
        versionCode = 20260001
        versionName = "2026.0.1"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions { jvmTarget = "17" }
    buildFeatures { compose = true }
}

dependencies {
    val composeBom = platform("androidx.compose:compose-bom:2024.12.01")
    implementation(composeBom)
    androidTestImplementation(composeBom)
    implementation("androidx.core:core-ktx:1.15.0")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.batoulapps.adhan:adhan:1.2.1")
    implementation("androidx.media3:media3-exoplayer:1.5.1")
    implementation(platform("com.google.firebase:firebase-bom:33.7.0"))
    implementation("com.google.firebase:firebase-firestore")
    implementation("com.google.firebase:firebase-auth")
    implementation("androidx.work:work-runtime-ktx:2.10.0")
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    kapt("androidx.room:room-compiler:2.6.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.9.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.9.0")
    implementation("androidx.activity:activity-compose:1.9.3")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-core")
    implementation("androidx.navigation:navigation-compose:2.8.5")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.7")
    debugImplementation("androidx.compose.ui:ui-tooling")
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.9.0")
    androidTestImplementation("androidx.test:runner:1.6.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.room:room-testing:2.6.1")
}

tasks.named("preBuild").configure { dependsOn(prepareHidayahResources) }
