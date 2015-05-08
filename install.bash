#!/usr/bin/env bash

rm -r gen
rm -r bin

ant debug && adb install -r bin/telegramx-debug.apk && \
adb shell am start -a android.intent.action.MAIN \
    -n com.brnv.telegram/com.brnv.telegram.AuthorizationActivity
