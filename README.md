# হিদায়াহ | Hidayah

হিদায়াহ একটি ইসলামিক Android অ্যাপ, যেখানে কুরআন, নামাজের সময় এবং দৈনন্দিন আমল এক জায়গায় পাওয়ার ব্যবস্থা রয়েছে।

Hidayah is an Islamic Android app bringing Quran, prayer times and everyday worship tools together.

## অ্যাপে যা রয়েছে | Features

- কুরআনের আরবি পাঠ, বাংলা অনুবাদ, তাফসির ও তিলাওয়াত / Quran reading, Bengali translation, tafsir and recitation
- নামাজের সময়, কিবলা এবং রমজানের সূচি / Prayer times, Qibla and Ramadan schedule
- দৈনন্দিন আমল tracker, দোয়া ও শিক্ষণীয় বিষয় / Daily worship tracker, duas and learning content
- দৈনিক আয়াত ও হাদিস দেখার এবং শেয়ার করার সুবিধা / Daily verses and Hadith with sharing

আমল tracker-এর তথ্য ডিভাইসের local database-এ রাখা হয়। তথ্য পড়তে সমস্যা হলে অগ্রগতি দেখানোর বদলে পুনরায় চেষ্টা করার সুযোগ থাকে। বাস্তব ফোনে persistence ও দিন বদল যাচাই এখনও বাকি; uninstall বা data clear করলে local তথ্য হারাতে পারে।

The tracker stores progress in a local database. If reading fails, it offers a retry rather than presenting progress as empty. Persistence and day changes still need device testing; uninstalling or clearing app data can remove local records.

দোয়ার পছন্দ ও অ্যাপের কিছু সেটিংস ডিভাইসে রাখা হয়; পড়া বা সংরক্ষণে সমস্যা হলে সতর্কবার্তা দেখানোর ব্যবস্থা আছে। বাস্তব ফোনে সংরক্ষণ ও পুনরায় খোলা পরীক্ষা বাকি।

Dua favorites and some app settings are stored locally, with warnings for detected read or write failures. Device persistence and recovery testing is pending.

নামাজের সময় অবস্থান, ফোনের সময় অঞ্চল ও নির্বাচিত হিসাব পদ্ধতির ওপর নির্ভর করে। অবস্থান বা অনুমতি না পাওয়া গেলে সময় দেখানো হয় না; স্থানীয় নির্ভরযোগ্য সময়সূচির সঙ্গে মিলিয়ে নিন। পদ্ধতি ও আসরের পছন্দ ডিভাইসে সংরক্ষণের ব্যবস্থা আছে; ফোনে পরীক্ষা বাকি।

Prayer times depend on location, the device time zone and the chosen calculation method. If location or permission is unavailable, times are not shown; check a trusted local timetable. Method and Asr choices are stored on the device; persistence and calculations need device testing.

কিবলার compass sensor ও রমজানের আনুমানিক সূচি স্থানীয়ভাবে যাচাই করা প্রয়োজন; কিছু সময় পাওয়া না গেলে তা ফাঁকা থাকে। দৈনিক reminder-এর প্রথম লক্ষ্য রাত ৮টা, পরে প্রায় ২৪ ঘণ্টার ব্যবধান—নির্দিষ্ট সময় বা delivery নিশ্চিত নয়।

The Qibla compass and estimated Ramadan timetable need device and local-authority verification; unavailable times remain blank. The daily reminder initially targets 8 pm and then runs roughly every 24 hours; exact timing and delivery are not guaranteed.

দৈনিক লেখার ছবি শেয়ারে শিরোনাম, ব্যাখ্যা, উপলব্ধ আরবি, বাংলা লেখা ও সূত্র রাখার ব্যবস্থা আছে; লেখা বেশি বড় হলে টেক্সট শেয়ারের পরামর্শ দেওয়া হয়। ছবির preview থেকে ব্যবহারকারী নিজে জায়গা বেছে সংরক্ষণের অনুরোধ করতে পারেন। অ্যাকাউন্ট, share ও save বাস্তব ফোনে এবং Firebase নিরাপত্তা আলাদাভাবে যাচাই প্রয়োজন।

Daily share images are designed to include the title, reflection, available Arabic, translation and citation; for oversized content, users are prompted to share as text instead. From the preview, users can request a save to a location chosen in the system document picker. Account, sharing and saving need device testing, and Firebase security requires separate review.

Home অনলাইনে Al Quran Cloud থেকে একটি দৈবচয়িত আয়াত দেখায়; উৎস অনুপলব্ধ হলে বিদ্যমান Firestore দৈনিক কনটেন্ট fallback হিসেবে ব্যবহৃত হয়। বিকল্প লেখা, অনুপস্থিত, অসম্পূর্ণ বা error অবস্থায় retry বাটনে আবার চেষ্টা করা যায়; ইন্টারনেট ফিরলেই Quran উৎস স্বয়ংক্রিয়ভাবে পুনরায় চেষ্টা করা হয় না। একই অ্যাপ প্রক্রিয়ায় দিনের বাকি সময়ে আয়াতটি পুনরায় ব্যবহার করা হয়; ব্যবহারকারী বা নতুন অ্যাপ প্রক্রিয়ায় ভিন্ন হতে পারে। বাংলা অনুবাদ মুহিউদ্দীন খানের; ধর্মীয় ও ডিভাইস যাচাই এখনও প্রয়োজন।

Home shows a randomly selected verse from Al Quran Cloud when available and falls back to existing Firestore daily content on source failure. In fallback, missing, invalid or error states, use the retry button to try again; the Quran source is not automatically retried merely because connectivity returns. The selection is reused during the same local day within one app process; it may vary between users or after an app restart. Bengali translation: Muhiuddin Khan. Service availability and rate limits apply; this is not a persistent offline cache. Religious and device review remain necessary.

কুরআন reader অনলাইন নির্ভর। Arabic, বাংলা ও অডিও edition-এর সূরা ও আয়াতের পরিচয় মেলে কি না যাচাই করা হয়। অডিও edition অনুপলব্ধ বা অবৈধ হলে আলাদাভাবে শুধু Arabic ও বাংলা পাঠ আনার চেষ্টা করা হয়; পাঠের উৎসও অনুপলব্ধ হলে error দেখায়। ফোনে ব্যবহার, ধর্মীয় নির্ভুলতা ও পৃথক কনটেন্টের ব্যবহারাধিকার পর্যালোচনা বাকি।

The Quran reader is online-only. It checks chapter and verse identities across Arabic, Bengali and audio editions. If the audio edition is unavailable or invalid, it tries to fetch the Arabic and Bengali text separately; it still needs a working text source. Device, religious accuracy and individual content-rights reviews are pending.

আরবি অডিওতে শুধু একটি আয়াত, নির্বাচিত আয়াত থেকে সূরার শেষ পর্যন্ত বা প্রথম থেকে পুরো সূরা শোনার নিয়ন্ত্রণ রয়েছে। এই পরিবর্তন বাস্তব ডিভাইসে যাচাই বাকি। বাংলা অনুবাদ ও তাফসির অডিও এখনও যুক্ত নয়; উপযুক্ত রেকর্ডিং ও ব্যবহারাধিকার যাচাই প্রয়োজন।

Arabic recitation controls support a single ayah, playback from a selected ayah to the end, or the whole surah. Buffering and playback errors have pause/retry controls; actual playback and audio-focus behavior still need device verification. Bengali translation and tafsir audio are not implemented; suitable recordings and rights remain to be verified.

অ্যাপটি উন্নয়নাধীন। নামাজের সময় ও ধর্মীয় তথ্য ব্যবহারের আগে স্থানীয় নির্ভরযোগ্য উৎসের সঙ্গে মিলিয়ে নিন। কিছু সুবিধার জন্য internet বা অবস্থান অনুমতি প্রয়োজন হতে পারে।

The app is under development. Please check prayer times and religious information against reliable local sources. Some features may require internet access or location permission.

## প্রয়োজনীয় লিংক | Project links

| বিষয় / Item | লিংক / Link | Access |
|---|---|---|
| অ্যাপের source code | [Hidayah source](https://github.com/iNWEB-Official/Hidayah/tree/main/app/src) | Public |
| Development নথির তালিকা | [Documentation index](https://github.com/iNWEB-Official/iNWEB-Official-Documentation/blob/main/Hidayah/INDEX.md) | Private |
| Development README | [Private README](https://github.com/iNWEB-Official/iNWEB-Official-Documentation/blob/main/Hidayah/README.md) | Private |
| সব প্রকাশ্য resources | [Hidayah resources](https://github.com/iNWEB-Official/iNWEB-Official-Public-Resources/tree/main/Hidayah) | Public |
| ছবি ও background | [Hadith images ও banner](https://github.com/iNWEB-Official/iNWEB-Official-Public-Resources/tree/main/Hidayah/app/src/main/res/drawable-nodpi) | Public |
| Logo ও branding | [Hidayah branding](https://github.com/iNWEB-Official/iNWEB-Official-Public-Resources/tree/main/Hidayah/branding) | Public |
| Popup design preview | [Popup design image](https://github.com/iNWEB-Official/iNWEB-Official-Public-Resources/blob/main/Hidayah/designs/support-popup/hidayah-popup-professional.png) | Public |
| Font ও license | [Fonts](https://github.com/iNWEB-Official/iNWEB-Official-Public-Resources/tree/main/Hidayah/app/src/main/res/font) · [Noto license](https://github.com/iNWEB-Official/iNWEB-Official-Public-Resources/blob/main/Hidayah/NOTO-FONT-LICENSE.txt) | Public |
| ব্যক্তিগত backup | [Hidayah Drive folder](https://drive.google.com/drive/folders/1vGat2kLPYfPCuOpfxfCQB3p9piMeFg_Q) | Restricted |

Private GitHub links require an account with repository access. The Drive backup requires the appropriate Google account and permissions. Links do not grant access or change sharing settings.

© 2026 iNWEB Official. All rights reserved.
