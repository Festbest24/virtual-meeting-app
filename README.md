Documentation of VirtualMeeting android application
OverView
Hi Please follow below steps to configure and make changes in application source code.

Installation
Before you start
Current documentation was created to help you with quick installation and configuration of VMeet application. Please, read it carefully to avoid most of potential problems with incorrect configuration.

Below chapter describes how to install Android SDK and Android Studio. You don’t have to install Android Studio, but it’s better. The project can be built without Android Studio, using Gradle and Android SDK. Gradle is a build system used for building final APK file.

Once you will have activated the theme you will need to change some of the pages and settings for optimal performance. That’s why, please, do not contact the Support center beforehand. Read firstly the documentation, implement all the steps following the instructions and only after that, if the issues persist, contact us.

Download and Install Java
If you have downloaded and installed Java JDK then move to next point.

Download & Install Java from here Java JDK​



Download Java by clicking on Java Platform (JDK)

Install Android Studio
Download & Install Android Studio from here Download Android Studio​



Run Android SDK Manager and download necessary SDK packages, make sure that you have installed Android SDK Tools, Android SDK Platform-tools, Android SDK Build-tools, Android Support Repository, Android Support Library and Google Play services.

Now you should be able to open/edit the Android project and build APK.

How to Import Project
To Import project you need to start android studio which you already downloaded, Once android studio start follows below steps to import the project in the android studio.

Follow steps to import project
First of all go to the location where you have placed VMeet code

Please extract the zip file in the same location

Now select “Import Project (Gradle, Eclipse ADT, etc.)” from Android Studio.

Browse location where you have placed VMeet Code and press “OK” button.

Now, wait till android studio complete the Importing project successfully.


Import project

By clicking on import project you will get file browser dialog, select your code location and press OK button.

Customize Project
Project Structure
After importing project successfully you will get project structure like below image



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

In your Project pane, click on the little gear icon ( Gears icon )

Uncheck / De-select the Compact Empty Middle Packages option



Your package directory will now be broken up in individual directories

Individually select each directory you want to rename, and:

Right-click it
Select Refactor
Click on Rename
In the Pop-up dialog, click on Rename Package instead of Rename Directory
Enter the new name and hit Refactor
Click Do Refactor in the bottom
Allow a minute to let Android Studio update all changes
Note: When renaming com in Android Studio, it might give a warning. In such case, select Rename All


Now open your Gradle Build File (build.gradle - Usually app or mobile). Update the applicationId in the defaultConfig to your new Package Name and Sync Gradle, if it hasn’t already been updated automatically:


You may need to change the package= attribute in your manifest.

Clean and Rebuild.

Done! Anyway, Android Studio needs to make this process a little simpler.

Firebase Database Setup
Please refere below link for create new app in firebase.
http://mariechatfield.com/tutorials/firebase/step1.html

Replace your generated google-service.json



Firebase Auth Setup
Please refere below link for enable Email auth in firebase.

https://firebase.google.com/docs/auth/web/email-link-auth

Enable Auth as shown in attached image



Enable Firebase Database
Navigate to the Database section of the Firebase console. You’ll be prompted to select an existing Firebase project



Select a starting mode for your Firebase Security Rules:

Test mode

Good for getting started with the mobile and web client libraries, but allows anyone to read and overwrite your data. After testing, make sure to review the Understand Firebase Realtime Database Rules section.

Enable Firebase Storage
To allow your app access to Firebase Storage, you need to set up permissions in the Firebase console. From your console, click on Storage, and then click on Rules.



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

Want to know more?? Follow this link - https://developers.google.com/admob/android/quick-start​

Sign into your AdMob account.

Create a new App by giving the package name of the app you want to integrate AdMob. Once the App is created, you can find the APP ID on the dashboard which looks like ca-app-pub-XXXXXXXXX~XXXXXXXXX.

Select the newly created App and click on ADD AD UNIT button to create a new ad unit.

Select the ad format and give the ad unit a name.

Once the ad unit is created, you can notice the Ad unit ID on the dashboard. An example of ad unit id look like ca-app-pub-066XXXXXXX/XXXXXXXXXXX



Create ad unit for banner ads
After creating banner ad unit and interstitial ad unit you have to place you adUnit id in string.xml file.

app > res > values > string.xml


Resource Credits
Thanks for provide great material and we are very appreciates to our assets provider.

Google Fonts
Montserrat
