# Q Tests
Demonstrates some bugs with Android Q preview, for issues:
https://issuetracker.google.com/issues/130042030

https://issuetracker.google.com/issues/130085810

Compiled APK in [Release](/Release) - requires Q preview

Check the message advising if ACCESS_MEDIA_LOCATION permission is granted. If not, request it via
the REQ PERMISSION button. Should see a runtime permission dialog (if this is a runtime permission).

Once the permission is acquired, tap TAKE PHOTO to launch the camera. On result, the text will
display the photo's orientation and shutter speed, if the EXIF data wasn't redacted.