# হিদায়াহ | Hidayah

হিদায়াহ একটি ইসলামিক Android অ্যাপ, যেখানে কুরআন, নামাজের সময় এবং দৈনন্দিন আমল এক জায়গায় পাওয়ার ব্যবস্থা রয়েছে।

Hidayah is an Islamic Android app bringing Quran, prayer times and everyday worship tools together.

## অ্যাপে যা রয়েছে | Features

- কুরআনের আরবি পাঠ, বাংলা অনুবাদ, তাফসির ও তিলাওয়াত / Quran reading, Bengali translation, tafsir and recitation
- নামাজের সময়, কিবলা এবং রমজানের সূচি / Prayer times, Qibla and Ramadan schedule
- দৈনন্দিন আমল tracker, দোয়া ও শিক্ষণীয় বিষয় / Daily worship tracker, duas and learning content
- দৈনিক আয়াত ও হাদিস দেখার এবং শেয়ার করার সুবিধা / Daily verses and Hadith with sharing

Home অনলাইনে Al Quran Cloud থেকে একটি দৈবচয়িত আয়াত দেখায়; উৎস অনুপলব্ধ হলে বিদ্যমান Firestore দৈনিক কনটেন্ট fallback হিসেবে ব্যবহৃত হয়। একই অ্যাপ প্রক্রিয়ায় দিনের বাকি সময়ে আয়াতটি পুনরায় ব্যবহার করা হয়; ব্যবহারকারী বা নতুন অ্যাপ প্রক্রিয়ায় ভিন্ন হতে পারে। বাংলা অনুবাদ মুহিউদ্দীন খানের; ধর্মীয় ও ডিভাইস যাচাই এখনও প্রয়োজন।

Home shows a randomly selected verse from Al Quran Cloud when available and falls back to existing Firestore daily content on source failure. The selection is reused during the same local day within one app process; it may vary between users or after an app restart. Bengali translation: Muhiuddin Khan. Religious and device review remain necessary.

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
