package udesk.sdk.demo.activity;

import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import udesk.sdk.demo.R;

public class SelectPhotoFileActivity extends AppCompatActivity {

    private ActivityResultLauncher<Intent> photoPickerLauncher;
    private ActivityResultLauncher<Intent> filePickerLauncher;
    private final List<Uri> imageItems = new ArrayList<>();
    private ImageAdapter mAdapter;
    private TextView mTv;

    public static void start(Context context) {
        Intent starter = new Intent(context, SelectPhotoFileActivity.class);
        //starter.putExtra();
        context.startActivity(starter);
    }

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_select_photo_file);

        mTv = findViewById(R.id.tv);

        ListView listView = findViewById(R.id.lv); // 确保你已在 XML 中添加 ListView
        mAdapter = new ImageAdapter(this, imageItems);
        listView.setAdapter(mAdapter);

        findViewById(R.id.btn_photo).setOnClickListener(view -> {
            // 启动系统照片选择器
            Intent intent = new Intent(MediaStore.ACTION_PICK_IMAGES);
            // 设置最大选择数量
            intent.putExtra(MediaStore.EXTRA_PICK_IMAGES_MAX, 9);
            photoPickerLauncher.launch(intent);
        });

        findViewById(R.id.btn_file).setOnClickListener(view -> {
            // 创建文件选择意图
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            // 允许选择所有文件类型
            intent.setType("*/*");
            // 允许多选
            //intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            // 设置可选文件类型限制（可选）
            /*String[] mimeTypes = {
                    "application/pdf", // PDF 文件
                    "application/msword", // DOC
                    "application/vnd.openxmlformats-officedocument.wordprocessingml.document", // DOCX
                    "application/vnd.ms-excel", // XLS
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", // XLSX
                    "text/plain", // 文本文件
                    "image/*", // 所有图片
                    "audio/*", // 所有音频
                    "video/*" // 所有视频
            };
            intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);*/
            // 注册结果接收器
            filePickerLauncher.launch(intent);
        });

        photoPickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> handlePhotoPickerResult(result.getResultCode(), result.getData())
        );

        filePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> handleSelectedFiles(result.getResultCode(), result.getData())
        );
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void handlePhotoPickerResult(int resultCode, Intent data) {
        if (resultCode != RESULT_OK || data == null) return;

        List<Uri> selectedUris = new ArrayList<>();

        if (data.getClipData() != null) {
            // 处理多选
            ClipData clipData = data.getClipData();
            for (int i = 0; i < clipData.getItemCount(); i++) {
                selectedUris.add(clipData.getItemAt(i).getUri());
            }
        } else if (data.getData() != null) {
            // 处理单选
            selectedUris.add(data.getData());
        }

        imageItems.clear();
        imageItems.addAll(selectedUris);
        mAdapter.notifyDataSetChanged();
    }

    private void handleSelectedFiles(int resultCode, Intent data) {
        if (resultCode != RESULT_OK || data == null) return;

        List<Uri> selectedFiles = new ArrayList<>();

        if (data.getClipData() != null) {
            // 处理多选文件
            ClipData clipData = data.getClipData();
            for (int i = 0; i < clipData.getItemCount(); i++) {
                selectedFiles.add(clipData.getItemAt(i).getUri());
            }
        } else if (data.getData() != null) {
            // 处理单选文件
            selectedFiles.add(data.getData());
        }

        // 处理所有选中的文件
        for (Uri fileUri : selectedFiles) {
            // 获取文件名
            String fileName = queryFileName(fileUri);
            // 获取文件类型
            String fileType = getContentResolver().getType(fileUri);

            Log.d("FilePicker", "Selected file: " + fileName + " (" + fileType + ")");

            // 获取永久访问权限（设备重启后仍可访问）
            //getContentResolver().takePersistableUriPermission(fileUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);

            // 使用文件
            //useFileContent(fileUri);

            mTv.setText("Selected file: " + fileName + " (" + fileType + ")");
        }
    }

    private String queryFileName(Uri uri) {
        String name = "Unknown File";
        try (Cursor cursor = getContentResolver().query(
                uri,
                null,
                null,
                null,
                null)) {
            if (cursor != null && cursor.moveToFirst()) {
                int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                if (nameIndex != -1) {
                    name = cursor.getString(nameIndex);
                }
            }
        }
        return name;
    }

    private void useFileContent(Uri fileUri) {
        try (InputStream inputStream = getContentResolver().openInputStream(fileUri)) {
            // 读取文件内容
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                // 处理文件内容...
            }
        } catch (IOException e) {
            Log.e("FilePicker", "Error reading file: " + e.getMessage());
        }
    }

}
