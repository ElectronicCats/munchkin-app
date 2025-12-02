
# Munchkin Android Application

**Munchkin** is a multi-protocol, multi-band device designed for sniffing, communication, and attacking IoT devices. Munchkin was designed to be controlled via **RPC (Remote Procedure Call)** through an Android device via **USB-Serial**.

[![](https://electroniccats.com/wp-content/uploads/2018/01/fav.png)](https://www.electroniccats.com)

In addition to its innovative integration of an Android application for device management, Protocol Buffers serialization technology, designed and developed by Google, was implemented, allowing for compact, fast, and efficient information transfer.

This project is based on **[usb-serial-for-android](https://github.com/mik3y/usb-serial-for-android)** by **mik3y**, which enables serial device communication via Android, with robust and reliable tools for data transmission.

The application was developed with the Android Studio IDE (Android Studio Otter | 2025.2.1) using Kotlin and Jetpack Compose as the main programming tools. In addition, certain implementations were made to facilitate the development of the application, such as Dagger Hilt. These changes were made in the build.gradle (app) of the document.

> IMPORTANT:
> Please use an OTG Adapter or an USB-C to USB-C Cable for the development of this project.

> NOTE:
>It is highly recommended to compile this project on an Android 11+ device. The reason is that it allows wireless ADB (Android Debug Bridge), a perfect tool to debug USB-Serial.

## Start developing

To begin developing this project, you must follow these steps.

### 1. Download Android Studio

The first step is to download, install and execute **[Android Studio](https://developer.android.com/studio?hl=es-419)**.

Once installed and executed you should see something like this:

<img width="774" height="628" alt="image" src="https://github.com/user-attachments/assets/5cf017d7-f0b3-48c3-8237-18e32059d515" />

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

<img width="1366" height="727" alt="image" src="https://github.com/user-attachments/assets/6528af15-9bab-4cd6-a236-ac66a37eaeb7" />

Now that your screen looks like this, you can continue to the next step to develop on this project.

### 2. Explore munchkin-app in Android Studio

To know how the code of the munchkin-app works, we must explore the files of the project.

#### Protocol Buffers

Protocol Buffers (protobuf) is one of the main implementations made in this project, to see where are the .proto files stored in your project you must set your project files view to "project", then navigate to the following directory route.

**~\munchkin-app\app\src\main\proto**

<img width="431" height="373" alt="image" src="https://github.com/user-attachments/assets/7c3afabe-a226-4a85-a782-c962a6c00f6c" />

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

<img width="76" height="79" alt="image" src="https://github.com/user-attachments/assets/ed11d5bb-870b-4d4f-a02d-80e32f188290" />

<img width="232" height="70" alt="image" src="https://github.com/user-attachments/assets/d6421c1a-3c21-472a-8160-d8c6145f5eaa" />

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

<img width="430" height="474" alt="image" src="https://github.com/user-attachments/assets/b6d92919-0a8b-43dd-845f-b871c07a3815" />

In the image we can appreciate 5 packages: **data, di, navigation, ui and viewmodel**. It is a must to understand what is in each one to fully understand what is inside and what are their functions.

##### Data

After you open this package you will be received with 2 subpackages: **datastore and usb**.

In datastore you will find the configuration CRUD (Create, Read, Update and Delete) for the **SSID Spammer**, this code allows you to save configurations for your SSID Spammer inside the storage of your smartphone.

> NOTE: The actual code saves the information on JSON, but it can be configured to save it in protobuf.

In **usb** you will find the configuration **UsbHelper** that triggers the USB connection, disconnection, ask for permission for USB usage and allow the **UsbSerialManager** to **receive information, process the buffer** from Munchkin and also **write into the serial port** of the usb.

### Activar/desactivar DRC y ERC

Las opciones de DRC y ERC están siempre activas predeterminadamente, para desactivarlas se deberá de eliminar las siguientes líneas del archivo [electroniccats_sch.kibot.yaml](hardware/electroniccats_sch.kibot.yaml).

```yaml

run_erc: true

run_drc: true

```

Esta acción solo correrá cada vez que se haga un release.

Si, además, se busca desactivar el DRC y el ERC cuando se haga push o pull request, es necesario eliminar el archivo [action_drc.yml](.github/workflows/action_drc.yml).

## Creación de Release

Al terminar el proyecto y su revisión, se publicará el primer Release.

Para crear un nuevo Release, presiona el botón de "Create a new release".

Una vez creado el Release, podrás ver la creación de los archivos en la sección de Actions.

Al terminar, los archivos serán generados en el mismo release.

## Elementos para mejorar tu `Readme.md`

Los archivos `Readme.md` se crean con el propósito de hacer visualmente agradable un repositorio para los usuarios que visiten nuestro proyecto, puedes utilizar algunos de los siguientes elementos.

### Código
Quoting code, como lo dice su nombre, se utiliza para añadir código y que se separe del texto plano. Debes de agregar el codigo dentro de ` ``` ```` ` , ademas si agregas el nombre del lenguaje inmediatamente despues de las primeras ` ``` ` las funciones se pondran de color diferenciandolas del resto del codigo. Aquí algunos ejemplos. Puedes encontrar los lenguajes aquí:
https://github.com/github/linguist/blob/master/vendor/README.md
```sh
192.168.0.1
cd Downloads
```
  ```diff
- text in red
+ text in green
! text in orange
# text in gray
@@ text in purple (and bold)@@
```

### Hipervínculos

Usa hipervínculos o links para redirigir a los usuarios a páginas donde puedan conocer más acerca de algún tema o concepto en concreto, hay dos formas de hacer esto:

- [Agregando el link en seguida](https://github.com/ElectronicCats/Template-Project-KiCAD-CI) - Con esta forma deberas de seguir el siguiente formato `[Texto](www.url.com)`. El texto que se mostrará en la página principal del Readme será el que se encuentra dentro de los corchetes y el link de la página deberá de ir de manera inmediata a los corchetes dentro de paréntesis.

- [Agregando el link como referencia] - Al igual que otro tipo de formatos de referencia en este agregas el texto que se mostrará en la página principal del repositorio dentro de corchetes `[Texto]` y al final de tu archivo (de preferencia) agregas la referencia de la siguiente manera: `[Texto]:<www.url.com>`, este no se mostrara en el archivo por lo que es una buena forma de mantener un formato y un orden.  

### Tablas

Las tablas que todos conocemos con filas y columnas. El formato para estas tablas se basa en el uso del símbolo `|`, entonces debes de encerrar las palabras como esto: `|Columna1|` (sin la posibilidad de dos | seguidos), para agregar más columnas basta con dar un espacio y repetir el formato, sin embargo, para añadir filas debes de hacer un salto de línea y repetir el formato de columnas, dejándonos una tabla como la siguiente:

|Columna1|Columna2|
|-|-|
|Fila 1 Columna 1|Fila 2 Columna 2|

Nota: Si agregas en la segunda fila guiones (-) harás que la primera fila se convierta en el encabezado de la tabla.

### Imágenes
Puedes añadir imágenes siempre y cuando estas estén en Internet, si quieres agregar una nueva imagen tambien la puedes arrastrar y soltar en el cuadro de texto (en caso de que edites tu `Readme.md`) directo desde GitHub, esto hará que se guarde tu imagen en una carpeta oculta dentro de tu repositorio.
El formato para agregar imagenes es: `![](www.urlimagen.com)`
Si requieres que al hacer click en tu imagen se redirija a otra pagina usa el siguiente formado `[![](www.urldeimagen.com)](https://www.urlaredirigir.com)`
Es importante agregar `https://` , si no te enviara a una página de GitHub que probablemente no exista.

[![](https://electroniccats.com/wp-content/uploads/2018/01/fav.png)](https://www.electroniccats.com)

### Referencias
Es posible que en la wiki hayas visto numeritos como este --->[^1], pero que significan?

No son más que referencias que puedes hacer para hacer saltos de información e ir directo a la referencia haciendo click en el pequeño número.
[^1]: Soy la referencia :))))

### Emojis :trollface: :shipit:
Solo escribe el código del emoji así: `:EMOJICODE:`.
Aqui la lista de los [EMOJICODEs](https://github.com/ikatyang/emoji-cheat-sheet/blob/master/README.md#github-custom-emoji)

### Listas con checkbox
Usa este formato:
```
- [x] GFM task list 1
- [x] GFM task list 2
- [ ] GFM task list 3
    - [ ] GFM task list 3-1
    - [ ] GFM task list 3-2
    - [ ] GFM task list 3-3
- [ ] GFM task list 4
    - [ ] GFM task list 4-1
    - [ ] GFM task list 4-2
  ```
  Y tendras algo asi: 
- [x] GFM task list 1
- [x] GFM task list 2
- [ ] GFM task list 3
    - [ ] GFM task list 3-1
    - [ ] GFM task list 3-2
    - [ ] GFM task list 3-3
- [ ] GFM task list 4
    - [ ] GFM task list 4-1
    - [ ] GFM task list 4-2

### Otros Elementos

![](https://img.shields.io/github/stars/ElectronicCats/Template-Project-KiCAD-CI?style=for-the-badge)
![](https://img.shields.io/github/forks/ElectronicCats/Template-Project-KiCAD-CI?color=green&style=for-the-badge)

Este tipo de indicadores nos pueden ayudar a identificar diferente información relacionada al proyecto, solo los debes de agregar como una imagen y en el URL  pegar el link correspondiente. 
Los ejemplos de arriba fueron generados con la pagina: Shields.io , solo debes de asegurarte que son para GitHub y que tienen el formato `MarkDown`

- Badges,
En caso de que necesites algun referente a alguna empresa o plataforma puedes usar esta pagina: https://dev.to/envoy_/150-badges-for-github-pnk

[![](https://img.shields.io/badge/LinkedIn-0077B5?style=for-the-badge&logo=linkedin&logoColor=white)](https://www.linkedin.com/company/electroniccats/?originalSubdomain=mx)

- Estadísticas
Utilizando el projecto de este usuario puedes agregar estadísticas del proyecto como estas:

![This repository Stats](https://github-readme-stats.vercel.app/api/pin?username=ElectronicCats&repo=Template-Project-KiCAD-CI&title_color=fff&icon_color=f9f9f9&text_color=9f9f9f&bg_color=151515)

https://github.com/anuraghazra/github-readme-stats

**NOTA**:Son pocas las tarjetas que puedes utilizar con el proyecto de este usuario para repositorios ya que son más dirigidos a perfiles de GitHub.
 
## Maintainer

<a
href="https://github.com/sponsors/ElectronicCats">

<img  src="https://electroniccats.com/wp-content/uploads/2020/07/Badge_GHS.png"  height="104" />

</a>

Electronic Cats invests time and resources providing this open source design, please support Electronic Cats and open-source hardware by purchasing products from Electronic Cats!

[Agregando el link como referencia]: <https://github.com/ElectronicCats/Template-Project-KiCAD-CI>
