plugins {
	id("com.android.library")
	id("kotlin-android")
	id("org.jetbrains.dokka") version "1.9.10"
	id("maven-publish")
}

android {
	namespace = "dev.frozenmilk.mercurial"
	compileSdk = 29

	defaultConfig {
		minSdk = 24
		//noinspection ExpiredTargetSdkVersion
		targetSdk = 28

		testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
		consumerProguardFiles("consumer-rules.pro")
	}

	buildTypes {
		release {
			isMinifyEnabled = false
			proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
		}
	}
	compileOptions {
		sourceCompatibility = JavaVersion.VERSION_1_8
		targetCompatibility = JavaVersion.VERSION_1_8
	}
}

repositories {
	maven { url = uri("https://www.jitpack.io") }
}

dependencies {
	//noinspection GradleDependency
	implementation("androidx.appcompat:appcompat:1.2.0")
	testImplementation("org.testng:testng:6.9.6")

	compileOnly("com.github.Dairy-Foundation.Dairy:Core:beta-0")
	compileOnly("com.github.Dairy-Foundation.Dairy:Calcified:beta-0")

	compileOnly("org.firstinspires.ftc:RobotCore:9.0.1")
	compileOnly("org.firstinspires.ftc:Hardware:9.0.1")
	compileOnly("org.firstinspires.ftc:FtcCommon:9.0.1")
}

publishing {
	publications {
		register<MavenPublication>("release") {
			groupId = "dev.frozenmilk.mercurial"
			artifactId = "Mercurial"
			version = "v0.0.0"

			afterEvaluate {
				from(components["release"])
			}
		}
	}
	repositories {
		maven {
			name = "Mercurial"
			url = uri("${project.buildDir}/release")
		}
	}
}