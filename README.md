# BakingGuru #

## Development Setup ##
The development environment may be configured as follows:
* Clone the repository (https://github.com/ibuttimer/bakingguru) from GitHib in Android Studio
* When prompted to create a Studio project, choose yes and Import the project using the default Gradle wrapper.

### Database population ###
The application supports pre-populating the database with data. This may be done as follows:
* Save the data in json format to the file, <code>assets/baking.json</code>
* Set the <code>PREPOPULATE_DB</code> meta-data value in the manifest to <code>true</code>
* Force the database to be recreated by incrementing <code>ie.ianbuttimer.bakingguru.data.db.BakingDbHelper.VERSION</code>, or uninstalling and reinstalling the application.

The <code>assets/baking.json</code> file contains sample data.

## Usage ##
### Application Widget ###
Widgets may be added to the home screen to display the ingredients of individual recipes. Widgets may be configured as follows:
* Long press on the home screen
* Tap the Widgets icon
* Locate the BakingGuru widget and tap it
* In the widget configuration screen, tap the required recipe to select it
* Tap the save icon in the bottom right hand corner of the screen to add the widget to the home screen





