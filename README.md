# Hidayah

An Android Kotlin/Compose MVVM starter with Home, Quran, Prayer Times and Tracker tabs. Prayer Times includes an estimated Ramadan schedule and Qibla compass. The Quran reader streams Arabic, Bengali translation, Bengali tafsir and recitation.

## Run

1. Open the project in Android Studio with JDK 17, Android SDK 35 and Build Tools 35.0.0. Sync Gradle and run on Android 8.0+ (API 26+).
2. **Prayer Times:** tap **Use my location**, allow location, and choose your local calculation method and Asr setting. Expand the Ramadan timetable.
3. **Quran:** connect to the internet, select any surah, and tap a verse's **Read বাংলা তাফসির** to load its commentary. Use **Play** to stream from the first verse, or **Play verse** to start at that verse and continue sequentially. Playback pauses when leaving the reader and the player is released on disposal.
4. Use a physical device with a rotation-vector sensor for the Qibla compass. A simulated location can test the schedule on an emulator.
5. Run `./gradlew testDebugUnitTest` or use Android Studio's test runner.

## Sources and content

- Surah metadata, Uthmani Arabic, Muhiuddin Khan Bengali translation (`bn.bengali`), and per-verse Mishary Alafasy audio (`ar.alafasy`) come from [Al Quran Cloud](https://alquran.cloud/api) and its [audio CDN](https://alquran.cloud/cdn). These are streamed/loaded online and are not bundled or cached for offline use.
- Bengali **Tafsir Ahsanul Bayaan**, resource ID 165, is requested on demand by verse from [Quran.com](https://api.quran.com/api/v4/resources/tafsirs?language=bn); grouped verses are identified when provided. HTML is rendered as plain text, not executed as web content. Errors are shown per verse and can be retried.
- Sehri displays the general pre-meal **Bismillah**, not a claimed special fasting formula (Sunan Abi Dawud 3767). Iftar displays the supplication reported in [Sunan Abi Dawud 2357](https://sunnah.com/abudawud:2357), with a Bengali meaning. Intention is in the heart; wording and translations should receive qualified review before production.
- Review the upstream providers' licensing, attribution and usage policies and arrange appropriate rights before public distribution. API availability, data accuracy and audio CDN availability depend on third parties.

## Accuracy and privacy limits

- **Sehri end = Fajr; Iftar = Maghrib**, from Adhan Java 1.2.1 using the selected method and location. Actual local schedules may use different methods or precautionary offsets. Verify all times with a trusted local authority; unavailable polar-day calculations display `—`.
- The current or next Ramadan is derived from Android's Umm al-Qura Hijri calendar. It is **an estimate** and can differ from local moon sightings and official dates. The timetable is not a religious ruling. It uses the device timezone, which must match the location, and a one-shot location fix that should be refreshed after travel.
- Location is requested only when tapped and is not saved or directly transmitted by the app. Android's network location provider may use system services. Requests for Quran, tafsir and audio go to the external services named above and reveal normal network metadata such as IP address.
- Qibla is geographic bearing corrected with magnetic declination. Sensor accuracy, approximate location and interference affect results; hold the phone flat, calibrate and verify independently.
- The tracker is local-only and date-based. There are no accounts, notifications, offline Quran/audio, background playback, accessibility audit, localization, or production scholarly review.

## Architecture

`data/PrayerCalculator.kt`, `RamadanSchedule.kt` and `DeviceLocationSource.kt` contain prayer, schedule and location logic. `CloudQuranRepository.kt` implements the Quran repository and validates/aligns verse editions. `ui/ViewModels.kt`, `ui/HomeTrackerViewModels.kt` and `ui/QuranViewModels.kt` expose screen state and actions. `ui/screens/` contains the Compose views, lifecycle-bound Qibla sensor and Media3/ExoPlayer. `HidayahApp.kt` owns navigation; `app/src/test/` covers calculation and tracker behavior. API failures surface with retry controls; data is loaded using IO dispatchers.

## Firebase Home dashboard setup

The project requires a locally supplied `app/google-services.json` for package `com.hidayah.app`. This file is intentionally excluded from the public repository; download it from your Firebase project settings and place it in `app/` before building. The original local configuration points to Firebase project `inweb-hidayah`. Client Firebase configuration is embedded in any APK built with it and is **not a secret**. Protect the project using Firebase rules, appropriate API key restrictions, and (for production) App Check. Never include service-account credentials in the app.

**Connection check:** a public read of the current date document succeeded during the latest implementation. The app cannot enable cloud APIs, create a database, change rules or publish content without project-owner access. Make sure a reviewed document exists for each device-local date.

1. [Enable the Cloud Firestore API](https://console.developers.google.com/apis/api/firestore.googleapis.com/overview?project=inweb-hidayah) for the project, and create a Cloud Firestore database in the Firebase console if one does not already exist.
2. Configure **read-only access** for published `daily_values` documents. `firestore.rules.example` is a narrow example that allows public single-document reads and denies client writes/listing. Review and merge it with existing rules before deploying; do not replace other production rules blindly. All published values will be publicly readable. Never put private user data in this collection.
3. In the console, create `daily_values/YYYY-MM-DD`, using the **device-local Gregorian date** for the desired day. Set these five required string fields, replacing the bracketed descriptions with reviewed content:
   - `title`: the daily value's title
   - `reflection`: a short reflection
   - `referenceType`: exactly `Hadith` or `Ayat`
   - `referenceText`: verified quotation or translation
   - `reference`: precise source/citation
   - `referenceArabic` (optional): verified Arabic text, displayed above the translation when present
4. Reopen Home. It listens for changes while visible and switches documents after the device-local date changes. Missing, incomplete, offline/cached and denied content have distinct states. No sample religious quotation is inserted automatically. Have religious content and translations reviewed before publishing.

This client does not write to Firestore. Publishing content and configuring cloud services are owner/admin tasks. Firestore client caching may retain previously read values on the device.

## Daily deeds and builds

The Amol Tracker stores five prayers, Quran reading and a good deed as date-keyed Room rows. The seven-day view includes today, and each day starts unchecked. Records survive app process restarts but are private to this installation: they do not sync, Android backup is disabled, and uninstalling or clearing data removes them. Dates follow the device timezone; changing it can change which date is considered today. The database has no migration yet because it is at version 1.

Builds use one worker, a 768 MB Gradle heap and in-process Kotlin compilation. Extended Material icons were replaced with lightweight vectors to avoid costly dexing. For a debug APK, use `./gradlew --no-daemon :app:assembleDebug`; output is `app/build/outputs/apk/debug/app-debug.apk`. `./gradlew testDebugUnitTest` runs JVM tests; `./gradlew connectedDebugAndroidTest` requires a connected device/emulator for the Room integration test. A debug APK is signed with the debug key and is **not** a production release; use a properly configured private release signing key for distribution.

### Build verification notes

The version 2026.0.1 debug APK was assembled successfully after resource-constrained retries. The latest completed JVM test run before the version update passed 29 tests; the test run after the version update was interrupted. The resulting debug APK passed ZIP and Android v2 signature checks. There was no Android device/emulator, so device, accessibility, audio, notification and Room instrumentation tests were not run. Cold builds in constrained environments may be slow; use the Gradle settings in this project and retry a failed task without cleaning. Debug signing keys are not included in the project. A differently signed debug APK requires uninstalling the previous app first, which deletes its local tracker data.

## Bengali interface and starter learning modules

The app uses the supplied Firebase config and reads the date-keyed `daily_values` document. A read of the current document succeeded during implementation; visibility still depends on the device date/timezone, network, document availability and Firestore rules. The four bottom tabs remain Home, Quran, Prayer Times and Amol. Home links to the new Values, Duas, Stories/Quiz and Settings screens.

The visual design uses an original generated mosque banner and bundled Noto Sans Bengali / Noto Naskh Arabic fonts under the SIL Open Font License (`NOTO-FONT-LICENSE.txt`). The settings screen persists light/dark theme, font scale and an optional approximately 8 pm daily reminder. Android may delay periodic work, and travel/timezone changes can shift a reminder. On Android 13+ notification permission is required. The reminder is not a prayer-time alarm. Duas use device Arabic text-to-speech when available; this is synthetic speech, not a verified reciter, and pronunciation may be inaccurate. Sharing includes source references.

Values, nine duas, five short story summaries and five quiz questions are a **small starter set**, not a complete religious library. Source references are displayed. They, the Bengali wording, and the Firestore content require qualified review before public release. The Quran and tafsir remain network-powered. The privacy section in the settings screen is an explanatory prototype and is not a complete privacy policy. The app is still not a production-ready release; real-device accessibility, theme, reminders, location, audio, text rendering and end-to-end testing remain necessary.


## Home dashboard — current implementation

Home observes `daily_values/YYYY-MM-DD`, based on the device-local date. It displays the verified title, reflection, reference text and citation; if the optional `referenceArabic` field is present, that text is shown separately using the Arabic font. Sharing includes the published content and its citation. Old documents without Arabic remain supported. No religious quotation is generated or inferred. Loading, missing, invalid, cached and error states are handled separately; cached data is not labelled as necessarily offline. Entries and translations still require qualified review, and an Android device should be used to verify layout, network behavior and share-sheet accessibility.

## বিষয়ভিত্তিক মূল্যবোধ

The values section has four categories and searchable Bengali titles, topic labels and verse references (Bengali or Western digits). Each category opens a detail screen; Arabic Uthmani text and the Muhiuddin Khan Bengali translation for its listed verses are fetched on demand from Al Quran Cloud. Each verse independently shows loading/error/retry, can be shared with its attribution, and links to its broader Quran.com context. There is no permanent offline verse cache. The category summaries are editorial introductions, not tafsir or religious rulings; sources and Bengali wording require qualified review before release.

## দোয়া ও জিকির

The Dua and Dhikr screen includes a small offline collection of nine sourced entries with Arabic, Bengali pronunciation, Bengali meaning and source links. Search and categories work offline; favorites are stored in local SharedPreferences and are deleted when the application data is cleared or the app is uninstalled. A device Arabic text-to-speech engine is used only when the user requests it; this is **synthetic speech, not a verified recording**, and may mispronounce the text. The engine is stopped on pause and released when leaving the screen. Sources open in the browser and need internet. Bengali wording, transliteration and contextual usage still require qualified editorial review before publication. Device tests are needed for TTS availability, lifecycle, accessibility, sharing and favorites persistence.

## শিক্ষণীয় গল্প ও কুইজ

The learning screen has five short Bengali **paraphrases**, with separately labelled reflections and Quran references; they are not verbatim Quran translations or complete stories. Story search and topic filters work offline. The five-question offline quiz locks each answer, explains the correct answer, calculates the score and offers a review with source links and a restart. Quiz state is saved with the screen across configuration changes when Android restores it, but is not a permanent history or synced account. Quran.com links open the first referenced verse and require a browser and internet; read onward for a range. The paraphrases, reflections and quiz answers require qualified religious/editorial review before publication. Device testing remains necessary for layout, accessibility, saved-state restoration and external links.

## Navigation, notifications and search

A top bar supplies the side menu, dark/light toggle, reminders status and search; the original four bottom tabs remain available. Settings is listed in the side menu. The bell opens the daily reminder status and links to its settings; this is **not a notification inbox**. Search is an offline index of app destinations, value-topic titles, duas and stories; it is not a full-text Quran or verse search. Search results open the relevant topic or filtered list. Dark mode uses the same locally persisted setting as the settings screen. Device testing is still needed for navigation, encoded Bengali queries, drawer gestures, screen readers, window insets, notification permission and small/large-font layouts.

## Versioning

The application version is `2026.0.1` (Android `versionName`). Its numeric `versionCode` is `20260001`, calculated as year × 10000 + minor × 100 + patch (minor and patch 0–99). Always increase `versionCode` for subsequent distributed builds, including any build reissued with the same display version. This is a versioning convention, not an automatic calendar update.

## App launcher icon

Hidayah has a green, cream and gold mosque-and-crescent launcher icon. The manifest references `@mipmap/ic_launcher` and `@mipmap/ic_launcher_round`; density-specific PNGs and adaptive icon layers are included, with a monochrome layer for themed icons on Android 13+ when supported and enabled by the launcher. `branding/hidayah-icon.png` is a high-resolution preview, and `branding/hidayah-adaptive-foreground.png` is the transparent adaptive foreground. Verify the icon on real launchers and masks before release; icon appearance and themed-icon support vary by device.
