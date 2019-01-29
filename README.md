# WeCast 2.0 Mobile

WeCast 2.0 is android IPTV/OTT application for android devices. <br/>
To use this project you will need latest version of android studio because project is migrated to AndroidX.
> AndroidX is the open-source project that the Android team uses to develop, test, package, version and release libraries within [Jetpack](https://developer.android.com/jetpack).
>
> Learn more about [AndroidX](https://developer.android.com/jetpack/androidx/).

## Submodules

- [wecast-core](https://bitbucket.org/harunagic/wecast-2.0-core)
- [wecast-player](https://bitbucket.org/harunagic/wecast-2.0-player)

## Dependencies
- [Dagger 2](https://github.com/google/dagger)
- [Java RX](https://github.com/ReactiveX/RxJava)
- [Stream](https://github.com/aNNiMON/Lightweight-Stream-API)
- [Retrofit 2](https://square.github.io/retrofit/)
- [OkHttp Logging](https://github.com/square/okhttp/tree/master/okhttp-logging-interceptor)
- [Gson](https://github.com/google/gson)
- [Calligraphy](https://github.com/chrisjenx/Calligraphy)
- [Glide](https://github.com/bumptech/glide)
- [Exo Player](https://github.com/google/ExoPlayer)
- [Timber](https://github.com/JakeWharton/timber)
- [HockeyApp](https://github.com/bitstadium/HockeySDK-Android)
- [LeakCanary](https://github.com/square/leakcanary)

# Installation

Clone this repository and import into **Android Studio**

>git clone git@bitbucket.org:harunagic/wecast-2.0-mobile.git

##  Build variants

For develop we are using **dev** build variant,<br/>
for demo we are using **demo** build variant.<br/>
<br/>
Other build variants are for customers :
- nettv
- brasilnet
- intv
- trendtv

## Generating signed APK

To crate signed app from Android Studio follow this steps:

1.  _**Build**_  menu
2.  _**Generate Signed APK...**_
3.  Fill in the keystore information  _(you only need to do this once manually and then let Android Studio remember it)_


Generated **.apk** file will be placed in root project inside **build** folder
