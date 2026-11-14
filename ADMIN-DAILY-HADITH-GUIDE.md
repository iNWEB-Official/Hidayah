# দৈনিক হাদিস: শেয়ার ছবি ও অ্যাডমিন গাইড

এই গাইডটি অ্যাপের **প্রকল্প অ্যাডমিনের** জন্য। অ্যাপে বর্তমানে আলাদা অ্যাডমিন প্যানেল নেই; কনটেন্ট Firebase Console থেকে সম্পাদনা করতে হবে। আগে ধর্মীয়ভাবে যাচাই করা লেখা ও সঠিক সূত্র প্রকাশ করুন। ছবির লেখাটি অ্যাপ প্রকাশিত টেক্সট থেকে তৈরি করে—AI কোনো হাদিস বা সূত্র বানায় না।

## ১. প্রতিদিনের লেখা প্রকাশ বা সংশোধন

1. Firebase Console-এ আপনার `inweb-hidayah` প্রকল্প খুলুন → **Firestore Database → Data**।
2. `daily_values` সংগ্রহে সংশ্লিষ্ট তারিখের `YYYY-MM-DD` আইডি দিয়ে ডকুমেন্ট খুলুন বা তৈরি করুন। দিন নির্ধারণ ব্যবহারকারীর ডিভাইসের স্থানীয় তারিখ অনুযায়ী হয়।
3. নিচের প্রতিটি প্রয়োজনীয় ফিল্ড Firestore-এর **string** টাইপে দিন:
   - `title`: বিষয়ের শিরোনাম
   - `reflection`: সংক্ষিপ্ত ব্যাখ্যা
   - `referenceType`: হাদিস হলে `Hadith`, আয়াত হলে `Ayat`
   - `referenceText`: যাচাই করা বাংলা উদ্ধৃতি বা অনুবাদ
   - `reference`: নির্ভুল সূত্র
   - `referenceArabic`: যাচাই করা আরবি লেখা (ঐচ্ছিক)
4. নিচের দুই ছবি-ফিল্ডও ঐচ্ছিক। পুরোনো ডকুমেন্টে এগুলো না থাকলেও অ্যাপ চলবে।

## ২. ব্যাকগ্রাউন্ড নির্বাচনের নিয়ম

- **নিজের নতুন ছবি:** `shareBackgroundUrl` নামে একটি string ফিল্ডে ছবির সরাসরি পাবলিক HTTPS লিংক দিন। JPG, PNG বা WebP; সর্বোচ্চ ৫ MB; সার্ভারকে `200 OK` ও সঠিক `Content-Type` দিতে হবে। লগইন, redirect, HTTP, ব্যক্তিগত/লোকাল IP, custom port বা গোপন লিংক ব্যবহার করবেন না। ছবিটি user যখন ছবি তৈরি করবেন তখন ডাউনলোড হবে।
- **নিরাপত্তা সতর্কতা:** নতুন কোডে HTTPS সংযোগের DNS ফলের প্রাইভেট/বিশেষ ঠিকানা প্রত্যাখ্যান করা হয়, redirect ও proxy বন্ধ থাকে। বিশ্বস্ত অ্যাডমিনের public host ব্যবহার করুন; বিভিন্ন ডিভাইস/নেটওয়ার্কে যাচাই এখনো বাকি। পুরোনো APK-তে এই সুরক্ষা নেই—সেখানে URL খালি রেখে bundled ID ব্যবহার করুন। বিস্তারিত [Firebase ও শেয়ার-লিংক পর্যালোচনা](FIREBASE-SECURITY-REVIEW.md)।
- **নির্দিষ্ট ডিফল্ট ছবি:** `shareBackgroundId` নামে একটি string ফিল্ডে নিচের তালিকার ID দিন, যেমন `charity_b`। বড় হাতের অক্ষরও অ্যাপ ছোট হাতের অক্ষরে রূপান্তর করে।
- **কিছুই না দিলে:** অ্যাপ শিরোনাম, ব্যাখ্যা ও উদ্ধৃতিতে কয়েকটি বিষয়-সংশ্লিষ্ট শব্দ মিলিয়ে থিম নির্ধারণ করে এবং তার দুই ভ্যারিয়েন্টের একটি স্থিরভাবে বেছে নেয়। এটি AI-ভিত্তিক অর্থ বোঝা নয়। বিষয় না মিললে `general_a` বা `general_b` ব্যবহার হবে।
- **অগ্রাধিকার:** কার্যকর `shareBackgroundUrl` → বৈধ `shareBackgroundId` → শব্দ মেলানো → সাধারণ ডিফল্ট। লিংক ভুল, অফলাইন, ধীর, বড়, redirect বা অনুপলব্ধ হলে অ্যাপ ডিফল্ট ছবিতে ফিরে যাবে এবং প্রিভিউতে জানাবে। ভুল ID হলে শব্দ মেলানো হবে।
- দুটিই দিলেও লিংক আগে ব্যবহৃত হবে। শুধু ডিফল্ট ID ব্যবহার করতে চাইলে URL ফিল্ড খালি করুন বা মুছে দিন।

## ৩. ২০টি পূর্বনির্ধারিত ছবি

২০টি ব্যাকগ্রাউন্ডের মধ্যে বর্তমানে ১৯টি আলাদা AI-তৈরি মূল দৃশ্য আছে; বাকি ১টি পূর্বের দৃশ্যের ভ্যারিয়েন্ট। পরবর্তী ধাপে এটিও আলাদা ছবি দিয়ে প্রতিস্থাপন করা হবে। লিংক খুলে ছবি দেখে ID কপি করুন।

| বিষয় | ছবি A | ছবি B |
| --- | --- | --- |
| ধৈর্য | [`patience_a`](app/src/main/res/drawable-nodpi/hadith_patience_a.jpg) | [`patience_b`](app/src/main/res/drawable-nodpi/hadith_patience_b.jpg) |
| দয়া | [`compassion_a`](app/src/main/res/drawable-nodpi/hadith_compassion_a.jpg) | [`compassion_b`](app/src/main/res/drawable-nodpi/hadith_compassion_b.jpg) |
| দান | [`charity_a`](app/src/main/res/drawable-nodpi/hadith_charity_a.jpg) | [`charity_b`](app/src/main/res/drawable-nodpi/hadith_charity_b.jpg) |
| নামাজ/ইবাদত | [`prayer_a`](app/src/main/res/drawable-nodpi/hadith_prayer_a.jpg) | [`prayer_b`](app/src/main/res/drawable-nodpi/hadith_prayer_b.jpg) |
| সততা | [`honesty_a`](app/src/main/res/drawable-nodpi/hadith_honesty_a.jpg) | [`honesty_b`](app/src/main/res/drawable-nodpi/hadith_honesty_b.jpg) |
| জ্ঞান | [`knowledge_a`](app/src/main/res/drawable-nodpi/hadith_knowledge_a.jpg) | [`knowledge_b`](app/src/main/res/drawable-nodpi/hadith_knowledge_b.jpg) |
| পরিবার | [`family_a`](app/src/main/res/drawable-nodpi/hadith_family_a.jpg) | [`family_b`](app/src/main/res/drawable-nodpi/hadith_family_b.jpg) |
| কৃতজ্ঞতা | [`gratitude_a`](app/src/main/res/drawable-nodpi/hadith_gratitude_a.jpg) | [`gratitude_b`](app/src/main/res/drawable-nodpi/hadith_gratitude_b.jpg) |
| প্রতিবেশী | [`neighbor_a`](app/src/main/res/drawable-nodpi/hadith_neighbor_a.jpg) | [`neighbor_b`](app/src/main/res/drawable-nodpi/hadith_neighbor_b.jpg) |
| সাধারণ | [`general_a`](app/src/main/res/drawable-nodpi/hadith_general_a.jpg) | [`general_b`](app/src/main/res/drawable-nodpi/hadith_general_b.jpg) |

## ৪. নতুন AI ছবি তৈরি ও যোগ করা

1. Gemini বা অন্য অনুমোদিত টুলের ফ্রি সুবিধা ও বর্তমান শর্ত যাচাই করে ছবি তৈরি করুন। কোনো নবী বা পবিত্র ব্যক্তিত্বের ছবি তৈরি/ব্যবহার করবেন না; অর্থসঙ্গত প্রকৃতি, স্থাপত্য বা প্রতীকী দৃশ্য বেছে নিন। ছবি ব্যবহারের অধিকার নিশ্চিত করুন।
2. ছবিতে হাদিসের লেখা বা সূত্র AI দিয়ে লেখাবেন না: বানান বা অর্থ ভুল হতে পারে। অ্যাপ প্রকাশিত লেখাটি নিজেই বসাবে। ছবিটি portrait, তুলনামূলক শান্ত এবং লেখার জন্য উপযোগী রাখুন।
3. ছবিকে সরাসরি HTTPS-এ প্রকাশ করুন। উদাহরণ হিসেবে রিপোজিটরিতে `daily-backgrounds/2026-11-14.jpg` নাম দিয়ে ছবি যোগ করলে GitHub-এর **Raw** লিংক ব্যবহার করতে পারেন; সার্ভিসের ব্যবহারনীতি, ব্যান্ডউইথ ও নির্ভরযোগ্যতা বিবেচনা করুন। Firebase Storage বা অন্য কোনো পদ্ধতিতে বিলিং লাগতে পারে।
4. ফাইলের direct URL `shareBackgroundUrl` ফিল্ডে বসান। আগের অংশে থাকা অন্য প্রয়োজনীয় ফিল্ড মুছে ফেলবেন না।
5. অ্যাপে ওই তারিখের দৈনিক অংশে **ছবি শেয়ার** চাপুন → প্রিভিউতে ছবি, পুরো লেখা ও সূত্র পরীক্ষা করুন → **শেয়ার করুন**। ছবির URL কাজ না করলে প্রিভিউতে fallback বার্তা থাকবে।

## ৫. ব্যবহারকারী ও অপারেশন নোট

- **টেক্সট শেয়ার** অ্যাপের স্বাভাবিক শেয়ার মেনুতে প্রকাশিত লেখা ও সূত্র পাঠায়। **ছবি শেয়ার** প্রথমে প্রিভিউ দেখায়; নিশ্চিত করলে PNG শেয়ার করে। দীর্ঘ লেখা নিরাপদ সর্বোচ্চ উচ্চতায় না ধরলে কেটে না দিয়ে ছবি শেয়ার বন্ধ হবে—তখন সম্পূর্ণ টেক্সট শেয়ার করুন।
- তৈরি PNG অ্যাপের অস্থায়ী cache-এ থাকে; শেয়ারের জন্য কেবল ওই ফাইলের সীমিত read permission দেওয়া হয়। ২৪ ঘণ্টার বেশি পুরোনো শেয়ার ফাইল পরবর্তী শেয়ারে পরিষ্কার করার চেষ্টা হয়; অপারেটিং সিস্টেমও cache পরিষ্কার করতে পারে।
- নতুন ছবির URL-এ ব্যবহারকারীর ডিভাইস থেকে সরাসরি অনুরোধ যায়; হোস্ট ব্যবহারকারীর IP ও সময় দেখতে পারে। বিশ্বস্ত হোস্ট ব্যবহার করুন। ব্যক্তিগত কনটেন্ট বা API key URL-এ দেবেন না।
- কোনো স্বয়ংক্রিয় Gemini/Imagen API কল বা Cloud Function এখানে নেই। অ্যাপের bundled ছবি অফলাইনে কাজ করে; দৈনিক Firestore ডেটা ও নতুন ছবি হোস্টের নিজস্ব কোটা/খরচ প্রযোজ্য হতে পারে।
- Firestore-এর client write access বাড়াবেন না। বিদ্যমান read-only rules যাচাই করুন; production-এ rules পরিবর্তনের আগে পর্যালোচনা করুন।
- GitHub Actions-এ বিল্ড/টেস্টের ফল দেখুন। ফন্ট, দীর্ঘ/আরবি লেখার বিন্যাস, লিংক ফেইল ও বিভিন্ন শেয়ার অ্যাপ বাস্তব ডিভাইসে পরীক্ষা ও ধর্মীয় কনটেন্ট পর্যালোচনা এখনো প্রয়োজন।
