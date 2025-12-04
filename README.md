
# Munchkin Android Application

**Munchkin** is a multi-protocol, multi-band device designed for sniffing, communication, and attacking IoT devices. Munchkin was designed to be controlled via **RPC (Remote Procedure Call)** through an Android device via **USB-Serial**. This repository belongs to the Munchkin Android Application, if you wish to explore more about the hardware or the firmware, feel free to visit the [Munchkin Repository](https://github.com/ElectronicCats/munchkin)


![GitHub actions](https://img.shields.io/github/actions/workflow/status/ElectronicCats/Minino/builds.yml)
![Static Badge](https://img.shields.io/badge/made-with_love-blue?color=%23008000)

<p align="center">
  <img height="400" alt="image" src="https://github.com/user-attachments/assets/4955df6c-2e94-49a3-9b7b-ed0002cba6fb" />
</p>

Works correctly in this applications.

| - - - WiFi - - - |
- [x] Analyzer
- [x] Deauth
- [x] SSID Spammer

Work in progress.

| - - - WiFi - - - |
- [ ] Captive Portal
- [ ] Deauth Scan
- [ ] Denial of Service
- [ ] Modbus TCP

| - - - Bluetooth - - - |
- [ ] Trackers Scan
- [ ] BLE Spam
- [ ] HiD
      
| - - - Others - - - |
- [ ] Zigbee 
- [ ] Thread
- [ ] SubGHz
- [ ] GPIO
- [ ] Plugins
     

In addition to its innovative integration of an Android application for device management, Protocol Buffers serialization technology, designed and developed by Google, was implemented, allowing for compact, fast, and efficient information transfer.

This project is based on **[usb-serial-for-android](https://github.com/mik3y/usb-serial-for-android)** by **mik3y**, which enables serial device communication via Android, with robust and reliable tools for data transmission.

<p align="center">
  <a href="https://www.electroniccats.com">
    <img src="https://electroniccats.com/wp-content/uploads/2018/01/fav.png">
  </a>
</p>


The application was developed with the Android Studio IDE (Android Studio Otter | 2025.2.1) using Kotlin and Jetpack Compose as the main programming tools. In addition, certain implementations were made to facilitate the development of the application, such as Dagger Hilt. These changes were made in the build.gradle (app) of the document.


> IMPORTANT:
> Please use an OTG Adapter or an USB-C to USB-C Cable for the use or development of this application.

> IMPORTANT:
> If you do not want to explore the [**"Start Developing"**](#start-developing) explanation of the application, then jump to  [**"How to Use the Application"**](#how-to-use-the-application) section.  .

> NOTE:
>It is highly recommended to compile this project on an Android 11+ device. The reason is that it allows wireless ADB (Android Debug Bridge), a perfect tool to debug USB-Serial.

## Start Developing

To begin developing this project, you must follow these steps.

### 1. Download Android Studio

The first step is to download, install and execute **[Android Studio](https://developer.android.com/studio?hl=es-419)**.

Once installed and executed you should see something like this:

<p align="center">
  <img width="774" height="628" alt="image" src="https://github.com/user-attachments/assets/5cf017d7-f0b3-48c3-8237-18e32059d515" />
</p>

To import this repository into Android Studio you must do the following steps:

&nbsp;&nbsp;&nbsp;&nbsp;**I)** Click on "Get from VCS".

&nbsp;&nbsp;&nbsp;&nbsp;**II)** Once opened, copy one of the following URL's and paste it into the URL text field, then press enter on your keyboard or click the blue button that says "clone" on the bottom right to start cloning this repository.
  
  **HTTPS**
  ```bash
  https://github.com/ElectronicCats/munchkin-app.git
  ```
  **SSH**
  ```bash
  git@github.com:ElectronicCats/munchkin-app.git
  ```
> NOTE: Remember that, to use the SSH URL you must first synchronize your SSH key from your PC to your GitHub account. Here are some [videos](https://www.youtube.com/results?search_query=how+to+activate+ssh+on+github) that explain you how

&nbsp;&nbsp;&nbsp;&nbsp;**III)** Now wait for the project to be cloned. Once it finishes, allow Android Studio to import the project. Be patient, because it may take a while to import.

After Android Studio finished importing the project files, the Android Studio project must look like this.

<p align="center">
  <img width="1366" height="727" alt="image" src="https://github.com/user-attachments/assets/6528af15-9bab-4cd6-a236-ac66a37eaeb7" />
</p>

To compile the code into your Android device be sure that your ADB(Android Debug Bridge) is connected either by USB or WiFi, once you are conected and your screen looks like this, you can press on the green play button on the top that says "Run 'app'".

<p align="center">
  <img width="334" height="41" alt="image" src="https://github.com/user-attachments/assets/db80302b-5f20-4134-a1a2-626ab03a7618" />
</p>

You can continue to the next step to develop on this project.

### 2. Explore munchkin-app in Android Studio

To know how the code of the munchkin-app works, we must explore the files of the project.

#### Protocol Buffers

Protocol Buffers (protobuf) is one of the main implementations made in this project, to see where are the .proto files stored in your project you must set your project files view to "project", then navigate to the following directory route.

**~\munchkin-app\app\src\main\proto**

<p align="center">
  <img width="431" height="373" alt="image" src="https://github.com/user-attachments/assets/7c3afabe-a226-4a85-a782-c962a6c00f6c" />
</p>

In these folders you will observe the different **.proto** files that allows the compilation of the protobuf code for Kotlin. For instance: When you open the **main.proto** file, you will see something just like this.

> NOTE: Recommended to install a plugin for Android Studio that supports .proto file syntax.

  ```proto
syntax = "proto3";
package minino.rpc;

import "about/about.proto";
import "wifi/analyzer/analyzer.proto";
import "wifi/deauth/deauth.proto";
import "wifi/wifi_spam/wifispam.proto";
enum Status {
      STATUS_UNKNOWN = 0;
      STATUS_OK = 1;
      STATUS_ERROR = 2;
}

message MainRequest {

      uint32 message_id = 100;
      Status status = 101;
    
      oneof payload {
        // about mesagges
        minino.about.AboutRequest about = 1;
        // analyzer messages
        minino.analyzer.AnalyzerStartRequest analyzer_start = 2;
        minino.analyzer.AnalyzerStopRequest analyzer_stop = 3;
        minino.analyzer.AnalyzerSetChannelRequest analyzer_set_channel = 4;
        // deauth mess
        minino.deauth.DeauthScanRequest deauth_scan = 5;
        minino.deauth.DeauthSelectTargetRequest deauth_select_target = 6;
        minino.deauth.DeauthSetAttackRequest deauth_set_attack = 7;
        minino.deauth.DeauthStartAttackRequest deauth_start = 8;
        minino.deauth.DeauthStopRequest deauth_stop = 9;
        // wifi spam
        minino.wifispam.WifiSpamSetConfigRequest wifispam_config = 12;
        minino.wifispam.WifiSpamStartRequest wifispam_start = 13;
        minino.wifispam.WifiSpamStopRequest wifispam_stop = 14;
      }
}

message MainResponse {
      uint32 message_id = 100;
      Status status = 101;
    
      oneof payload {
        minino.about.AboutResponse about = 1;
        minino.analyzer.AnalyzerData analyzer = 2;
        minino.deauth.DeauthScanResults deauth_scan_results = 5;
      }
}
  ```

This code is meant to be **the center of all the .proto files** that are already into the project, additioning the posterior codes that are planned to be integrated for future modules.

In the next code snippet you will observe the first part of the code, this sets the syntax of the protobuf version that you will use and some imports from the different gathered **.proto** files.

  ```proto
syntax = "proto3";
package minino.rpc;

import "about/about.proto";
import "wifi/analyzer/analyzer.proto";
import "wifi/deauth/deauth.proto";
import "wifi/wifi_spam/wifispam.proto";
 ```

As you can observe on the code there is a status enum, which indicates if the request was successful or not.

  ```proto
enum Status {
      STATUS_UNKNOWN = 0;
      STATUS_OK = 1;
      STATUS_ERROR = 2;
}
 ```

There is also a MainRequest that has an unique identifier, and the integration of the status enum previously mentioned; now the part that you must pay the most attention, it is that the code also includes a **one of** payload that indicates that only one command at the time can be executed.

  ```proto
message MainRequest {

      uint32 message_id = 100;
      Status status = 101;
    
      oneof payload {
        // about mesagges
        minino.about.AboutRequest about = 1;
        // analyzer messages
        minino.analyzer.AnalyzerStartRequest analyzer_start = 2;
        minino.analyzer.AnalyzerStopRequest analyzer_stop = 3;
        minino.analyzer.AnalyzerSetChannelRequest analyzer_set_channel = 4;
        ...
 ```
> NOTE: If you wish to know more about the protobuf documentation, structure and logic; it is a good idea to visit **[the oficial webpage](https://protobuf.dev/)**. 

Now that you know the basic functionality of the code, you must learn how to compile the .proto files in this project. The only thing you must do is to press the "run 'app'" button or the "Assemble 'app' Run Configuration" button. 

<p align="center">
  <img width="76" height="79" alt="image" src="https://github.com/user-attachments/assets/ed11d5bb-870b-4d4f-a02d-80e32f188290" />
  <img width="232" height="79" alt="image" src="https://github.com/user-attachments/assets/d6421c1a-3c21-472a-8160-d8c6145f5eaa" />
</p>

Once you press the button, Android Studio will compile the code for Kotlin automatically, this is done thanks to a build.gradle (app) configuration. This configuration is showcased in the next code snippet.


  ```gradle
//plugins
...
id("com.google.protobuf") version "0.9.5"
...

//after Android code section
protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:3.25.1"
    }
    plugins {
    }
    generateProtoTasks {
        all().forEach { task ->
            task.builtins {
                create("java") {
                    option("lite")
                }
                create("kotlin") {
                    option("lite")
                }
            }
        }
    }
}
...

//dependencies
implementation("com.google.protobuf:protobuf-kotlin:4.33.1")
 ```
Once you build your program, all the compiled code will be built on the following directory:

**~\munchkin-app\app\build\generated\sources\proto\debug**

In this folder you will see two additional directories, one for Java and another for Kotlin, both are necessary for the correct functionality of the code. **DO NOT MODIFY OR DELETE THEM**, since the code is autogenerated and any change you do, will be lost; and if you delete it, the protobuf commands will not work. 

The functions that allow protobuf serialization are on these folder. It is fundamental to know the package destination to import the codes from these functions, in this case the main package is **minino** and from this one it divides 5 more packages, **about, analyzer, deauth, rpc, wifispam**

#### Project Structure

You must understand the project structure of the project to understand how the coding flow works. First, you will need to navigate to **com.example.munchkin_app**, so navigate to the next directory.

**~\munchkin-app\app\src\main\java\com.example.munchkin-app**

And once you opened the directory, you will see something like this.

<p align="center">
  <img width="430" height="474" alt="image" src="https://github.com/user-attachments/assets/b6d92919-0a8b-43dd-845f-b871c07a3815" />
</p>

In the image we can appreciate 5 packages: **data, di, navigation, ui and viewmodel**. It is a must to understand what is inside each one to fully comprehend how the project works.

##### Data

After you open this package you will be received with 2 subpackages: **datastore and usb**.

In datastore you will find the configuration CRUD (Create, Read, Update and Delete) for the **SSID Spammer**, this code allows you to save configurations for your SSID Spammer inside the storage of your smartphone.

> NOTE: The actual code saves the information on JSON, but it can be configured to save it in protobuf.

In **usb** you will find the configuration **UsbHelper** that triggers the USB connection, disconnection, ask for permission for USB usage and allows the **UsbSerialManager** to **receive information, process the buffer** from Munchkin and also **write into the serial port** of the usb.

##### DI

Inside DI you will find the modules from Dagger Hilt, modules which sostain the code of UsbHelper and DataStore

##### Navigation

Inside navigation you will find the different files that allow the navigation between screens to work.

Here you can observe the package NavGraphs, this tools are used to set the different navigation routes from all the application. You will find the navigation route for every module, **Home, WiFi, BLE, IoT and Scripts**. 

**NavigationController.kt** is the main heart of navigation, this is the host and all the NavGraphs are inherited from this file.

As the name explains, **Transitions.kt** are the transitions for every screen. In Android, a transition is the process or period of change between a screen and another.

##### UI

The UI package is one of the most complex of this application, here you will find everything that is related to **User Interface (UI)**, this package is divided in 3 subpackages: **Common, Screens and Theme**.

Once you open the UI package inside you will find something like this:

<p align="center">
  <img width="421" height="94" alt="image" src="https://github.com/user-attachments/assets/40e6e6bf-13b0-43c7-aa1d-1824222dbc33" />
</p>

Inside the common package you will find a subpackage named **components** and a Kotlin file named **Utils.kt**. 

**Components** contains reusable composable functions that you can implement, for instance: **ChannelDropMenu.kt, DisplayCardContainer.kt, OptionsButtonSegment.kt** and so many others. 

**Utils.kt** contains also reusable composable functions, however, these functions are not as complex as the **components** ones.

The next subpackage to explore is **screens**, in this package you can observe the different modules that integrates the Munchkin application.

<p align="center">
  <img width="316" height="213" alt="image" src="https://github.com/user-attachments/assets/c3c9fbb8-57bd-4be2-8e85-3e1359f40a2b" />
</p>

Most of the content at the moment is not developed yet, so we are going to focus on **home** and **wifi**.  

The package **home** also contains a components folder which serves to store composable functions that are only specific for home, such as **ConnectionSegmentedButton.kt and UsbDeviceCard.kt**. **Home** also contains **HomeScreen** which acts as the main screen for this module.

The package **WiFi** contains all the applications of the WiFi module, for example: **analyzer, deauth and ssid_spammer**, inside each module are the codelines that lets the UI work. Lets explore analyzer for instance: Inside you will find the **layouts** package, which contain the expanded and compacted view of the UI (For tablets and smartphones) and next to this package is the AnalyzerScreen which acts as the main file of this application.

> NOTE: Every application code follows the same structure, a main file that leads into a layout package that stores the compact and expanded view.

> NOTE: It is important to understand, that the intermediary which stores the code for Munchkin is the UsbViewModel and the UI functionality is the ViewModel designed for each screen.

And lastly, the **UI** package also contains the **theme** package which contains the theme settings that come by default, even though, you can modify as you need it. In this case, the modifications were to the colors: **primary, secondary and background**, the importation of some fonts, responsivity of the font sizes and configurations to the fonts.

##### ViewModel

ViewModels are the intermediary between the business code and the UI. It is necessary because it prevents failures due to recomposition of the screen, since it stores the cache for the status of the activity, for example: When you rotate the screen of your mobile device, the options you selected stay the same thanks to the viewmodel.

In this package are stored all the viewModels for the project, when you first open the viewmodel package, you can observe the packages **screens and usb**. 

<p align="center">
  <img src="https://github.com/user-attachments/assets/1eb3ed55-cda4-41ea-a900-f482fdc3c8bc" width="307" height="72" alt="imagen centrada">
</p>

In the **screens** package remains the ViewModel for every application of each module, at the moment, the only function of these ViewModels is to store the cache of the states for the UI, but later you can add more implementations inside.

As for the **usb** package, inside you will find the **ProtobufRepository.kt** for all the protobuf requests, and also the callback to receive and deserialize the protobuf code in real time. In addition, there is the **DeviceRepository.kt** which creates some variables that store information about the device that is connected. Furthermore, there is the **UsbManager.kt**, this file stores the commands related to the management of the USB. And finally, the **UsbViewModel.kt**, where all the commands related to the bussines logic are stored.


This is an overview explanation for the developing part of Munchkin app, any suggestion or improvement feel free to create an issue or a pull request to improve it.` 

## How to Use the Application.

### Install the App and Connect to Munchkin

First you must open the application, once you do it the application will receive you with a welcome ViewPager.

<p align="center">
  <img width="460" height="190" alt="image" src="https://github.com/user-attachments/assets/b78ef73d-326c-4df1-ae1a-332e4a3430bd" />
</p>

Once you reach the third slide, accept the terms and conditions and it will lead you to the **Home** screen. Here you must connect Munchkin to your smarthphone using one end of the USB to Munchkin and the other to the application. It will detect a **USB device** in the port, click the button that appears with the name of the device and accept the request for USB permission. 

<p align="center">
  <img height="400" alt="image" src="https://github.com/user-attachments/assets/62baa25b-adbc-4790-ae85-ad1b6a5d6a55" />
</p>

After you accept the request your screen will look like this.

<p align="center">
  <img height="400" alt="image" src="https://github.com/user-attachments/assets/e3eefcf5-9ed9-42aa-a211-5c11ee2252ca" />
</p>

> NOTE: Be aware that the Munchkin board must be correctly turned on. Please be sure that after you connect the USB device the **green LED turns ON**

Once your Munchkin is successfully connected to your smarthphone, you can use the applications that are working. Now navigate to the **WiFi** module on the bottom navigation bar.

<p align="center">
  <img width="201" height="37" alt="image" src="https://github.com/user-attachments/assets/75d6af28-9f34-4037-9486-bc6e260ac648" />
</p>

Once you press the button you will see this grid of applications from the WiFi module.

<p align="center">
  <img width="196" height="406" alt="image" src="https://github.com/user-attachments/assets/60a79b41-27fb-4e6e-8988-79316cbed433" />
</p>

The modules that are currently working are: **Analyzer, Deauth and SSID Spammer**.

#### Analyzer

Capture Wi-Fi packets from nearby networks, save them to the SD card or internal storage, and visualize the data. The saved information includes details such as data length, SSID, channel, destination, source, and more.

#### Deauth

Deauth its a set of different types of Denial-of-Service attacks.

**- Broadcast:** A deauthentication attack, a type of denial-of-service attack that disrupts communication between routers and devices by sending deauthentication frames to the network, causing disconnections. This attack exploits the IEEE 802.11 wireless standard’s ability to terminate connections.

**- Rogue AP:** A rogue access point is a wireless access point installed on a secure network without authorization from the network administrator.

**- Combine:** The Combine attack is more efficient because it merges the techniques of both the Broadcast and Rogue AP attacks, maximizing impact.

#### SSID Spammer

Create a list of SSID (Service Set Identifier) names, which are the names of wireless networks, and broadcast them to nearby devices as spam. This app allows you to send multiple SSIDs to overload or confuse nearby wireless networks.

### Navigate into an App

Tap one of the different cards to navigate to the application. For instance: Lets tap **"Analyzer"**. After you tap the button it will lead you to the application screen, where you can configurate the different settings for every application. In this case, we have an information label and a button to pick a channel of radiofrequency.

> NOTE: Some networks will send package in a certain channel, so be aware to choose the correct channel from the network you will capture packets

<p align="center">
  <img width="198" height="412" alt="image" src="https://github.com/user-attachments/assets/dcc5173b-8833-4b63-b4d8-09b0d90054b4" />
</p>

> NOTE: The SD configuration is a dummy, it is planned to be removed since now Munchkin detects if the board has an SD and if it does not have one, it will store the packages in the internal storage of the ESP32-C6.

To make this application work, be sure that Munchkin is correctly connected and press start. Once you press start and the application is running, the UI will notice you, and Munchkin will blink his green LED constantly. In the process, Munchkin will capture and store packets from nearby networks. Once it finishes, the application shows you in which route the **.pcap files** are being stored, and the different networks Munchkin captured packets with some additional information.


The Deauth application follows a similar principle, but first you must scan for nearby networks, after choosing one you will have to pick one type of attack and then press start to run the script.

However, SSID Spammer is a bit different, first thing you must do is to add a name for your SSID's list and also the SSID's for your list, after you added the list of SSID's you must pick one from the dropdown menu and then you press start to start spamming your SSID List

 
## Maintainer

<p align="center">
  <a href="https://www.linkedin.com/company/electroniccats/?originalSubdomain=mx">
    <img src="https://img.shields.io/badge/LinkedIn-0077B5?style=for-the-badge&logo=linkedin&logoColor=white">
  </a>
</p>

<p align="center">
  <a
  href="https://github.com/sponsors/ElectronicCats">
  
  <img  src="https://electroniccats.com/wp-content/uploads/2020/07/Badge_GHS.png"  height="104" />
  
  </a>
</p>

Electronic Cats invests time and resources providing this open source design, please support Electronic Cats and open-source hardware by purchasing products from Electronic Cats!

