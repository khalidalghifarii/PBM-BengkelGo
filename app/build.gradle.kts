plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.example.bengkelgo"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.bengkelgo"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    // Tambahkan Firebase Bill of Materials (BoM)
    // Ganti "33.1.0" dengan versi BoM Firebase terbaru.
    // Anda bisa cek versi terbaru di: https://firebase.google.com/docs/android/setup#available-libraries
    implementation(platform("com.google.firebase:firebase-bom:33.14.0"))

    // Library Firebase sekarang bisa ditambahkan tanpa menyebutkan versi,
    // karena versinya akan dikelola oleh BoM.
    implementation("com.google.firebase:firebase-firestore-ktx") // Sebelumnya: implementation("com.google.firebase:firebase-firestore-ktx:25.1.4")
    implementation("com.google.firebase:firebase-auth-ktx")     // Menggantikan libs.firebase.auth jika ingin dikelola BoM, atau pastikan libs.firebase.auth tidak menyertakan versi.

    // Dependensi Anda yang lain
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    // implementation(libs.firebase.auth) // Komentari atau hapus ini jika Anda menggunakan deklarasi firebase-auth-ktx di atas
    implementation(libs.androidx.credentials)
    implementation(libs.androidx.credentials.play.services.auth)
    implementation(libs.googleid)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}