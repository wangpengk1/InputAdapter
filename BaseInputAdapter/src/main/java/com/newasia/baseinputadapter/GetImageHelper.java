package com.newasia.baseinputadapter;

import android.Manifest;
import android.app.Activity;
import android.content.ClipData;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import java.io.File;
import java.util.List;


import static android.app.Activity.RESULT_OK;

public class GetImageHelper {
    private Activity mContext;
    private static final String PERMISSION_CAMERA = Manifest.permission.CAMERA;
    private static final String PERMISSION_WRITE = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    private static final int REQUEST_PERMISSION_CAMERA = 0x001;
    private static final int REQUEST_PERMISSION_WRITE = 0x002;
    private static final int CROP_REQUEST_CODE = 0x003;


    private Uri mTakePhotoUri;
    private Uri mCorpUri;

    private boolean mIsCrop;
    private onTakeImageResult mOnResult;

    public Activity getContext() {
        return mContext;
    }

    private static GetImageHelper mInstance;

    public static GetImageHelper getInstance(boolean isCrop, Activity context) {
        if (mInstance != null) {
            if (mInstance.getContext() != context) {
                mInstance = new GetImageHelper(isCrop, context);
            }
        } else mInstance = new GetImageHelper(isCrop, context);

        return mInstance;
    }

    private GetImageHelper(boolean isCrop, Activity context) {
        mContext = context;
        mIsCrop = isCrop;
    }


    public interface onTakeImageResult {
        void getImageResult(File imgeFile);
    }


    public void popupGetImageDlg(Activity context, onTakeImageResult result) {
        mOnResult = result;
        new ActionSheetDialog(context)
                .builder()
                .setCancelable(true)
                .setCanceledOnTouchOutside(true)
                .addSheetItem("拍照", ActionSheetDialog.SheetItemColor.Blue,
                        new ActionSheetDialog.OnSheetItemClickListener() {
                            @Override
                            public void onClick(int which) {
                                takePhoto();
                            }
                        })
                .addSheetItem("相册", ActionSheetDialog.SheetItemColor.Blue,
                        new ActionSheetDialog.OnSheetItemClickListener() {
                            @Override
                            public void onClick(int which) {
                                choosePhoto();
                            }
                        }).show();
    }


    private void takePhoto() {

        //打开照相机
        Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        mTakePhotoUri = PickUtils.getOutputMediaFileUri(mContext, "temp.jpg");
        openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, mTakePhotoUri);

        //Android7.0添加临时权限标记，此步千万别忘了
        openCameraIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        mContext.startActivityForResult(openCameraIntent, REQUEST_PERMISSION_CAMERA);

    }


    private void choosePhoto() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        mContext.startActivityForResult(photoPickerIntent, REQUEST_PERMISSION_WRITE);
    }


    private String getRealFilePath(final Context context, final Uri uri) {
        if (null == uri) return null;
        final String scheme = uri.getScheme();
        String data = null;
        if (scheme == null)
            data = uri.getPath();
        else if (ContentResolver.SCHEME_FILE.equals(scheme)) {
            data = uri.getPath();
        } else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
            Cursor cursor = context.getContentResolver().query(uri, new String[]{MediaStore.Images.ImageColumns.DATA}, null, null, null);
            if (null != cursor) {
                if (cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                    if (index > -1) {
                        data = cursor.getString(index);
                    }
                }
                cursor.close();
            }
        }
        return data;
    }


    /**
     * 裁剪图片
     */
    private void cropPhoto(Uri uri) {

        mCorpUri = PickUtils.getOutputMediaFileUri(mContext,"corp.jpg");




        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 800);
        intent.putExtra("outputY", 800);
        intent.putExtra("return-data", false);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mCorpUri);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true);
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setClipData(ClipData.newRawUri(MediaStore.EXTRA_OUTPUT, uri));

        List<ResolveInfo> resInfoList =  mContext.getPackageManager()
                .queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        for (ResolveInfo resolveInfo : resInfoList) {
            String packageName = resolveInfo.activityInfo.packageName;
            mContext.grantUriPermission(packageName, mCorpUri, Intent.FLAG_GRANT_READ_URI_PERMISSION
                    | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        }


        mContext.startActivityForResult(intent, CROP_REQUEST_CODE);
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_PERMISSION_CAMERA:
                    if(mIsCrop)
                    {
                        cropPhoto(mTakePhotoUri);
                    }else retrunResult(mTakePhotoUri);
                    break;
                case REQUEST_PERMISSION_WRITE:
                    cropPhoto(data.getData());
                    break;
                case CROP_REQUEST_CODE:
                    retrunResult(mCorpUri);
                    break;
                default:
                    break;
            }
        }
    }


    private void retrunResult(Uri uri)
    {
        String path = PickUtils.getPath(mContext,uri);
        mOnResult.getImageResult(new File(path));
    }

}