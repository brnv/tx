#!/usr/bin/env bash

ant debug

adb install -r bin/telegramx-debug.apk

adb shell am start -a android.intent.action.MAIN \
    -n com.brnv.telegram/com.brnv.telegram.MainActivity
