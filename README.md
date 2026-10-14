# Barbarian Arena

Barbarian Arena Android app with splash screen, menu UI, MIT license, and release signing guidance.

## Overview
- **Runtime**: Android 8.0+ (Min SDK 26, Target/Compile SDK 36)
- **UI Framework**: Jetpack Compose & Material 3
- **Build System**: Gradle 9.3.1 (Kotlin DSL), Android Gradle Plugin 9.1.1, Kotlin 2.2.10

## Features
- **Splash Screen**: Barbarian crest animation and entrance transition.
- **Main Menu**: High-level warrior stats (Level, Gold, Glory, Streak) with interactive arena hubs.
- **Battle Arena**: Turn-based gladiator combat featuring dynamic combat moves (Quick Strike, Heavy Cleave, Shield Slam, Berserk Rage, Blood Roar), live health bars, fury meter, and battle log.
- **Warrior Barracks**: Class selection (Berserker, Gladiator, Warlord, Valkyrie), XP progression, and gold attribute training (Health, Attack, Defense).
- **The Armory**: Weapon, armor, and talisman purchasing and equipment system.
- **Hall of Champions**: Ranked leaderboard and Colosseum league standings.
- **Release & Build Guide**: Built-in developer guide displaying signing configurations, EAS CI/CD notes, and the full MIT License text.

## Building the APK
To assemble the debug APK:
```bash
gradle :app:assembleDebug
```
The resulting APK is generated at `app/build/outputs/apk/debug/app-debug.apk`.
