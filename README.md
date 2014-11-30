# [GameShow](https://github.com/OnlyInAmerica/Gameshow)

[![Screenshot](http://i.imgur.com/rU8QmaK.png)](http://i.imgur.com/rU8QmaK.png)
[![Screenshot](http://i.imgur.com/e4aJZZh.jpg)](http://i.imgur.com/e4aJZZh.jpg)

[![GameShow](http://img3.wikia.nocookie.net/__cb20131203174704/logopedia/images/d/dc/Get_it_on_Google_Play_badge.svg)](https://play.google.com/store/apps/details?id=pro.dbro.gameshow)

A game show for AndroidTV inspired by Alex Trebek using the wonderful [jservice.io](http://jservice.io/) Jeopardy question API.
Also supports custom questions bundled via JSON. See `./app/src/main/assets/games/default.json`.

## Build & Install

Make sure you've installed the following items from the Android SDK Manager:

+ Android SDK Platform 21 Rev. 1
+ Android SDK Build Tools Rev. 21.1.1
+ Android Support Repository Rev. 9

```
$ git clone https://github.com/OnlyInAmerica/GameShow.git
$ cd GameShow
# You have two options for building:
$ ./gradlew assembleCriminalDebug # Jeopardy theme
$ ./gradlew assembleRoyaltyFreeDebug # Royalty-free theme
$ adb install -r ./app/build/outputs/apk/app-royaltyFree-debug.apk # or app-criminal-debug.apk
```

## Testing

Ensure a compatible Android device is available to adb and run:

```
$ ./gradlew connectedAndroidTest
```

## License

MPL 2.0
