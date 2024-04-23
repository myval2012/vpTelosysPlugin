plugins {
    id("java")
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

group = "cz.fit.vut.xvrana32.telosysplugin"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(files("dependency/openapi.jar", "dependency/telosys-tools-all-4.1.1.jar"))
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}

tasks.register<Zip>("zip"){
//    dependsOn("build")
    mustRunAfter("build")

    archiveFileName = "telosysPlugin.zip"
    destinationDirectory = layout.buildDirectory.dir(".")

// .class source files of my plugin
    from(layout.buildDirectory.dir("classes/java/main")){
        include("cz/**")
        into("classes")
    }
// resources
    from(layout.buildDirectory.dir("resources/main")){
        include("icons/**")
    }
// plugin.xml
    from("."){
       include("plugin.xml")
    }
// unpacked telosys archive
    from(zipTree("dependency/telosys-tools-all-4.1.1.jar")){
        into("classes")
    }
}