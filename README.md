# FHG APP

[![Build Status](https://travis-ci.org/JBamberger/fhg-android-app.svg?branch=master)](https://travis-ci.org/JBamberger/fhg-android-app)

Dieses Repository enthält den Code der FHG-App (früher VPlan-App).

Die App zeigt den Vertretungsplan und weitere nützliche Informationen des
Friedrich-Hecker-Gymnasiums Radolfzell für Mobilgeräte optimiert an. Der Vertretungsplan kann so
konfiguriert werden, dass er nur ausgewählte Klassen anzeigt.

Die App ist nur für Schüler, Eltern und Lehrer des FHGs gedacht.

[Download](https://play.google.com/store/apps/details?id=xyz.jbapps.vplan)

## Release Checklist

1. Increment `versionCode` and `versionName` in `build.gradle`
2. Update `CHANGELOG.md` with the build number, date, and list of changes
3. Build signed and optimized release APK
4. Perform manual testing on device
   - Check if migration from previous app versions works
   - Check if fresh start works
5. Create PR and merge when all checks pass
6. Create a tag with the value of `versionName` at the merge commit
7. Build signed apk bundle and upload to Google Play
8. Download and test Device-specific version
9. Publish release
