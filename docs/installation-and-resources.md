# Installation and Resources

## Installation

### Desugaring

All versions of mercurialftc need desugaring enabled in order to be able to manage settings using TOML

1. Add `coreLibraryDesugaringEnabled true` to the top of your compileOptions block in `build.common.gradle`
2. Add `coreLibraryDesugaring 'com.android.tools:desugar_jdk_libs:1.1.9'` to the bottom of your dependencies block in `build.dependencies.gradle`
3. Do not update desugar\_jdk\_libs, it is at the newest version for the version of the gradle AGP used in the FTCRobotController SDK

### Adding MercurialFTC as a Dependency

1. Add `maven { url "https://jitpack.io" } // Needed for mercurialftc` to your repositories block in `build.dependencies.gradle`
2. Add `implementation 'com.github.Froze-N-Milk:mercurialftc:<tag>'` to the bottom of your dependencies block in `build.dependencies.gradle`
3. Run a gradle sync

To install the desired version of mercurialftc replace `<tag>` with your desired tag from the releases section of [mercurialftc](https://github.com/Froze-N-Milk/mercurialftc), or your desired commit hash. E.g. `implementation 'com.github.Froze-N-Milk:mercurialftc:v0.0.0'`

## Resources

### Discord Server

[MercurialFTC](https://discord.gg/xaSHyhKkFr) | official library discord server

### Samples

Samples throughout this documentation are taken from the following repositories. If you are using mercurialftc, I would love to add your repository to this list, and add examples from real-usage cases.

[mercurialftc](https://github.com/Froze-N-Milk/mercurialftc) | official project page

[mercurialftcsample](https://github.com/Froze-N-Milk/mercurialftcsample) | official samples + drive quick start
