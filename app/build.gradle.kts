plugins {
    id("kotlin")
}

repositories {
    mavenCentral()
}

val exposedVersion = "0.40.1"

dependencies {
//    val ktor_version: String by project

    testImplementation("org.junit.jupiter:junit-jupiter:5.8.1")

    // https://mvnrepository.com/artifact/com.h2database/h2
    implementation("com.h2database:h2:2.1.214")
    implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-dao:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-java-time:$exposedVersion")
    implementation("io.ktor:ktor-server-core:1.4.1")
    implementation("io.ktor:ktor-server-netty:2.3.4")
    implementation("org.jetbrains.kotlinx:kotlinx-io-core:0.3.0")
    implementation("io.ktor:ktor-server-content-negotiation:2.0.1")
    implementation("io.ktor:ktor-serialization-kotlinx-json:2.0.1")

    runtimeOnly("org.slf4j:slf4j-simple:1.7.32")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<JavaCompile> {
    sourceCompatibility = JavaVersion.VERSION_1_9.toString()
    targetCompatibility = JavaVersion.VERSION_1_9.toString()
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.jvmTarget = "9"
}