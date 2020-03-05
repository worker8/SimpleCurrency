./gradlew assembleRelease
zipalign -v -p 4 ./app/build/outputs/apk/release/app-release-unsigned.apk simple-currency-unsigned.apk
apksigner sign --ks junrong.keystore --out simple-currency-signed.apk simple-currency-unsigned.apk
rm simple-currency-unsigned.apk
