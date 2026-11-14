# Hidayah

An Android Kotlin/Compose MVVM starter with Home, Quran, Prayer Times and Tracker tabs. Prayer Times includes an estimated Ramadan schedule and Qibla compass. The Quran reader streams Arabic, Bengali translation, Bengali tafsir and recitation.

## রোডম্যাপ | Project roadmap

এই চেকলিস্ট সম্পূর্ণ অ্যাপ উন্নয়নের পরিকল্পনা; অসমাপ্ত ফিচারকে সম্পন্ন বলে বোঝায় না। ধাপগুলো একে একে বাস্তবায়ন, GitHub Actions-এ যাচাই এবং সংক্ষিপ্ত অগ্রগতি রিপোর্ট দেওয়া হবে। ধর্মীয় বিষয়বস্তু ও বাস্তব ডিভাইস পরীক্ষা প্রকাশের আগে গুরুত্বপূর্ণ।

**International contributors:** The roadmap below covers release readiness, contributions, sharing, offline/cloud images, update policies and device testing. Contribution instructions and licensing are planned; please check issue and pull request guidance when published.

### বর্তমান অবস্থা

- [x] Kotlin/Jetpack Compose অ্যাপের Home, Quran, Prayer Times ও Tracker চারটি মূল ট্যাব; Firestore দৈনিক কনটেন্ট ও স্থানীয় Room tracker যুক্ত।
- [x] দৈনিক হাদিস/আয়াত টেক্সট ও ছবিতে শেয়ার এবং প্রকাশিত উদ্ধৃতি ও সূত্রসহ PNG প্রিভিউ; ঐচ্ছিক অ্যাডমিন URL/ডিফল্ট ID ও keyword-ভিত্তিক fallback যুক্ত। উদ্ধৃতি AI দিয়ে তৈরি হয় না।
- [x] ২০টি bundled ব্যাকগ্রাউন্ড: ১৯টি পৃথক মূল দৃশ্য ও একটি ভ্যারিয়েন্ট। GitHub Actions-এ JVM টেস্ট ও debug APK বিল্ড সফল।
- [ ] বাস্তব Android ডিভাইস পরীক্ষা, পূর্ণ ধর্মীয় ও নিরাপত্তা পর্যালোচনা, production release এবং নিচের নতুন ফিচারগুলো এখনো বাকি। বর্তমানে প্রকাশিত GitHub Release নেই।

### ধাপ ১ — বিদ্যমান অ্যাপ যাচাই ও প্রকাশের প্রস্তুতি

- [ ] চারটি মূল ট্যাব বজায় রেখে Home, Firestore rules ও দৈনিক লেখা; কুরআনের ১১৪ সুরা, বাংলা অনুবাদ/তাফসির ও Media3 অডিও; নামাজ, কিবলা, রমজানের সেহরি/ইফতার এবং Room tracker পরীক্ষা ও প্রয়োজনীয় সংশোধন।
- [ ] দোয়া, গল্প/কুইজ, বাংলা ও আরবি লেখা, অনুবাদ এবং উদ্ধৃতি/সূত্র যোগ্য ব্যক্তির মাধ্যমে ধর্মীয় ও সম্পাদকীয় পর্যালোচনা; উৎসের লাইসেন্স ও ছবির ব্যবহারাধিকার যাচাই।
- [ ] প্রকৃত ডিভাইসে নেভিগেশন, অবস্থান, সেন্সর, রিমাইন্ডার, থিম, বড় ফন্ট, accessibility, শেয়ার ও ডেটা ধরে রাখা যাচাই। Firebase rules ও API restrictions পর্যালোচনা; privacy policy সম্পূর্ণ করা।

### ধাপ ২ — GitHub Release ও কন্ট্রিবিউশন

- [ ] মালিকের সঙ্গে সফটওয়্যার লাইসেন্স, ছবি/কনটেন্টের অধিকার, সহযোগিতার শর্ত, issue/PR পর্যালোচনার নিয়ম নির্ধারণ। Public repository মানেই স্বয়ংক্রিয় open-source লাইসেন্স নয়।
- [ ] দেশ-বিদেশের স্বেচ্ছাসেবী ডেভেলপারদের আমন্ত্রণ এবং বাংলা/ইংরেজি contribution নির্দেশনা, নিরাপত্তা ও আচরণবিধি প্রকাশ। GitHub Actions secrets ও workflow কেবল বিশ্বস্ত রক্ষণাবেক্ষকদের নিয়ন্ত্রণে রাখা।
- [ ] নিরাপদ release signing key, ধারাবাহিক versionCode, যাচাইকৃত APK, release notes ও ডাউনলোড ব্যবস্থা নির্ধারণ; মালিকের অনুমোদনে প্রথম [GitHub Release](https://github.com/iNWEB-Official/Hidayah/releases) প্রকাশ। Actions-এর debug artifact-কে প্রকাশিত Release বলা হবে না।
- [ ] বিদ্যমান debug ইনস্টলেশনের signing key ও নতুন release key ভিন্ন হলে সরাসরি আপডেট সম্ভব নয়—স্থানীয় ডেটা হারানোর ঝুঁকিসহ migration পরিকল্পনা ও ব্যবহারকারীকে সতর্ক করা।

### ধাপ ৩ — পরিচিতি ও দৈনিক কনটেন্ট শেয়ার

- [ ] সাইড মেনুর নিচে অ্যাপের নাম, কপিরাইট এবং ক্লিকযোগ্য `iNWEB-Official/Hidayah` রিপোজিটরি/কন্ট্রিবিউশন লিংক।
- [ ] ছবির নিচে মাঝখানে অ্যাপের পরিচিতি, কপিরাইট ও GitHub Release ডাউনলোড ঠিকানা; প্রকাশিত সম্পূর্ণ উদ্ধৃতি ও সঠিক সূত্র পরিষ্কার রাখা।
- [ ] টেক্সট শেয়ার ও ছবি প্রিভিউ, ব্যবহারকারীর অনুরোধে ডিভাইসে সেভ এবং সোশ্যাল শেয়ার সহজ করা। শেয়ার টেক্সটেও ডাউনলোড লিংক থাকবে: ছবিতে লেখা লিংক সাধারণত ক্লিকযোগ্য নয় এবং সব অ্যাপ শেয়ার টেক্সট গ্রহণ করে না। Android share sheet/অনুমতিতে অতিরিক্ত ধাপ লাগতে পারে।
- [ ] Firestore থেকে অ্যাডমিনের যাচাই করা লেখা ও ছবির নির্দেশ বজায় রাখা; বিদ্যমান [অ্যাডমিন গাইড](ADMIN-DAILY-HADITH-GUIDE.md) নতুন ফিচারের সঙ্গে হালনাগাদ করা।

### ধাপ ৪ — হালকা অফলাইন ও ১,০০০ ছবির সংগ্রহ

- [ ] সর্বমোট **১,০০০টি পৃথক মূল ছবি** ধাপে ধাপে তৈরি; বর্তমানে ১৯টি আছে, আরও ৯৮১টি প্রয়োজন। একবারে সর্বোচ্চ ৫টি তৈরি ও যাচাই; বিদ্যমান ভ্যারিয়েন্টটি পৃথক ছবি দিয়ে প্রতিস্থাপন।
- [ ] অ্যাপের ভেতরে সর্বোচ্চ **৪০–৫০টি** অল্প সাইজের ডিফল্ট ছবি; কম্প্রেশন, APK আকার, মান ও পাঠযোগ্যতা পরিমাপ। বাকি ছবি ক্লাউডে রাখা হবে।
- [ ] ফ্রি ব্যবহারসীমা, bandwidth, নির্ভরযোগ্যতা, অধিকার ও সম্ভাব্য খরচ দেখে ক্লাউড হোস্ট নির্বাচন; paid AI image API বা অনিচ্ছাকৃত billing নয়।
- [ ] বিষয় ও ID-ভিত্তিক ক্যাটালগ, অ্যাডমিন নির্বাচন, আগের উপযুক্ত ছবি ও keyword-based fallback। অর্থ বোঝার ক্ষেত্রে keyword matching-এর সীমাবদ্ধতা স্পষ্ট রাখা।
- [ ] অনলাইনে প্রয়োজনীয় ছবি ডাউনলোড করে app-private storage-এ রাখা: পটভূমিগুলো সাধারণ গ্যালারিতে দেখা যাবে না এবং সংরক্ষিত ছবি অফলাইনে চলবে। ব্যবহারকারী চাইলে কেবল চূড়ান্ত শেয়ার ছবি দৃশ্যমান স্থানে সেভ করতে পারবেন।
- [ ] নেটওয়ার্ক ব্যর্থতা, সীমিত ডেটা, URL ও ফাইল নিরাপত্তা, private storage-এর আকারসীমা/পরিষ্কারের নিয়ম এবং bundled fallback বাস্তবায়ন ও পরীক্ষা।

### ধাপ ৫ — ভার্সন নীতি ও আপডেট ব্যবস্থা

- [ ] `বছর.মেজর.ছোট` নীতি প্রয়োগ: ছোট পরিবর্তনে শেষের সংখ্যা বাড়বে; `2026.0.99`-এর পরের ছোট পরিবর্তন `2026.1.0`। বড় পরিবর্তনেও মেজর বাড়বে ও ছোট সংখ্যা ০ হবে। `versionCode = বছর × 10000 + মেজর × 100 + ছোট`; মেজর ও ছোট উভয় ০–৯৯ এবং প্রতিটি বিতরণযোগ্য নতুন বিল্ডে আগের চেয়ে বেশি code।
- [ ] বছর বদলালে মেজর ও ছোট সংখ্যা বহাল থাকবে, নাকি reset হবে—প্রোজেক্ট মালিকের সিদ্ধান্ত নিয়ে নথিভুক্ত করা। release tag, versionName ও versionCode-এর সামঞ্জস্য যাচাই।
- [ ] স্থিতিশীল GitHub Release থেকে নির্ভরযোগ্য versionCode, release ক্রম এবং সংক্ষিপ্ত release notes সংগ্রহ; draft/pre-release বাদ। ইনস্টল করা সংস্করণ ও প্রকাশিত সংস্করণ তুলনা করে পপআপ ও settings-এ ম্যানুয়াল চেক যোগ।
- [ ] ইনস্টল করা সংস্করণের পরের **তিনটি স্থিতিশীল Release** পর্যন্ত পূর্ণ ব্যবহারসহ আপডেট পেছানোর সুযোগ। চতুর্থ নতুন Release প্রকাশিত হলে পুরোনো সংস্করণ **সীমিত মোডে** যাবে; হিসাব versionCode-এর সংখ্যাগত পার্থক্য নয়, বাস্তবে প্রকাশিত স্থিতিশীল Release-এর ক্রমের ভিত্তিতে হবে।
- [ ] সীমিত মোডে নামাজের সময় ও কুরআন পড়ার মতো জরুরি সুবিধা বজায় রাখা; অন্য সুবিধার জন্য আপডেট প্রয়োজন। নির্দিষ্ট সীমিত ফিচারের তালিকা এবং অফলাইন grace period-এর মেয়াদ মালিকের সঙ্গে পরে নির্ধারণ। কুরআন বর্তমানে অনলাইন API-নির্ভর—অফলাইনে পাঠের নিশ্চয়তা দেওয়া যাবে না যদি না আলাদা ব্যবস্থা করা হয়।
- [ ] অস্থায়ী নেটওয়ার্ক/API ব্যর্থতা, rate limit, cache, খালি/ভুল Release metadata, প্রথম ইনস্টল এবং অফলাইনে grace period পরীক্ষা; ভুল করে ব্যবহারকারীকে সীমিত মোডে পাঠানো এড়ানো। client-side সীমাবদ্ধতা পরিবর্তিত অ্যাপে সম্পূর্ণ অনতিক্রম্য নয়।
- [ ] পপআপের আপডেট বাটন থেকে যাচাইকৃত GitHub APK ডাউনলোড ও Android installer চালু করা; HTTPS, বিশ্বস্ত উৎস, package identity, signing certificate, আকার ও ব্যর্থতা যাচাই। সাধারণ GitHub-বিতরণ Android অ্যাপে নীরব install নিশ্চিত করা যায় না: Android-এর অনুমতি/নিশ্চিতকরণ লাগতে পারে। Play Store-এ গেলে উপযুক্ত in-app update পদ্ধতি পর্যালোচনা।

### ধাপ ৬ — চূড়ান্ত যাচাই ও ধারাবাহিক উন্নয়ন

- [ ] প্রতিটি বাস্তবায়ন ধাপে GitHub Actions-এ JVM টেস্ট ও APK build; ডিভাইসে install/update, একই signing key-এ upgrade ও পুরোনো লোকাল ডেটা অক্ষত থাকার পরীক্ষা। লোকাল build/test নয়।
- [ ] প্রকৃত ডিভাইসে গ্যালারি গোপনীয়তা, অফলাইন ছবি, বাংলা/আরবি, ছবি সেভ/শেয়ার, update prompt, চতুর্থ Release, সীমিত মোড ও grace period পরীক্ষা।
- [ ] সংশোধন ও যোগ্য পর্যালোচনা সম্পন্ন করে পর্যায়ক্রমে release notes এবং নতুন versionCode-সহ বিতরণ; ব্যবহারকারীর মতামত ও কমিউনিটি অবদান পর্যালোচনা।


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
- The tracker is local-only and date-based. There are no accounts, offline Quran/audio, background playback, completed accessibility audit, full localization, or production scholarly review. An optional approximate daily reminder exists; it is not a prayer-time notification.

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

The current application version is `2026.0.1` (`versionName`) and `versionCode` is `20260001`. The planned format is year.major.small: increase the small number for each small change, and roll `2026.0.99` to `2026.1.0` for the next; major changes also increment major and reset small to zero. The numeric `versionCode` formula is year × 10000 + major × 100 + small, with major and small each 0–99. Each newly distributed build must have a larger `versionCode`; a build reissued with the same display version still needs a larger code, so plan the next public version to avoid formula collisions. The year-transition reset/carry rule is not yet decided. This convention is not an automatic calendar update.

## App launcher icon

Hidayah has a green, cream and gold mosque-and-crescent launcher icon. The manifest references `@mipmap/ic_launcher` and `@mipmap/ic_launcher_round`; density-specific PNGs and adaptive icon layers are included, with a monochrome layer for themed icons on Android 13+ when supported and enabled by the launcher. `branding/hidayah-icon.png` is a high-resolution preview, and `branding/hidayah-adaptive-foreground.png` is the transparent adaptive foreground. Verify the icon on real launchers and masks before release; icon appearance and themed-icon support vary by device.

## দৈনিক কনটেন্টের ছবি শেয়ার

Daily values can be shared as plain text or as a locally rendered PNG with the original published quotation and citation. An optional admin-provided direct HTTPS `shareBackgroundUrl` takes precedence over an optional bundled `shareBackgroundId`. If a remote image cannot load, the app falls back to the selected/keyword-matched bundled background. The 20 bundled backgrounds currently include nineteen distinct AI scenes and one variant; keyword matching is heuristic, not AI-generated interpretation. There is no runtime AI image generation or paid AI API call. See [অ্যাডমিন গাইড](ADMIN-DAILY-HADITH-GUIDE.md) for fields, IDs, image requirements, privacy and limitations. Actual device sharing and typography must be tested.
