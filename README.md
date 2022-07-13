Documentation of VirtualMeeting android application
OverView
Hi Please follow below steps to configure and make changes in application source code.
Installation
Before you start
•	Current documentation was created to help you with quick installation and configuration of VMeet application. Please, read it carefully to avoid most of potential problems with incorrect configuration.
•	Below chapter describes how to install Android SDK and Android Studio. You don’t have to install Android Studio, but it’s better. The project can be built without Android Studio, using Gradle and Android SDK. Gradle is a build system used for building final APK file.
•	Once you will have activated the theme you will need to change some of the pages and settings for optimal performance. That’s why, please, do not contact the Support center beforehand. Read firstly the documentation, implement all the steps following the instructions and only after that, if the issues persist, contact us.
Download and Install Java
If you have downloaded and installed Java JDK then move to next point.
Download & Install Java from here Java JDK
 ![image](https://user-images.githubusercontent.com/105209103/178787548-53faa2c2-58dc-4576-aca6-dcb1fed7cad8.png)

Download Java by clicking on Java Platform (JDK)
Install Android Studio
Download & Install Android Studio from here Download Android Studio
 ![image](https://user-images.githubusercontent.com/105209103/178787764-887dbffd-08d9-4e1e-8130-33604899d48b.png)

Run Android SDK Manager and download necessary SDK packages, make sure that you have installed Android SDK Tools, Android SDK Platform-tools, Android SDK Build-tools, Android Support Repository, Android Support Library and Google Play services.
Now you should be able to open/edit the Android project and build APK.
How to Import Project
To Import project you need to start android studio which you already downloaded, Once android studio start follows below steps to import the project in the android studio.
Follow steps to import project
1.	First of all go to the location where you have placed VMeet code
2.	Please extract the zip file in the same location
3.	Now select “Import Project (Gradle, Eclipse ADT, etc.)” from Android Studio.
4.	Browse location where you have placed VMeet Code and press “OK” button.
5.	Now, wait till android studio complete the Importing project successfully.
 ![image](https://user-images.githubusercontent.com/105209103/178787810-f7d85d3c-ee50-438b-93ac-636e965908fa.png)

Import project
By clicking on import project you will get file browser dialog, select your code location and press OK button.
Customize Project
Project Structure
After importing project successfully you will get project structure like below image
 ![image](https://user-images.githubusercontent.com/105209103/178787853-42685701-dcc2-4543-a26c-445464d94786.png)

Project Structure
Within each Android app module, files are shown in the following groups:
manifest
Contains the AndroidManifest.xml file.
java
Contains the Java source code files, separated by package names, including JUnit test code.
res
Contains all non-code resources, such as XML layouts, UI strings, and bitmap images, divided into corresponding sub-directories. For more information about all possible resource types, see Providing Resources.
To know more about project structure please go through this link Android Project Structure
Change App icon
Place your app icon inside mipmap folder -> app\src\main\res\mipmap\
Icon name should be “ic_launcher.png”
Change application package name
In Android Studio, you can do this:
For example, if you want to change com.example.app to my.awesome.game, then:
1.	In your Project pane, click on the little gear icon (   )
2.	Uncheck / De-select the Compact Empty Middle Packages option
 ![image](https://user-images.githubusercontent.com/105209103/178787933-cf5368a9-72bf-4341-ba1a-c485231070c4.png)

3.	Your package directory will now be broken up in individual directories
4.	Individually select each directory you want to rename, and:
o	Right-click it
o	Select Refactor
o	Click on Rename
o	In the Pop-up dialog, click on Rename Package instead of Rename Directory
o	Enter the new name and hit Refactor
o	Click Do Refactor in the bottom
o	Allow a minute to let Android Studio update all changes
o	Note: When renaming com in Android Studio, it might give a warning. In such case, select Rename All
 ![image](https://user-images.githubusercontent.com/105209103/178787992-18a7d7f3-2d2a-4364-bfc4-9c2f40010b86.png)

5.	Now open your Gradle Build File (build.gradle - Usually app or mobile). Update the applicationId in the defaultConfig to your new Package Name and Sync Gradle, if it hasn’t already been updated automatically:
 ![image](https://user-images.githubusercontent.com/105209103/178788025-a928208c-2d15-4d14-a013-7b413aecb715.png)

6.	You may need to change the package= attribute in your manifest.
7.	Clean and Rebuild.
8.	Done! Anyway, Android Studio needs to make this process a little simpler.
Firebase Database Setup
Please refere below link for create new app in firebase.
http://mariechatfield.com/tutorials/firebase/step1.html
Replace your generated google-service.json
 ![image](https://user-images.githubusercontent.com/105209103/178788083-e13b86cb-e359-4310-ba4e-ad3a877bcb05.png)

Firebase Auth Setup
Please refere below link for enable Email auth in firebase.
https://firebase.google.com/docs/auth/web/email-link-auth
Enable Auth as shown in attached image
 ![image](https://user-images.githubusercontent.com/105209103/178788128-0cb02e02-8d64-4a3c-85f0-b7ef5bc211d4.png)

Enable Firebase Database
Navigate to the Database section of the Firebase console. You’ll be prompted to select an existing Firebase project
 ![image](https://user-images.githubusercontent.com/105209103/178788153-70cdeb23-7117-4fc4-ace1-dc10383409f8.png)

Select a starting mode for your Firebase Security Rules:
Test mode
Good for getting started with the mobile and web client libraries, but allows anyone to read and overwrite your data. After testing, make sure to review the Understand Firebase Realtime Database Rules section.
Enable Firebase Storage
To allow your app access to Firebase Storage, you need to set up permissions in the Firebase console. From your console, click on Storage, and then click on Rules.
 ![image](https://user-images.githubusercontent.com/105209103/178788227-674286da-7222-4595-8d9a-58ab3679ff82.png)

Paste the rule below and publish.
`service firebase.storage {` 
    `match /b/{bucket}/o {`
`		match /{allPaths=**} {`
		    `allow read, write: if true;`
	    `}`
    `}`
`}`
This will allow read and write access to your Firebase storage.
Admob Configuration
How to Integrate Google AdMob in your App
Introduction
AdMob is a multi platform mobile ad network that allows you to monetize your android app. By integrating AdMob you can start earning right away. It is very useful particularly when you are publishing a free app and want to earn some money from it.
Integrating AdMob is such an easy task that it takes no more than 5mins. In this article, we’ll build a simple app with two screens to show the different types of ads that AdMob supports.
Creating Ad Units
NOTE: AdMob admin interface changes quite often. The below steps to create Ad Unit IDs might differ from time to time.
Want to know more?? Follow this link - https://developers.google.com/admob/android/quick-start
1.	Sign into your AdMob account.
2.	Create a new App by giving the package name of the app you want to integrate AdMob. Once the App is created, you can find the APP ID on the dashboard which looks like ca-app-pub-XXXXXXXXX~XXXXXXXXX.
3.	Select the newly created App and click on ADD AD UNIT button to create a new ad unit.
4.	Select the ad format and give the ad unit a name.
5.	Once the ad unit is created, you can notice the Ad unit ID on the dashboard. An example of ad unit id look like ca-app-pub-066XXXXXXX/XXXXXXXXXXX
 ![image](https://user-images.githubusercontent.com/105209103/178788316-b17b9da7-7cde-43e0-a09f-e252375f8a3a.png)

Create ad unit for banner ads
After creating banner ad unit and interstitial ad unit you have to place you adUnit id in string.xml file.
app > res > values > string.xml
 ![image](https://user-images.githubusercontent.com/105209103/178788352-f5c837d7-c7fd-479d-be48-f548e5835783.png)

Resource Credits
Thanks for provide great material and we are very appreciates to our assets provider.
Google Fonts
Montserrat

