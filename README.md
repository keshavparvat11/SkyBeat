# 🎵 SkyBeat – Modern Music Streaming & Offline Player

SkyBeat is a modern Android music player built using **Jetpack Compose** and **Media3 (ExoPlayer)**.  
It supports streaming, background playback, notification controls, offline downloads, and playlist management with Firebase.

---

## ✨ Features

- ▶️ Play / Pause / Next / Previous
- 🎧 Background playback
- 🔔 Media notification & lock-screen controls
- 📥 Download songs for offline listening
- 🗑 Delete downloaded tracks
- ❤️ Add / remove songs from playlist
- 🔄 Real-time sync using Firebase Firestore
- 🎚 Seek bar with live progress & timing
- 🎨 Clean Material 3 UI

---

## 🧠 Architecture

The project follows **MVVM (Model–View–ViewModel)** architecture.

- UI built with **Jetpack Compose**
- State managed using **StateFlow**
- Async work handled via **Kotlin Coroutines**
- Media playback powered by **Media3 ExoPlayer**
- Cloud database via **Firebase Firestore**

---

## 🛠 Tech Stack

- **Kotlin**
- **Jetpack Compose**
- **Media3 / ExoPlayer**
- **Firebase Auth**
- **Firebase Firestore**
- **Coroutines & Flow**
- **Material 3**

---

## 📱 Screens

- Splash Screen  
- Login / Signup  
- Home (Featured & Recent)  
- Search  
- Song Player  
- Library / Playlist  
- Downloads  

---

## 🚀 How It Works

### Streaming
Songs are fetched from Firestore and streamed using ExoPlayer.

### Playlist
Users can add or remove songs from their personal playlist, stored per user in Firebase.

### Downloads
Audio files are saved locally in: /Music/SkyBeat/


Downloaded songs are automatically detected and shown in the app.

### Notifications
Playback can be controlled from the notification shade and lock screen.

---

## 📸 Future Improvements

- Shuffle & Repeat
- Lyrics support
- Mini player
- Dynamic album art colors
- Create multiple playlists

---

## 👨‍💻 Author

Keshav Parvat 
Android Developer

---

## 📄 License

This project is for learning and portfolio purposes.
