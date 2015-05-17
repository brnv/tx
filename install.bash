#!/usr/bin/env bash

rm -r gen
rm -r bin

ant debug && adb install -r bin/txmessenger-debug.apk && \
adb shell am start -a android.intent.action.MAIN \
    -n com.brnv.txmessenger/com.brnv.txmessenger.MainActivity
