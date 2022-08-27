package com.audiometainfo;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.module.annotations.ReactModule;

import com.facebook.react.bridge.NativeModule;

import com.facebook.react.bridge.ReactContext;


import com.facebook.react.bridge.Callback;
import java.util.Map;
import java.util.HashMap;
import android.util.Log;



import android.content.ContentResolver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import androidx.annotation.Nullable;


import java.io.Console;
import java.io.File;



import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.WritableNativeArray;
import com.facebook.react.bridge.WritableNativeMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;


@ReactModule(name = AudioMetaInfoModule.NAME)
public class AudioMetaInfoModule extends ReactContextBaseJavaModule {
    public static final String NAME = "AudioMetaInfo";


    private boolean getArtistFromSong = false;
    private boolean getDurationFromSong = true;
    private boolean getTitleFromSong = true;
    private boolean getIDFromSong = false;
    private boolean getCoversFromSongs = false;
    private String coversFolder = "/";
    private String covername= "cover";
    private String iconname = "icon";
    private boolean getBluredImages = false;
    private double coversResizeRatio = 1;
    private boolean getIcons = false;
    private int iconsSize = 125;
    private int coversSize = 0;

    private boolean getCoverFromSong = false;
    private String coverFolderpath = "/";
    private boolean getBluredImage = false;
    private double coverResizeRatio = 1;
    private double iconResizeRatio = 1;
    private boolean getIcon = false;
    private int iconSize = 125;
    private int coverSize = 0;

    private int delay = 100;

    private boolean getGenreFromSong = false;
    private boolean getAlbumFromSong = true;
    private int minimumSongDuration = 0;
    private int songsPerIteration = 0;

  private final ReactApplicationContext reactContext;


    public AudioMetaInfoModule(ReactApplicationContext reactContext) {
        super(reactContext);
         this.reactContext = reactContext;
    }

    @Override
    @NonNull
    public String getName() {
        return NAME;
    }


    // Example method
    // See https://reactnative.dev/docs/native-modules-android
    // @ReactMethod
    // public void multiply(double a, double b, Promise promise) {
    //     promise.resolve(a * b);
    // }


    @ReactMethod
    public void getSongByPath(ReadableMap options, final Callback successCallback, final Callback errorCallback) {

        if (options.hasKey("coverFolderpath")) {
            coverFolderpath = options.getString("coverFolderpath");
        }

        if (options.hasKey("cover")) {
            getCoverFromSong = options.getBoolean("cover");
        }

        if (options.hasKey("coverResizeRatio")) {
            coverResizeRatio = options.getDouble("coverResizeRatio");
        }

        if (options.hasKey("iconResizeRatio")) {
            iconResizeRatio = options.getDouble("iconResizeRatio");
        }

        if (options.hasKey("icon")) {
            getIcon = options.getBoolean("icon");
        }
        if (options.hasKey("iconSize")) {
            iconSize = options.getInt("iconSize");
        }

        if (options.hasKey("coverSize")) {
            coverSize = options.getInt("coverSize");
        }

        if (options.hasKey("covername")) {
            covername = options.getString("covername");
        }

        if (options.hasKey("iconname")) {
            iconname = options.getString("iconname");
        }

        WritableArray jsonArray = new WritableNativeArray();
        WritableMap item = new WritableNativeMap();
        if (options.hasKey("songUri")) {
            String songUri = options.getString("songUri");
            String title = "";
            String artist = "";
            long id = -1;
            String album = "";
            String duration = "";

            String[] proj = { MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.ARTIST, MediaStore.Audio.Media.ALBUM,
                    MediaStore.Audio.Media.DURATION, MediaStore.Audio.Media._ID, MediaStore.Audio.Media.DATA };
            Uri musicUris = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
            Cursor cursor = getCurrentActivity().getContentResolver().query(musicUris, proj,
                    MediaStore.Audio.Media.DATA + " like ? ", new String[] { songUri }, null);
            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();
                if (cursor.getColumnIndex(MediaStore.Audio.Media.TITLE) != -1) {
                    title = cursor.getString(0);
                    artist = cursor.getString(1);
                    id = cursor.getLong(4);
                    album = cursor.getString(2);
                    duration = cursor.getString(3);
                    songUri = cursor.getString(5);
                    if (getCoverFromSong) {
                        getCoverByPath(coverFolderpath,covername,iconname, coverResizeRatio,iconResizeRatio, getIcon, iconSize, coverSize,
                                songUri, item);
                   }
                }
            } else {
                String msg = "cursor is either null or empty ";
                Log.e("Musica", msg);
            }

            item.putString("id", String.valueOf(id));
            item.putString("path", String.valueOf(songUri));
            item.putString("title", String.valueOf(title));
            item.putString("author", String.valueOf(artist));
            item.putString("album", String.valueOf(album));
            item.putString("duration", String.valueOf(duration));
            jsonArray.pushMap(item);
            successCallback.invoke(jsonArray);
        } else {

            Log.e("musica", "no ID");
            errorCallback.invoke("No song Uri");
        }

    
    }

    public void getCoverByPath(String coverFolderpath,String covername,
            String iconname, Double coverResizeRatio,Double iconResizeRatio, Boolean getIcon,
            int iconSize, int coverSize, String songPath, WritableMap items) {
        
        MediaMetadataRetriever mmrr = new MediaMetadataRetriever();
        ReactNativeFileManager fcm = new ReactNativeFileManager();
        String encoded = "";
        String blurred = "";
        try {
            mmrr.setDataSource(songPath);
            byte[] albumImageData = mmrr.getEmbeddedPicture();
                
            if (albumImageData != null) {
                Bitmap songImage = BitmapFactory.decodeByteArray(albumImageData, 0, albumImageData.length);
                Bitmap resized = songImage;
                if (coverResizeRatio != 1 && coverSize == 0) {
                    resized = Bitmap.createScaledBitmap(songImage, (int) (songImage.getWidth() * coverResizeRatio),
                            (int) (songImage.getHeight() * coverResizeRatio), true);
                }

                if (coverSize != 0) {
                    resized = Bitmap.createScaledBitmap(songImage, coverSize, coverSize, true);
                }

                try {
                    File covers = new File(coverFolderpath+ File.separator + "covers");
                    boolean success = true;
                    if (!covers.exists()) {
                        success = covers.mkdirs();
                    }
                    if (success) {
                        String pathToImg = covers.getAbsolutePath() + "/" + covername + ".jpg";
                        encoded = fcm.saveImageToStorageAndGetPath(pathToImg, resized);
                        items.putString("cover", "file://" + encoded);
                    } else {
                        // Do something else on failure
                    }

                } catch (Exception e) {
                    // Just let images empty
                    Log.e("error in image", e.getMessage());
                }
                if (getIcon) {
                    try {
                        File icons = new File(coverFolderpath + File.separator + "icons");
                        boolean success = true;
                        if (!icons.exists()) {
                            success = icons.mkdirs();
                        }
                        if (success) {
                            //Bitmap icon = Bitmap.createScaledBitmap(songImage, iconSize, iconSize, true);
                            Bitmap icon = Bitmap.createScaledBitmap(songImage,
                                    (int) (songImage.getWidth() * iconResizeRatio),
                                    (int) (songImage.getHeight() * iconResizeRatio), true);
                            String pathToIcon = icons.getAbsolutePath() + "/" + iconname + ".jpg";
                            encoded = fcm.saveImageToStorageAndGetPath(pathToIcon, icon);
                            items.putString("icon", "file://" + encoded);
                        } else {
                            // Do something else on failure
                        }
                    } catch (Exception e) {
                        // Just let images empty
                        Log.e("error in icon", e.getMessage());
                    }

                }
            }
        } catch (Exception e) {
            Log.e("embedImage", "No embed image");
        }
    }

}
