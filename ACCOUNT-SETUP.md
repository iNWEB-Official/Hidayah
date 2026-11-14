# লগইন ও প্রোফাইল: সেটআপ ও সীমাবদ্ধতা

সাইড মেনুর **লগইন ও প্রোফাইল** থেকে Firebase Authentication-এর ইমেইল–পাসওয়ার্ড sign-in, অ্যাকাউন্ট তৈরি, verification email, password reset এবং sign-out-এর প্রাথমিক ব্যবস্থা আছে। এগুলো ব্যবহার করতে ইন্টারনেট প্রয়োজন। অতিথি হিসেবেও অ্যাপের আগের মূল ফিচার চালু থাকে; tracker এখনো এই ডিভাইসেই থাকে, অ্যাকাউন্টে সিঙ্ক হয় না।

## প্রকল্প মালিকের প্রয়োজনীয় কাজ

1. Firebase Console-এ সঠিক প্রকল্পে **Authentication → Sign-in method → Email/Password** চালু আছে কি না দেখুন। মালিকের সম্মতি ছাড়া এই প্রকল্পের সেটিংস বা billing পরিবর্তন করা হয়নি। Google Cloud-এর Firebase Authentication API ও সম্ভাব্য quota পর্যালোচনা করুন।
2. অ্যাকাউন্ট তৈরি করে যাচাই ইমেইল পাঠান; ব্যবহারকারী লিংক থেকে ইমেইল যাচাই করার পর অ্যাপে **পরিচয় আবার যাচাই করুন** চাপতে পারবেন। কোডটি পরীক্ষা করতে প্রকৃত Android ডিভাইস ও কনফিগার করা Firebase প্রকল্প প্রয়োজন। কাউকে পাসওয়ার্ড, service-account key বা auth token পাঠাবেন না।
3. প্রকাশের আগে privacy policy, পাসওয়ার্ড নীতি, অ্যাকাউন্ট মুছে ফেলা/ডেটা ব্যবস্থাপনা, অপব্যবহার প্রতিরোধ ও Firebase-এর চলতি কোটা পর্যালোচনা করতে হবে।

## User এবং Admin

প্রাথমিক profile UI-তে যাচাইকৃত ID token-এর boolean `admin` claim এবং `email_verified` claim সত্য ও verified account হলেই Admin পরিচয় দেখানো হয়। সাধারণ signup কখনো নিজের জন্য এই claim তৈরি করে না। Claim কেবল বিশ্বস্ত privileged Admin SDK/server-side ব্যবস্থার মাধ্যমে মালিক নির্ধারণ করবেন; ওই ব্যবস্থাটি এখনো তৈরি বা পরিচালিত হয়নি।

**গুরুত্বপূর্ণ:** স্ক্রিনে Admin লেখা কোনো নিরাপত্তা সীমানা নয়। ভবিষ্যতের admin upload ও Firestore/Drive operations অবশ্যই যথাযথ server-side authorization, restrictive Firebase Security Rules এবং নিরাপদ Drive access দিয়ে আলাদাভাবে সুরক্ষিত করতে হবে। বর্তমান Firestore rules বা অন্য cloud settings পরিবর্তন করা হয়নি। Admin panel, Google Drive upload ও image auto-sync এখনো তৈরি হয়নি। কোনো Drive credential বা token APK-তে স্থায়ীভাবে রাখা যাবে না।
