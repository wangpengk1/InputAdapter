package com.newasia.baseinputadapter;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;


import androidx.core.content.FileProvider;

import com.xuexiang.xaop.annotation.Permission;
import com.xuexiang.xaop.consts.PermissionConsts;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;



import static android.app.Activity.RESULT_OK;

public class GetImageHelper
{
    private Activity mContext;
    private static final String PERMISSION_CAMERA = Manifest.permission.CAMERA;
    private static final String PERMISSION_WRITE = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    private static final int REQUEST_PERMISSION_CAMERA = 0x001;
    private static final int REQUEST_PERMISSION_WRITE = 0x002;
    private static final int CROP_REQUEST_CODE = 0x003;


    private File captureFile;
    private File cropFile;
    private File rootFile;

    private boolean mIsCrop;
    private onTakeImageResult mOnResult;

    public Activity getContext()
    {
        return mContext;
    }

    private static GetImageHelper mInstance;

    public static GetImageHelper getInstance(boolean isCrop,Activity context)
    {
        if(mInstance!=null)
        {
            if(mInstance.getContext()!= context)
            {
                mInstance = new GetImageHelper(isCrop,context);
            }
        }
        else  mInstance = new GetImageHelper(isCrop,context);

        return mInstance;
    }

    private GetImageHelper(boolean isCrop,Activity context)
    {
        mContext = context;
        mIsCrop = isCrop;
        rootFile= new File(context.getFilesDir() + "/TakePhotoPic");
        if (!rootFile.exists())
        {
            rootFile.mkdirs();
        }
    }

    public interface onTakeImageResult
    {
        void getImageResult(File imgeFile);
    }

    public void popupGetImageDlg(Activity context, onTakeImageResult result)
    {
        mOnResult = result;
        new ActionSheetDialog(context)
                .builder()
                .setCancelable(true)
                .setCanceledOnTouchOutside(true)
                .addSheetItem("拍照", ActionSheetDialog.SheetItemColor.Blue,
                        new ActionSheetDialog.OnSheetItemClickListener() {
                            @Override
                            public void onClick(int which)
                            {
//                                if (EasyPermissions.hasPermissions(mContext, PERMISSION_CAMERA, PERMISSION_WRITE)) {
//                                    takePhoto();
//                                } else {
//                                    EasyPermissions.requestPermissions(((Activity)mContext), "need camera permission", REQUEST_PERMISSION_CAMERA, PERMISSION_CAMERA, PERMISSION_WRITE);
//                                }
                                takePhoto();
                            }
                        })
                .addSheetItem("相册", ActionSheetDialog.SheetItemColor.Blue,
                        new ActionSheetDialog.OnSheetItemClickListener() {
                            @Override
                            public void onClick(int which)
                            {
//                                if (EasyPermissions.hasPermissions(mContext, PERMISSION_WRITE)) {
//                                    choosePhoto();
//                                } else {
//                                    EasyPermissions.requestPermissions(((Activity)mContext), "need camera permission", REQUEST_PERMISSION_WRITE, PERMISSION_WRITE);
//                                }
                                choosePhoto();
                            }
                        }).show();
    }



    @Permission(value = {PermissionConsts.CAMERA,PermissionConsts.STORAGE})
    private void takePhoto() {
        //用于保存调用相机拍照后所生成的文件
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            return;
        }
        captureFile = new File(rootFile, "temp.jpg");
        //跳转到调用系统相机
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //判断版本 如果在Android7.0以上,使用FileProvider获取Uri
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            Uri contentUri = FileProvider.getUriForFile(mContext,mContext.getPackageName()+".fileprovider", captureFile);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, contentUri);
        } else {
            //否则使用Uri.fromFile(file)方法获取Uri
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(captureFile));
        }
        mContext.startActivityForResult(intent, REQUEST_PERMISSION_CAMERA);
    }


    @Permission(value = {PermissionConsts.STORAGE})
    private void choosePhoto() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        mContext.startActivityForResult(photoPickerIntent, REQUEST_PERMISSION_WRITE);
    }


    private  String getRealFilePath( final Context context, final Uri uri ) {
        if ( null == uri ) return null;
        final String scheme = uri.getScheme();
        String data = null;
        if ( scheme == null )
            data = uri.getPath();
        else if ( ContentResolver.SCHEME_FILE.equals( scheme ) ) {
            data = uri.getPath();
        } else if ( ContentResolver.SCHEME_CONTENT.equals( scheme ) ) {
            Cursor cursor = context.getContentResolver().query( uri, new String[] { MediaStore.Images.ImageColumns.DATA }, null, null, null );
            if ( null != cursor ) {
                if ( cursor.moveToFirst() ) {
                    int index = cursor.getColumnIndex( MediaStore.Images.ImageColumns.DATA );
                    if ( index > -1 ) {
                        data = cursor.getString( index );
                    }
                }
                cursor.close();
            }
        }
        return data;
    }


    private String getPathFromUri(Uri uri, Context context) {
        if (uri == null) {
            return null;
        }

        if (uri.getAuthority().compareToIgnoreCase("com.newasia.MyCar")==0)
        {
            return Environment.getExternalStorageDirectory()+uri.getPath().replace("/external_path", "");
        }
        else
        {
            return getRealFilePath(context,uri);
        }
    }


    /**
     * 裁剪图片
     */
    private void cropPhoto(Uri uri)
    {
        if (!mIsCrop)
        {
            String strPath = getPathFromUri(uri, mContext);
            if (strPath !=null)
            {
                mOnResult.getImageResult(new File(strPath));
            }

            return;
        }
        cropFile = new File(rootFile, "avatar.jpg");
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 800);
        intent.putExtra("outputY", 800);
        intent.putExtra("return-data", false);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(cropFile));
        intent.putExtra("outputFormat", Bitmap.CompressFormat.PNG.toString());
        intent.putExtra("noFaceDetection", true);
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        mContext.startActivityForResult(intent, CROP_REQUEST_CODE);
    }




    /**
     * @param path
     * @return
     */
    private String saveImage(String path) {
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            return null;
        }
        Bitmap bitmap = BitmapFactory.decodeFile(path);
        try {
            FileOutputStream fos = new FileOutputStream(cropFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
            return cropFile.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_PERMISSION_CAMERA:
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        Uri contentUri = FileProvider.getUriForFile(mContext, mContext.getPackageName()+".fileprovider", captureFile);
                        cropPhoto(contentUri);
                    } else {
                        cropPhoto(Uri.fromFile(captureFile));
                    }
                    break;
                case REQUEST_PERMISSION_WRITE:
                    cropPhoto(data.getData());
                    break;
                case CROP_REQUEST_CODE:
                    mOnResult.getImageResult(cropFile);
                    break;
                default:
                    break;
            }
        }
    }


}
