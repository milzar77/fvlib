#!/bin/sh

java --class-path=./fvlib-bl-0.0.1-SNAPSHOT.jar:./fvlib-res-0.0.1-SNAPSHOT.jar:./fvlib-web-0.0.1-SNAPSHOT.jar:./\
fvlib-common-0.0.1-SNAPSHOT.jar:./fvlib-util-0.0.1-SNAPSHOT.jar \
 com.blogspot.fravalle.lib.importer.bookmarks.BrowserBookmarkReader /home/goldenplume/Documenti/BookmarksBackup/FV-FREE fragment false 1500000
