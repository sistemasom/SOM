package com.som.som;

import android.net.NetworkInfo;
import android.support.annotation.IntDef;


public interface DownloadCallback {
    interface Progress {
        int ERROR = -1;
        int CONNECT_SUCCESS = 0;
        int GET_INPUT_STREAM_SUCCESS = 1;
        int PROCESS_INPUT_STREAM_IN_PROGRESS = 2;
        int PROCESS_INPUT_STREAM_SUCCESS = 3;
    }

    void updateFromUpload(String result);

    NetworkInfo getActiveNetworkInfo();

    void onProgressUpdate(int progressCode, int percentComplete);

    void finishUploading();

}
