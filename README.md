# 🚀 Artha Android App - Multi-Agent Finance Client

<div align="center">

[![Android](https://img.shields.io/badge/Platform-Android-green.svg?style=flat-square)](https://developer.android.com)
[![Kotlin](https://img.shields.io/badge/Language-Kotlin-blue.svg?style=flat-square)](https://kotlinlang.org)
[![Jetpack Compose](https://img.shields.io/badge/UI-Jetpack%20Compose-4285F4.svg?style=flat-square)](https://developer.android.com/jetpack/compose)
[![Firebase](https://img.shields.io/badge/Backend-Firebase-orange.svg?style=flat-square)](https://firebase.google.com)
[![MIT License](https://img.shields.io/badge/License-MIT-yellow.svg?style=flat-square)](LICENSE)

*A sophisticated Android application built with Jetpack Compose for multi-agent financial management and analysis*

</div>

## ✨ Features

### 🏦 **Financial Management**
- **Real-time Dashboard** - Interactive financial overview with live data
- **Investment Tracking** - Monitor your portfolio performance with beautiful charts
- **Transaction Management** - Seamless transaction history and categorization
- **Net Worth Calculation** - Comprehensive wealth tracking and analysis

### 🤖 **AI-Powered Chat Interface**
- **Multi-Agent System** - Interact with specialized financial AI agents
- **Intelligent Responses** - Get personalized financial advice and insights
- **Rich Chat UI** - Modern bottom sheet chat interface with Markdown support
- **Contextual Analysis** - AI agents understand your financial context

### 🎨 **Modern UI/UX**
- **Material Design 3** - Latest Material You design language
- **Adaptive UI** - Optimized for different screen sizes and orientations
- **Smooth Animations** - Engaging transitions and micro-interactions
- **Dark/Light Theme** - Automatic theme switching support

### 📊 **Data Visualization**
- **Interactive Charts** - Beautiful custom charts for financial data
- **Real-time Updates** - Live data synchronization via Firebase
- **Performance Metrics** - Detailed analytics and KPIs

## 🏗️ Architecture

### **Clean Architecture Pattern**
```
📦 com.example.mcpclient
├── 📁 data/
│   ├── 📁 api/          # Network API interfaces
│   ├── 📁 models/       # Data models & DTOs
│   ├── 📁 network/      # Network configuration
│   └── 📁 repository/   # Data repositories
├── 📁 presentation/
│   ├── 📁 components/   # Reusable UI components
│   ├── 📁 navigation/   # Navigation setup
│   ├── 📁 screens/      # Screen composables
│   └── 📁 viewmodel/    # ViewModels
└── 📁 ui/
    └── 📁 theme/        # App theming
```

### **Tech Stack**
- **🏗️ Architecture**: MVVM + Clean Architecture
- **🎨 UI Framework**: Jetpack Compose
- **🚀 Language**: Kotlin
- **🌐 Networking**: Retrofit + OkHttp
- **🗄️ Database**: Firebase Realtime Database
- **📐 DI**: Manual Dependency Injection
- **🧭 Navigation**: Navigation Compose
- **📊 Charts**: Custom Compose Charts
- **📝 Markdown**: Compose Markdown

## 🚀 Getting Started

### Prerequisites
- **Android Studio**: Hedgehog | 2023.1.1 or newer
- **Kotlin**: 1.9.0+
- **Android SDK**: API 24+ (Android 7.0)
- **JDK**: 11 or higher

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/harshjoshi004/ArthaAndroidApp.git
   cd ArthaAndroidApp
   ```

2. **Firebase Setup**
   - Create a new Firebase project at [Firebase Console](https://console.firebase.google.com)
   - Add an Android app with package name: `com.example.mcpclient`
   - Download `google-services.json` and place it in the `app/` directory
   - Alternatively, copy `google-services.json.template` to `google-services.json` and fill in your values

3. **Build and Run**
   ```bash
   ./gradlew assembleDebug
   ./gradlew installDebug
   ```

### Configuration

#### Firebase Configuration
Create your own `google-services.json` file with your Firebase project details:

```json
{
  "project_info": {
    "project_number": "YOUR_PROJECT_NUMBER",
    "firebase_url": "https://YOUR_PROJECT_ID-default-rtdb.region.firebasedatabase.app",
    "project_id": "YOUR_PROJECT_ID",
    "storage_bucket": "YOUR_PROJECT_ID.appspot.com"
  }
  // ... additional configuration
}
```

## 📱 Screenshots

<div align="center">

| Dashboard | Investments | Chat Interface |
|:---------:|:-----------:|:--------------:|
| ![Dashboard](screenshots/dashboard.png) | ![Investments](screenshots/investments.png) | ![Chat](screenshots/chat.png) |

| Transactions | Net Worth | Authentication |
|:------------:|:---------:|:--------------:|
| ![Transactions](screenshots/transactions.png) | ![Net Worth](screenshots/networth.png) | ![Auth](screenshots/auth.png) |

</div>

## 🧪 Testing

### Unit Tests
```bash
./gradlew testDebugUnitTest
```

### Instrumentation Tests
```bash
./gradlew connectedDebugAndroidTest
```

### Test Coverage
```bash
./gradlew jacocoTestReport
```

## 🔧 Development

### Code Style
This project follows the [Kotlin Coding Conventions](https://kotlinlang.org/docs/coding-conventions.html) and uses:
- **ktlint** for code formatting
- **detekt** for static code analysis

### Gradle Tasks
```bash
# Clean build
./gradlew clean

# Debug build
./gradlew assembleDebug

# Release build
./gradlew assembleRelease

# Run tests
./gradlew test

# Generate lint report
./gradlew lintDebug
```

## 🚦 API Integration

### Endpoints Configuration
The app supports multiple API endpoints for different financial services:

```kotlin
// Example API configuration
object ApiConfig {
    const val BASE_URL = "https://api.example.com/"
    const val TIMEOUT = 30L
}
```

### Supported Services
- 📈 **Stock Market Data**
- 💱 **Currency Exchange**
- 🏦 **Banking Integration**
- 📊 **Financial Analytics**

## 📋 Roadmap

### Upcoming Features
- [ ] 🔐 Biometric Authentication
- [ ] 📲 Push Notifications
- [ ] 🌍 Multi-language Support
- [ ] 📈 Advanced Analytics
- [ ] 🤖 More AI Agent Types
- [ ] 💫 Widget Support
- [ ] 🔄 Offline Mode
- [ ] 📤 Data Export

### Performance Improvements
- [ ] ⚡ Lazy Loading Implementation
- [ ] 🗃️ Local Caching Strategy
- [ ] 🔄 Background Sync
- [ ] 📱 Battery Optimization

## 🤝 Contributing

We welcome contributions! Please see our [Contributing Guidelines](CONTRIBUTING.md) for details.

### Development Workflow
1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

### Code Review Process
- All code must pass CI checks
- Minimum 2 approving reviews required
- Follow the project's coding standards

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 👥 Authors & Contributors

### Core Team
- **[Harsh Joshi](https://github.com/harshjoshi004)** - *Project Lead & Main Developer*

### Contributors
We appreciate all contributors! See [CONTRIBUTORS.md](CONTRIBUTORS.md) for the full list.

## 📞 Support & Contact

### Get Help
- 📧 **Email**: support@arthaapp.com
- 💬 **Discord**: [Join our community](https://discord.gg/arthaapp)
- 🐛 **Issues**: [GitHub Issues](https://github.com/harshjoshi004/ArthaAndroidApp/issues)
- 📚 **Documentation**: [Wiki](https://github.com/harshjoshi004/ArthaAndroidApp/wiki)

### Social Media
- 🐦 **Twitter**: [@ArthaApp](https://twitter.com/arthaapp)
- 💼 **LinkedIn**: [Artha Finance](https://linkedin.com/company/artha-finance)

## 🙏 Acknowledgments

Special thanks to:
- **Jetpack Compose Team** for the amazing UI toolkit
- **Firebase Team** for the robust backend services
- **Retrofit Team** for excellent networking capabilities
- **Open Source Community** for inspiration and contributions

## 📊 Project Stats

![GitHub stars](https://img.shields.io/github/stars/harshjoshi004/ArthaAndroidApp?style=social)
![GitHub forks](https://img.shields.io/github/forks/harshjoshi004/ArthaAndroidApp?style=social)
![GitHub issues](https://img.shields.io/github/issues/harshjoshi004/ArthaAndroidApp)
![GitHub pull requests](https://img.shields.io/github/issues-pr/harshjoshi004/ArthaAndroidApp)

---

<div align="center">

**[⬆ Back to Top](#-artha-android-app---multi-agent-finance-client)**

Made with ❤️ by the Artha Team

</div>
