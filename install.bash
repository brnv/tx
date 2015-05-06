#!/usr/bin/env bash

mkdir -p ./bin/

ant debug && adb install -r bin/telegramx-debug.apk && \
adb shell am start -a android.intent.action.MAIN \
    -n com.brnv.telegram/com.brnv.telegram.LoginActivity

rm -r ./bin/
