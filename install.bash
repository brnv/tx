#!/usr/bin/env bash

rm -r ./bin/
mkdir ./bin/

ant debug && adb install -r bin/telegramx-debug.apk && \
adb shell am start -a android.intent.action.MAIN \
    -n com.brnv.telegram/com.brnv.telegram.MainActivity
