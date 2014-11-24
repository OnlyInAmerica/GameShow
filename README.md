# [GameShow](https://github.com/OnlyInAmerica/Gameshow)

[![Screenshot](http://i.imgur.com/rU8QmaK.png)](http://i.imgur.com/rU8QmaK.png)
[![Screenshot](http://i.imgur.com/e4aJZZh.jpg)](http://i.imgur.com/e4aJZZh.jpg)


A game show for AndroidTV inspired by Alex Trebek using the wonderful [jservice.io](http://jservice.io/) Jeopardy question API.
Also supports custom questions bundled via JSON. See `./app/src/main/assets/games/default.json`.

## Build & Install

Make sure you've installed the following items from the Android SDK Manager:

+ Android SDK Platform 21 Rev. 1
+ Android SDK Build Tools Rev. 21.1.1
+ Android Support Repository Rev. 9

```
$ git clone https://github.com/OnlyInAmerica/GamesShow.git
$ cd GameShow
$ ./gradlew assembleDebug
$ adb install -r ./app/build/outputs/apk/app-debug.apk
```

## License

MPL 2.0
