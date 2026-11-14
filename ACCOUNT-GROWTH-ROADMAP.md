# অ্যাকাউন্ট ও ফ্রি-প্রথম উন্নয়ন রোডম্যাপ

এই নথি পরিকল্পনা; `[ ]` কাজ এখনও বাস্তবায়িত বা যাচাইকৃত নয়। বর্তমান অ্যাপে ঐচ্ছিক email/password অ্যাকাউন্টের প্রাথমিক কোড ও ডিভাইস-স্থানীয় avatar আছে, কিন্তু social login, ছবি upload, profile cloud sync ও admin panel নেই। বাস্তব Firebase provider সেটিংস ও বিস্তারিত device verification বাকি। খরচ এড়াতে মালিকের অনুমতি ছাড়া billing/Blaze বা পেইড পরিষেবা চালু করা হবে না। Spark-এর অবস্থা Console-এ মালিকের যাচাই প্রয়োজন; শুধু এই নথি তা নিশ্চিত করে না।

## বর্তমানে উন্নয়নযোগ্য: ফ্রি-প্রথম পরিকল্পনা

- [ ] বিদ্যমান email/password login ও email verification মালিকের Firebase সেটিংসসহ যাচাই; শুধু email reset link ব্যবহার, numeric OTP নয়। Reset mail চাওয়া আর password বাস্তবে পরিবর্তন হওয়া এক জিনিস নয়।
- [ ] বর্তমানে Spark-এ প্রকল্প-সংশ্লিষ্ট password-reset **ইমেইল** সীমা ১৫০/দিন; অন্য নিরাপত্তা সীমাও থাকতে পারে। বিশ্বস্ত তথ্য ও সংশ্লিষ্ট timezone জানা থাকলে নির্বাচিত বাংলা/ইংরেজিতে যাচাইকৃত পরবর্তী চেষ্টা সময় দেখানো; এটি provider availability বা super-admin পুনরায় চালুর নিশ্চয়তা নয়। সময় জানা না থাকলে: `দুঃখিত, এই সুবিধাটি বর্তমানে ব্যবহার করা যাচ্ছে না। অনুগ্রহ করে কয়েক ঘণ্টা পর আবার চেষ্টা করুন। ধন্যবাদ।` / `Sorry, this feature is currently unavailable. Please try again in a few hours. Thank you.` এটি পরামর্শ, নির্দিষ্ট সময়ের নিশ্চয়তা নয়। অ্যাকাউন্ট আছে কি না প্রকাশ করে এমন বার্তা এড়ানো।
- [ ] প্রকল্পজুড়ে সর্বোচ্চ ১৫০টি নিয়ন্ত্রণের জন্য server-trusted, atomic counter, abuse protection, আর সব প্রবেশপথ ও provider quota বিবেচনায় নকশা যাচাই। শুধু অ্যাপের counter বদলে বা direct Firebase API দিয়ে এ নিয়ন্ত্রণ পাশ কাটানো সম্ভব; তাই কঠোর সীমা এখনও দাবি করা যাবে না। Firebase-এর reset quota কখন পুনরায় চালু হয় এবং কোন timezone প্রযোজ্য—যাচাই না হওয়া পর্যন্ত স্থানীয় রাত ১২টার পর নিশ্চিত কাজ করবে বা নির্দিষ্ট তারিখ দেখাবে এমন প্রতিশ্রুতি নয়। Firebase/provider quota-ই চূড়ান্ত।
- [ ] ভাষা নির্বাচন ও সংরক্ষণ: বাংলা ও ইংরেজি; account, limit/error, notification ও planned-feature বার্তা ধাপে ধাপে অনুবাদ ও accessibility যাচাই। পরবর্তী ভাষা চাহিদা অনুযায়ী।
- [ ] ফ্রি স্তরের শর্ত যাচাই করে Google ও Facebook login পর্যায়ক্রমে; provider config, consent, বিশ্বস্ত identity linking, প্রয়োজনীয় অনুমতি, rules এবং device test সম্পন্ন করার আগে কার্যকর নয়। সব সামাজিক provider-এর জন্য একই দৈনিক/মাসিক সীমা অনুমান না করে নির্দিষ্ট plan, provider, quota ও error যাচাই। Spark-এ সীমা শেষ হলে ব্যবহার বন্ধ বা সীমাবদ্ধ হতে পারে; কোনো paid overage অনুমোদিত নয়।
- [ ] বর্তমান Firebase Auth ও Identity Platform upgrade-এর পার্থক্য যাচাই: Identity Platform-এর Spark-এ সাধারণ provider-এর জন্য ৩,০০০ daily active users (Tier 1) সীমা প্রযোজ্য হতে পারে; এটি মোট নিবন্ধিত অ্যাকাউন্ট নয় এবং সব Firebase Auth প্রকল্পে একই সীমা ধরে নেওয়া যাবে না। TOTP MFA ফ্রি ও নিরাপদে সম্ভব কি না যাচাই; না হলে বন্ধ রাখা। Login MFA ও অ্যাপ-প্রবেশ lock আলাদা বিষয়।
- [ ] Privacy policy, account deletion, অপব্যবহার প্রতিরোধ এবং backend-enforced RBAC আগে; ইমেইল/ফোনের default `only me`। Public/friends/only-me নিয়ন্ত্রণ backend rules দিয়ে কার্যকর না হলে shared profiles প্রকাশ নয়; friends-only-র জন্য বিশ্বস্ত friend graph প্রয়োজন। শুধু UI-তে admin লেখা নিরাপত্তা নয়।
- [ ] Firebase Auth UID-এর সঙ্গে profile schema; server-trusted atomic sequential ID অন্তত ৬ অঙ্ক ও প্রয়োজনমতো বড় হবে; নাম ও সংখ্যাভিত্তিক `@username`, সম্পাদনা ও atomic uniqueness। সম্ভবপর বিনামূল্যের trusted architecture ও দুষ্কৃতকারীর সরাসরি API প্রতিরোধ যাচাই না হওয়া পর্যন্ত অনিরাপদ আনুমানিক ID নয়।
- [ ] Built-in avatar ও cover থেকে নির্বাচন; cover-এর জন্য নিজস্ব ছবি upload নয়। Profile ছবি upload-এর পরিকল্পনা: aspect ratio/দৈর্ঘ্য সীমা, orientation, metadata ও sensitive location সরানো, নির্ভরযোগ্য decode, সংকোচন ও server-side যাচাইসহ লক্ষ্য **১০০–২০০ KB**, সর্বোচ্চ ২০০ KB; সুন্দর ছবি ১০০ KB-এর কম হলেও অযথা ফাইল বড় না করা প্রস্তাব। ব্যর্থ হলে ব্যবহারকারীকে জানানো। এটি এখনও তৈরি নয়।
- [ ] Profile ছবির নিরাপদ storage সমাধান ও lifecycle (access, replace, delete, quota, backup) যাচাই। **Firebase Storage-এ ৩ ফেব্রুয়ারি ২০২৬ থেকে Blaze আবশ্যক**, তাই no-billing শর্তে সেটি এখন চালু করা যাবে না। Firestore-এর ১ GiB quota ও ১ MiB document limit আছে; binary image নকশা করতে index/transfer/reads ও বাস্তব সীমা যাচাই দরকার, এটিকে ১৫ GB ধরে নেওয়া যাবে না। ১৫,০০০ × ১০০ KB ≈ ১.৫ GB শুধু ছবির bytes, অর্থাৎ Firestore-এর ১ GiB-এর চেয়ে বেশি; ২০০ KB হলে ≈ ৩ GB, metadata/overhead বাদে। আপাতত local avatar; নির্ভরযোগ্য free backend পাওয়া বা মালিক policy বদলানো পর্যন্ত cloud upload বন্ধ।
- [ ] মালিকের ভবিষ্যৎ পৃথক Gmail-ভিত্তিক Google Drive প্রথমে সীমিত **prototype**: Gmail, Drive ও Photos-এর মোট shared storage সাধারণত ১৫ GB; এটি Firebase Storage/Firestore-এর ১৫ GB নয়। API quota, download/traffic সীমা, uptime, consent, নিরাপদ ownership/access, quota monitoring ও export যাচাই। Drive-কে CDN বা নিরাপদ বহু-user profile storage হিসেবে আগে থেকে নিশ্চিত বলা যাবে না; অ্যাপে privileged credentials নয়। মালিককে এখানে Gmail password বা service-account key দিতে বলা হবে না।
- [ ] ব্যবহার পরিমাপ: Firebase Console-এর actual quota/usage, নিবন্ধিত ও দৈনিক/মাসিক সক্রিয় user, storage, traffic, email এবং abuse পর্যবেক্ষণ; সত্যিকারের নিরাপদ admin dashboard ও ক্ষমতা-ভিত্তিক access তৈরি হলে সেখানে সীমা দেখানো। Admin panel এখনও নেই। Quota কাছাকাছি এলে প্রয়োজনমতো ফিচার বন্ধ/ধীর করে নিরাপদ সীমিত অভিজ্ঞতা।
- [ ] বিনামূল্যের FCM push feasibility যাচাই: FCM নিজে no-cost, কিন্তু sender/automation/server ও অন্য পরিষেবায় আলাদা খরচ হতে পারে। নিরাপদ trusted sender; secret APK-তে নয়। বিদ্যমান local reminder-এর সঙ্গে remote push আলাদা; permission, পছন্দ, rate, delivery ও platform সীমা পরীক্ষা করতে হবে।

## সুপার-অ্যাডমিন: ফ্রি quota ও ফিচার নিয়ন্ত্রণ (পরিকল্পনা)

- [ ] প্রতিটি প্রযোজ্য পরিষেবার জন্য আলাদা কার্ড: provider ও plan, সর্বোচ্চ free quota, ব্যবহৃত পরিমাণ, বাকি পরিমাণ, unit, সময়কাল, reset সময়/স্থানীয় timezone (নিশ্চিত জানা থাকলে), তথ্যের উৎস ও সর্বশেষ হালনাগাদ। উদাহরণ: reset **ইমেইল** ১৫০/দিন; Firestore read/write/delete/egress/storage আলাদা; Drive storage আলাদা। যেখানে provider প্রকৃত তথ্য দেয় না বা ডেটা পুরোনো, সেখানে `অজানা`/`আনুমানিক` দেখাতে হবে—০ বা নিশ্চিত অবশিষ্ট দেখানো যাবে না।
- [ ] নির্ভরযোগ্য provider quota/usage API বা অনুমোদিত মাধ্যম, free quota ও বিলিং যাচাই করে তবেই সংখ্যা দেখানো; সব provider একই API, real-time তথ্য বা একই ধরনের সীমা দেয়—এমন অনুমান নয়। App-side গণনা হলে provider console-এর সংখ্যা থেকে পার্থক্য ও delay স্পষ্ট করা; ব্যবহৃত ও বাকি মানের `আনুমানিক` অবস্থা দেখানো। API/পর্যবেক্ষণ নিজেও খরচ বা quota ব্যবহার করতে পারে, আগে যাচাই।
- [ ] কেবল বিশ্বস্তভাবে নিয়ন্ত্রণযোগ্য প্রতিটি option-এর জন্য সুপার-অ্যাডমিনের off/on switch, নিরাপদ warning threshold ও পরিবর্তনের audit history। বন্ধ করলে নতুন অনুমোদিত কাজ থামবে; চলমান requests, বিলম্বিত counters ও provider-side ব্যবহার সম্পূর্ণ বাতিলের নিশ্চয়তা নয়। পূর্বে সঞ্চিত storage বা background resource switch off করলেই মুছে যায় বা খরচ শূন্য হয় না। বন্ধ feature-এর জন্য বাংলা/ইংরেজিতে ব্যবহারযোগ্য বার্তা এবং নিরাপদ fallback প্রয়োজন।
- [ ] নতুন দৈনিক/মাসিক quota window provider অনুযায়ী শুরু হওয়ার পর সুপার-অ্যাডমিন পুনরায় চালু করতে পারবেন; switch চালু করলেই provider-এর quota reset হয় না। প্রকৃত storage দৈনিক শূন্য হয় না—ফাঁকা স্থান জানতে সংরক্ষিত ডেটা/সীমা দেখতে হবে। অজানা reset সময় নিয়ে নির্দিষ্ট সময়ের প্রতিশ্রুতি নয়। প্রয়োজনীয় লগইন ও জরুরি administrative access ভুল করে বন্ধ না করার নকশা দরকার।
- [ ] quota/control section, প্রকৃত ব্যবহার তথ্য ও পরিবর্তনের API কেবল trusted backend-enforced `super-admin` role-এ সীমাবদ্ধ; সাধারণ admin, editor ও user দেখতে বা পরিবর্তন করতে পারবেন না। UI লুকানোই নিরাপত্তা নয়: Firestore/security rules, server authorization, trusted role assignment ও audit প্রয়োজন; ক্লায়েন্টে privileged key রাখা যাবে না। বিদ্যমান token role display বা local counter এ অধিকার দেয় না।
- [ ] নিয়ন্ত্রণযোগ্য সমস্ত প্রবেশপথে trusted enforcement, atomic সীমা ও fail-closed পদ্ধতি যাচাই। সরাসরি provider API, অন্য ক্লায়েন্ট/কনসোল, প্রশাসকের manual ব্যবহার বা provider quota-delay bypass করতে পারলে dashboard switch পূর্ণ hard cap নয়—এমন ক্ষেত্রে ব্যয়-ঝুঁকিপূর্ণ feature বন্ধ থাকবে। অপারেশন আগেই শুরু হলে সামান্য overshoot হতে পারে; budget alert নিজেও hard cap নয়।
- [ ] no-billing/Spark শর্ত মালিকের মাধ্যমে যাচাই করে বজায় রাখা; পেইড billing ছাড়াই পরিকল্পনা সম্ভব না হলে feature অকার্যকর থাকবে। শুধু dashboard, threshold বা switch দিয়ে শূন্য খরচের নিশ্চয়তা দাবি নয়। নিরাপদ admin dashboard, provider integration, live quota ও switches এখনও তৈরি/পরীক্ষিত নয়।

## 🔴 শীঘ্রই আসছে: ভবিষ্যৎ কাজ

অকার্যকর অপশনের জন্য ভবিষ্যতে accessible লাল `🔴 শীঘ্রই আসছে` / `🔴 Coming soon` label দেখানোর পরিকল্পনা; রং একমাত্র সংকেত হবে না। এটি বর্তমানে যোগ করা হয়নি। নিরাপত্তা যাচাই ছাড়া মিথ্যা কার্যকর বোতাম বা কাজ করছে এমন MFA/সীমার দাবি নয়।

### নিজস্ব সার্ভারের লক্ষ্য

- [ ] অ্যাপের ইউজার সংখ্যা **১৫,০০০+** হলে মালিকের অনুমোদন, বাজেট ও নিরাপত্তা পর্যালোচনা সাপেক্ষে নিজস্ব সার্ভার ক্রয় বা ভাড়া নিয়ে ব্যবহারের পরিকল্পনা। সংখ্যা পূর্ণ হলেই স্বয়ংক্রিয় ক্রয়, billing বা migration হবে না।
- [ ] ইউজার সংখ্যার ভিত্তিতে কোন ফিচার কখন যুক্ত হবে, তা ভবিষ্যতে আলোচনা ও সিদ্ধান্তের পর রোডম্যাপে যোগ করা হবে। আপাতত অন্য কোনো সংখ্যাভিত্তিক ফিচার-মাইলফলক নির্ধারিত নয়।

১৫ GB পূর্ণ হলে আরও Gmail, third-party cloud অথবা নিজস্ব server বিবেচনা মালিকের সিদ্ধান্ত; এটি স্বয়ংক্রিয়ভাবে প্রসারিত বা provider-এর নিয়ম এড়িয়ে বহু অ্যাকাউন্ট ব্যবহার করার প্রতিশ্রুতি নয়। ব্যবহার বাড়ার আগেও সীমা ফুরালে অনুমোদিত ফ্রি বিকল্প মূল্যায়ন বা ফিচার বিরতি প্রয়োজন হতে পারে।

### পূর্ণ profile ও ভবিষ্যৎ Islamic social network-এর চাহিদা

নিচের বৈশিষ্ট্যগুলো চাহিদা, বর্তমানে সব বাস্তবায়িত নয়। নিরাপদ free implementation সম্ভব হলে প্রয়োজন অনুসারে ধাপে ধাপে যুক্ত করা যেতে পারে; পরবর্তীতে Android, web, iOS ও নিজস্ব backend-এ বহনযোগ্য data model রাখতে হবে।

- [ ] শিশু/প্রাপ্তবয়স্ক/প্রবীণ, নারী/পুরুষ ও সাধারণ built-in avatar অথবা অনুমোদিত profile photo; cover শুধু বিদ্যমান অ্যাপের ছবি।
- [ ] প্রদর্শনযোগ্য first, optional middle ও last name; নাম না থাকলে আবশ্যক random fallback full name; sequential numeric ID ও পরিবর্তনযোগ্য unique username।
- [ ] জন্মতারিখ Gregorian, সঠিক নির্বাচিত Bangla ও Hijri conversion, বাংলা/ইংরেজি বার; রূপান্তরের convention ও স্থানীয় চাঁদ দেখার পার্থক্য যাচাই। জন্মসাল অনুযায়ী কেবল অনুমোদিত admin-এর সংখ্যা ও তালিকা।
- [ ] প্রয়োজনীয় gender: পুরুষ, নারী, প্রকাশে অনিচ্ছুক; super-admin-এর অনুমোদিত gender তালিকা/সংখ্যা। ঐচ্ছিক blood group, nationality, country ও preferred language; প্রয়োজনীয় timezone dropdown।
- [ ] প্রয়োজনীয় recovery email; email/phone public, friends, only me, default only me। ঐচ্ছিক phone, unverified WhatsApp, alternate recovery email/phone; ফ্রি ও যাচাইকৃত না হলে SMS/WhatsApp recovery বন্ধ।
- [ ] বর্তমান/স্থায়ী ঠিকানা হাতে লেখা; চাইলে অনুমতি নিয়ে GPS। ঐচ্ছিক emergency contact, কর্মক্ষেত্র ও সময়সহ বর্তমান/পুরোনো প্রতিষ্ঠান, শিক্ষা, একাধিক দক্ষতা, অর্জন, শখ, সম্পর্কে লেখা।
- [ ] Facebook, LinkedIn, GitHub, X, Instagram, YouTube, Telegram, Discord এবং website-এর ঐচ্ছিক লিংক; প্রিয় স্থান যুক্ত/বাদ দেওয়া। Social provider কেবল অনুমোদিত ও নির্ভরযোগ্যভাবে দেওয়া তথ্য import করবে; পূর্ণ social profile অটোমেটিক পাওয়া যায় না।
- [ ] account creation, সর্বশেষ profile update ও সর্বশেষ login-এর সময়; সংশ্লিষ্ট location কেবল সম্মতিতে ও প্রকৃত তথ্য থাকলে, কোনো তথ্য কল্পনা নয়। Online/offline লাল/সবুজ অবস্থা অনির্ভরযোগ্য/unknown অবস্থাসহ পরিকল্পনা; দেশ/মহাদেশভিত্তিক সংখ্যা শুধু যথাযথ অনুমতিপ্রাপ্ত admin-এর জন্য।
- [ ] device/session ইতিহাস বজায় রেখে নির্বাচিত ডিভাইস revoke করার trusted architecture; সাধারণ Firebase revoke-all-কে selective revoke বলা যাবে না।
- [ ] সাধারণ user, editor, admin, super-admin ও account status; কেবল trusted super-admin সাধারণ/editor/admin তৈরি বা পদোন্নতি করতে পারবেন—server rules, audit এবং নিরাপদ bootstrap প্রয়োজন।
- [ ] ডিভাইসভিত্তিক theme-এর cloud sync, category-wise notification ও privacy preferences; অ্যাকাউন্ট recovery, ঐচ্ছিক MFA এবং আলাদা app-entry lock। Friends feature ও social network পরে নিরাপত্তা/নীতিমালা প্রস্তুত হলে।
- [ ] SMS OTP, WhatsApp OTP, paid Firebase Storage/Cloud Functions, paid geo-API, server এবং ভবিষ্যতের অতিরিক্ত ভাষা/প্ল্যাটফর্ম: ব্যবহার ও অর্থপ্রদান নিয়ে মালিকের পরবর্তী সিদ্ধান্ত না পাওয়া পর্যন্ত অকার্যকর পরিকল্পনা; প্রযোজ্য সম্মতি মেনে।

## যাচাইকৃত তথ্যসূত্র ও সীমা

তথ্য ১৩ নভেম্বর ২০২৬-এ যাচাই; provider নিয়ম পরিবর্তন করতে পারে।

- [Firebase Authentication limits](https://firebase.google.com/docs/auth/limits): Spark password-reset emails ১৫০/দিন; এটি অ্যাকাউন্টে সফল password পরিবর্তনের সংখ্যা নয়। Identity Platform-এর আলাদা DAU ও SMS শর্তও দেখুন।
- [Firebase pricing](https://firebase.google.com/pricing) ও [Storage billing change](https://firebase.google.com/docs/storage/faqs-storage-changes-announced-sept-2024): Spark ও Blaze পার্থক্য; Firebase Storage-এর জন্য Blaze আবশ্যক, free allowance থাকলেও billing risk আছে।
- [Firestore quota](https://firebase.google.com/docs/firestore/quotas): ১ GiB, ৫০,০০০ reads/দিন, ২০,০০০ writes/দিন, ২০,০০০ deletes/দিন ও ১০ GiB egress/মাস; daily Firestore quota প্রায় Pacific midnight-এ reset হয়। এটিকে Authentication reset-এর প্রমাণ ধরা যাবে না।
- [FCM pricing](https://firebase.google.com/products/cloud-messaging) ও [FCM throttling](https://firebase.google.com/docs/cloud-messaging/throttling-and-quotas): FCM no-cost; HTTP v1 downstream default project quota ৬,০০,০০০ messages/minute, quota window clock minute-এর সঙ্গে এক নাও হতে পারে; 429/retry ও device/topic সীমা রয়েছে। এটি প্রতিদিনের গ্যারান্টিযুক্ত delivery সংখ্যা নয়।
- [Google account storage](https://support.google.com/googleone/answer/9312312) এবং [Drive API limits](https://developers.google.com/workspace/drive/api/guides/limits): shared account space ও API limits আলাদা।
- [Google Cloud budget alerts](https://docs.cloud.google.com/billing/docs/how-to/budgets): budget alert নিজে hard spending cap নয়।
