package com.example.pdf;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.OpenableColumns;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.ScrollBar;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;

import java.io.File;

public class MainActivity extends AppCompatActivity implements OnPageChangeListener {

    private final String title = "投资合同";
    private String pdfFileName = "sample.pdf";
    private final String pdfFolder = "PDFFolder/";//存储于sd下面的文件夹
    private PDFView pdfView;
    private ScrollBar scrollBar;
    private int pageNumber = 1;

    private DownloadTask task;
    private String url = "http://7xvrxf.com1.z0.glb.clouddn.com/sample.pdf";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        pdfViewSetting();
    }

    private void pdfViewSetting(){

        pdfView = (PDFView) findViewById(R.id.pdfView);
        scrollBar = (ScrollBar) findViewById(R.id.scrollBar);
        pdfView.setScrollBar(scrollBar);


        verifyStoragePermissions(this);
        task = new DownloadTask();
        task.execute(url,pdfFolder,pdfFileName); //"Folder"后面要加"/"

/*
        pdfView.fromAsset(pdfFileName)
                //.pages(0, 2, 1, 3, 3, 3) //all pages are displayed by default
                //.enableSwipe(true)
                .enableDoubletap(true)
                .swipeVertical(true)
                .defaultPage(1)
                .showMinimap(false)
                //.onDraw(onDrawListener)
                //.onLoad(onLoadCompleteListener)
                .onPageChange(this)
                //.onError(onErrorListener)
                .load();

        */
    }

    @Override
    public void onPageChanged(int page, int pageCount) {
        pageNumber = page;
        setTitle(String.format("%s %s / %s", title, page, pageCount));
    }


    class DownloadTask extends AsyncTask<String, Void, File> {

        private LoadingProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new LoadingProgressDialog(MainActivity.this,
                    R.style.transparentFrameWindowStyle, "文件正在加载中，请稍等");
            dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {

                @Override
                public void onCancel(DialogInterface arg0) {
                    if (task != null && !task.isCancelled()) {
                        task.cancel(true);
                    }
                }
            });
            dialog.show();
        }

        @Override
        protected File doInBackground(String... params) {
            File file;
            try {
                HttpDownloader downloader = new HttpDownloader();
//                file = downloader.downloadReturnFile(
//                        BaseActivity.URL_BASE + "/user/"
//                                + params[0] + "/invest/" + params[1]
//                                + "/contract",
//                        "east/",
//                        params[2]);
//                file = downloader.downloadReturnFile(
//                        " http://jyxt.zrelicai.com/api/v2/contract/getContractAppointInfoId/17", "east/", params[2]);

                file = downloader.downloadReturnFile(params[0],params[1],params[2]);

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
            return file;
        }

        @Override
        protected void onPostExecute(File result) {
            super.onPostExecute(result);
            dialog.dismiss();
            if (result != null) {

                Log.e("FilePath",result.getAbsolutePath());
                pdfView.fromFile(result)
                        .defaultPage(pageNumber)
                        .onPageChange(MainActivity.this)
                        .swipeVertical(true)
                        .showMinimap(false)
                        .load();;
            } else {
                Toast.makeText(getApplicationContext(),"文件加载失败",Toast.LENGTH_LONG).show();
            }
        }
    }


    //caused by android.system.errnoexception open failed eacces (permission denied)解决方案，安卓6.0（API23）权限问题
    //在API23+以上，不止要在AndroidManifest.xml里面添加权限
    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE };

    /**
     * Checks if the app has permission to write to device storage
     *
     * If the app does not has permission then the user will be prompted to
     * grant permissions
     *
     * @param activity
     */
    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE);
        }
    }


    //=======================unuse===================
    private void displayFromUri(Uri uri) {
        pdfFileName = getFileName(uri);

        pdfView.fromUri(uri)
                .defaultPage(pageNumber)
                .onPageChange(this)
                .swipeVertical(true)
                .showMinimap(false)
                .load();
    }
    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }
        if (result == null) {
            result = uri.getLastPathSegment();
        }
        return result;
    }

}
