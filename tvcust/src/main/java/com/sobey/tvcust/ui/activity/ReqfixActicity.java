package com.sobey.tvcust.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.dd.CircularProgressButton;
import com.sobey.common.common.CommonNet;
import com.sobey.common.common.MyPlayer;
import com.sobey.common.helper.CropHelper;
import com.sobey.common.view.BundleView2;
import com.sobey.tvcust.R;
import com.sobey.tvcust.common.AppConstant;
import com.sobey.tvcust.common.AppData;
import com.sobey.tvcust.common.AppVali;
import com.sobey.tvcust.entity.CommonEntity;
import com.sobey.tvcust.ui.dialog.DialogRecord;
import com.sobey.tvcust.ui.dialog.DialogPopupPhoto;
import com.sobey.tvcust.ui.dialog.DialogReqfixChoose;

import org.greenrobot.eventbus.EventBus;
import org.xutils.http.RequestParams;

public class ReqfixActicity extends BaseAppCompatActicity implements View.OnClickListener, CropHelper.CropInterface {

    private CropHelper cropHelper = new CropHelper(this);

    private View lay_reqfix_quekind;
    private DialogReqfixChoose chooseDialog;
    private DialogPopupPhoto popup;
    private DialogRecord recordDialog;
    private BundleView2 bundleView;
    private MyPlayer player = new MyPlayer();

    private CircularProgressButton btn_go;
    private TextView text_question_name;
    private EditText edit_reqfix_detail;

    private String categoryId;

    private static final int RESULT_QUESTION = 0xf102;
    private static final int RESULT_VIDEO_RECORDER = 0xf101;

//    private String pathphoto;
//    private String pathvideo;
//    private String pathvoice;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
//        outState.putString("pathphoto", pathphoto);
//        outState.putString("pathvideo", pathvideo);
//        outState.putString("pathvoice", pathvoice);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
//            pathphoto = savedInstanceState.getString("pathphoto");
//            pathvideo = savedInstanceState.getString("pathvideo");
//            pathvoice = savedInstanceState.getString("pathvoice");
        }
        setContentView(R.layout.activity_reqfix);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initBase();
        initView();
        initCtrl();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (player!=null) player.onDestory();
        if (popup!=null) popup.dismiss();
        if (chooseDialog!=null) chooseDialog.dismiss();
        if (recordDialog!=null) recordDialog.dismiss();
    }

    private void initBase() {
        chooseDialog = new DialogReqfixChoose(this);
        chooseDialog.setOnHardListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseDialog.hide();
                Intent intent = new Intent(ReqfixActicity.this, QuestionActivity.class);
                intent.putExtra("type","1");
                startActivityForResult(intent,RESULT_QUESTION);
            }
        });
        chooseDialog.setOnSoftListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseDialog.hide();
                Intent intent = new Intent(ReqfixActicity.this, QuestionActivity.class);
                intent.putExtra("type","0");
                startActivityForResult(intent,RESULT_QUESTION);
            }
        });
        popup = new DialogPopupPhoto(this);
        popup.setOnCameraListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popup.hide();
                cropHelper.startCamera();
            }
        });
        popup.setOnPhotoListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popup.hide();
                cropHelper.startPhoto();
            }
        });
        recordDialog = new DialogRecord(this);
    }

    private void initView() {
        lay_reqfix_quekind = findViewById(R.id.lay_reqfix_quekind);
        bundleView = (BundleView2) findViewById(R.id.bundle_reqfix);
        btn_go = (CircularProgressButton) findViewById(R.id.btn_go);
        edit_reqfix_detail = (EditText) findViewById(R.id.edit_reqfix_detail);
        text_question_name = (TextView) findViewById(R.id.text_question_name);

        findViewById(R.id.img_reqfix_photo).setOnClickListener(this);
        findViewById(R.id.img_reqfix_vidio).setOnClickListener(this);
        findViewById(R.id.img_reqfix_voice).setOnClickListener(this);
    }

    private void initCtrl() {
        lay_reqfix_quekind.setOnClickListener(this);
        btn_go.setOnClickListener(this);
        btn_go.setIndeterminateProgressMode(true);
        bundleView.setDelEnable(true);
        bundleView.setOnBundleClickListener(new BundleView2.OnBundleClickListener() {
            @Override
            public void onPhotoDelClick(View v) {
//                pathphoto = "";
            }

            @Override
            public void onVideoDelClick(View v) {
//                pathvideo = "";
            }

            @Override
            public void onVoiceDelClick(View v) {
//                pathvoice = "";
            }

            @Override
            public void onPhotoShowClick(String path) {
                Intent intent = new Intent(ReqfixActicity.this, PhotoActivity.class);
                intent.putExtra("url", path);
                startActivity(intent);
            }

            @Override
            public void onVideoShowClick(String path) {
                Intent intent = new Intent(ReqfixActicity.this, VideoActivity.class);
                intent.putExtra("url", path);
                startActivity(intent);
            }

            @Override
            public void onVoiceShowClick(String path) {
                player.setUrl(path);
                player.play();
            }
        });
        recordDialog.setOnRecordListener(new DialogRecord.OnRecordListener() {
            @Override
            public void onRecordFinish(String voiceFilePath, int voiceTimeLength) {
                Log.e("liao", "complete:" + voiceFilePath + "  length:" + voiceTimeLength);
//                pathvoice = voiceFilePath;
                bundleView.addVoice(voiceFilePath);
            }
        });
        player.setOnPlayerListener(new MyPlayer.OnPlayerListener() {
            @Override
            public void onStart() {
                Log.e("liao", "start");
            }

            @Override
            public void onCompleted() {
                Log.e("liao", "stop");
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.lay_reqfix_quekind:
                chooseDialog.show();
                break;
            case R.id.img_reqfix_photo:
                popup.show();
                break;
            case R.id.img_reqfix_vidio:
                Intent intent = new Intent(ReqfixActicity.this, VideoRecorderActivity.class);
                startActivityForResult(intent, RESULT_VIDEO_RECORDER);
                break;
            case R.id.img_reqfix_voice:
                recordDialog.show();
                break;
            case R.id.btn_go:

                String detail = edit_reqfix_detail.getText().toString();
                String[] photoPaths = bundleView.getPhotoPaths();
                String[] videoPaths = bundleView.getVideoPaths();
                String[] voicePaths = bundleView.getVoicePaths();


                String msg = AppVali.reqfix_commit(categoryId,detail);
                if (msg != null) {
                    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
                }else {
                    btn_go.setProgress(50);

                    RequestParams params = new RequestParams(AppData.Url.reqfix);
                    params.addHeader("token", AppData.App.getToken());
                    params.addBodyParameter("categoryId", categoryId);
                    params.addBodyParameter("content", detail);
                    CommonNet.samplepost(params,CommonEntity.class,new CommonNet.SampleNetHander(){
                        @Override
                        public void netGo(int code, Object pojo, String text, Object obj) {

                            Toast.makeText(ReqfixActicity.this,text,Toast.LENGTH_SHORT).show();

                            btn_go.setProgress(100);
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
//                                    Intent intent = new Intent();
//                                    setResult(RESULT_OK,intent);
                                    EventBus.getDefault().post(AppConstant.EVENT_UPDATE_ORDERLIST);
                                    finish();
                                }
                            }, 800);
                        }

                        @Override
                        public void netSetError(int code, String text) {
                            Toast.makeText(ReqfixActicity.this,text,Toast.LENGTH_SHORT).show();
                            btn_go.setProgress(-1);
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    btn_go.setProgress(0);
                                }
                            }, 800);
                        }
                    });
                }

//                btn_go.setProgress(50);
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        btn_go.setProgress(100);
//                    }
//                }, 2000);

                Log.e("liao","photo");
                for (String path:photoPaths) {
                    Log.e("liao",path);
                }
                Log.e("liao","video");
                for (String path:videoPaths) {
                    Log.e("liao", path);
                }
                Log.e("liao","voice");
                for (String path:voicePaths) {
                    Log.e("liao", path);
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        cropHelper.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case RESULT_VIDEO_RECORDER:
                if (resultCode == RESULT_OK) {
                    // 成功
                    String pathvideo = data.getStringExtra("path");
                    Log.e("liao", pathvideo);
//                    Toast.makeText(this, "存储路径为:" + pathvideo, Toast.LENGTH_SHORT).show();
                    // 通过路径获取第一帧的缩略图并显示
//                    Bitmap bitmap = VideoUtils.createVideoThumbnail(pathvideo);
                    bundleView.addVideo(pathvideo);
                } else {
                    // 失败
                }
                break;
            case RESULT_QUESTION:
                if (resultCode == RESULT_OK) {
                    String question = data.getStringExtra("name");
                    String categoryId = data.getStringExtra("id");
                    text_question_name.setText(question);
                    this.categoryId = categoryId;
                }
                break;
        }
    }

    @Override
    public void cropResult(String path) {
        Log.e("liao", path);
//        this.pathphoto = path;

//        Bitmap bitmap = BitmapFactory.decodeFile(path);
        bundleView.addPhoto(path);
    }
}
