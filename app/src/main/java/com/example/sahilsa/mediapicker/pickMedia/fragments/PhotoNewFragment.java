package com.example.sahilsa.mediapicker.pickMedia.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.sahilsa.mediapicker.R;
import com.github.florent37.camerafragment.widgets.CameraSwitchView;
import com.github.florent37.camerafragment.widgets.RecordButton;
import com.otaliastudios.cameraview.CameraListener;
import com.otaliastudios.cameraview.CameraLogger;
import com.otaliastudios.cameraview.CameraOptions;
import com.otaliastudios.cameraview.CameraView;
import com.otaliastudios.cameraview.SessionType;
import com.otaliastudios.cameraview.Size;

import java.io.File;

public class PhotoNewFragment extends Fragment implements View.OnClickListener {
    RecordButton record_button;
    CameraSwitchView cameraSwitchView;
    private CameraView camera;

    private boolean mCapturingPicture;
    private boolean mCapturingVideo;

    // To show stuff in the callback
    private Size mCaptureNativeSize;
    private long mCaptureTime;

    public static PhotoNewFragment newInstance() {
        PhotoNewFragment photoFragment = new PhotoNewFragment();
        return photoFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);

        CameraLogger.setLogLevel(CameraLogger.LEVEL_VERBOSE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_camera, container, false);
        record_button = v.findViewById(R.id.record_button);
        cameraSwitchView = v.findViewById(R.id.front_back_camera_switcher);
        camera = v.findViewById(R.id.camera);

        camera.addCameraListener(new CameraListener() {
            public void onCameraOpened(CameraOptions options) {
            }

            public void onPictureTaken(byte[] jpeg) {
                onPicture(jpeg);
            }

            @Override
            public void onVideoTaken(File video) {
                super.onVideoTaken(video);
                onVideo(video);
            }
        });

        record_button.setOnClickListener(this);
        cameraSwitchView.setOnClickListener(this);
        return v;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.record_button:
                if (camera.getSessionType() == SessionType.PICTURE)
                    capturePhoto();
                else
                    captureVideo();
                break;

            case R.id.front_back_camera_switcher:
                toggleCamera();
                break;
        }
    }

    private void onPicture(byte[] jpeg) {
        mCapturingPicture = false;
        long callbackTime = System.currentTimeMillis();
        if (mCapturingVideo) {
            return;
        }

        // This can happen if picture was taken with a gesture.
        if (mCaptureTime == 0) mCaptureTime = callbackTime - 300;
        if (mCaptureNativeSize == null) mCaptureNativeSize = camera.getPictureSize();


        mCaptureTime = 0;
        mCaptureNativeSize = null;

        Toast.makeText(getActivity(), "Picture Taken", Toast.LENGTH_SHORT).show();
    }

    private void onVideo(File video) {
        mCapturingVideo = false;

        Toast.makeText(getActivity(), "Video taken "+video.getAbsolutePath().toString(), Toast.LENGTH_SHORT).show();
    }

    private void capturePhoto() {
        if (mCapturingPicture) return;
        mCapturingPicture = true;
        mCaptureTime = System.currentTimeMillis();
        mCaptureNativeSize = camera.getPictureSize();
        camera.capturePicture();
    }

    private void captureVideo() {
        if (camera.getSessionType() != SessionType.VIDEO) {
            return;
        }
        if (mCapturingPicture || mCapturingVideo) return;
        mCapturingVideo = true;
        camera.startCapturingVideo(null, 8000);
    }

    private void toggleCamera() {
        if (mCapturingPicture) return;
        switch (camera.toggleFacing()) {
            case BACK:
                break;

            case FRONT:
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        camera.start();
    }

    @Override
    public void onPause() {
        super.onPause();
        camera.stop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        camera.destroy();
    }

    public void setVideoView() {
        camera.setSessionType(SessionType.VIDEO);
    }

    public void setPictureView() {
        camera.setSessionType(SessionType.PICTURE);
    }
}



