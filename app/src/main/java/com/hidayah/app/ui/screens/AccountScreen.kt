package com.hidayah.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hidayah.app.data.AccountRole
import com.hidayah.app.ui.AccountViewModel
import com.hidayah.app.ui.theme.*

@Composable
fun AccountScreen(viewModel: AccountViewModel, onBack: () -> Unit) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var email by remember { mutableStateOf("") }
    // Never persist password in saved state or logs.
    var password by remember { mutableStateOf("") }
    var registering by remember { mutableStateOf(false) }
    Column(Modifier.fillMaxSize().background(Canvas)) {
        FeatureHeader("লগইন ও প্রোফাইল", onBack)
        Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp)) {
            if (state.loading) Text("অ্যাকাউন্ট তথ্য দেখা হচ্ছে…", color = Muted)
            else if (state.profile == null) {
                Text(if (registering) "নতুন অ্যাকাউন্ট" else "ইমেইল দিয়ে লগইন", color = Forest, style = MaterialTheme.typography.titleLarge)
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("ইমেইল") }, singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email), modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text("পাসওয়ার্ড") }, singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password), visualTransformation = PasswordVisualTransformation(), modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(12.dp))
                Button(enabled = !state.busy && email.isNotBlank() && password.isNotEmpty(), onClick = {
                    val entered = password; password = ""
                    if (registering) viewModel.register(email, entered) else viewModel.signIn(email, entered)
                }) { Text(if (registering) "অ্যাকাউন্ট তৈরি করুন" else "লগইন করুন") }
                TextButton(enabled = !state.busy, onClick = { registering = !registering; password = "" }) {
                    Text(if (registering) "আগের অ্যাকাউন্টে লগইন" else "নতুন অ্যাকাউন্ট তৈরি")
                }
                TextButton(enabled = !state.busy && email.isNotBlank(), onClick = { password = ""; viewModel.reset(email) }) { Text("পাসওয়ার্ড ভুলে গেছেন?") }
                Text("লগইন ছাড়াও অ্যাপের বর্তমান মূল সুবিধাগুলো ব্যবহার করা যায়।", color = Muted)
            } else {
                val profile = state.profile!!
                ProfileAvatarPicker(profile.uid)
                Spacer(Modifier.height(14.dp))
                Text(profile.email, color = Forest, style = MaterialTheme.typography.titleLarge)
                Spacer(Modifier.height(8.dp))
                Text(when (profile.role) {
                    AccountRole.ADMIN -> "অ্যাডমিন পরিচয় যাচাই হয়েছে"
                    AccountRole.USER -> "ব্যবহারকারী অ্যাকাউন্ট"
                    AccountRole.UNVERIFIED -> "ইমেইল বা পরিচয় যাচাই বাকি"
                }, color = Muted)
                if (!profile.emailVerified) TextButton(enabled = !state.busy, onClick = viewModel::verify) { Text("ইমেইল যাচাইয়ের লিংক পাঠান") }
                TextButton(enabled = !state.busy, onClick = viewModel::refresh) { Text("পরিচয় আবার যাচাই করুন") }
                TextButton(enabled = !state.busy, onClick = viewModel::signOut) { Text("লগআউট") }
                Text("অ্যাডমিন আপলোড ও ক্লাউড সিঙ্ক পরবর্তী ধাপে তৈরি হবে।", color = Muted)
            }
            if (state.busy) { Spacer(Modifier.height(8.dp)); Text("অনুগ্রহ করে অপেক্ষা করুন…", color = Muted) }
            state.message?.let { Spacer(Modifier.height(8.dp)); Text(it, color = Muted) }
        }
    }
}
