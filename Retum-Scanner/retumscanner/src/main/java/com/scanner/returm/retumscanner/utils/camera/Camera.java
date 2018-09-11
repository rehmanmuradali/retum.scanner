package com.scanner.returm.retumscanner.utils.camera;


public abstract class Camera {

    private static android.hardware.Camera mCamera;


    /**
     * A safe way to get an instance of the Camera object.
     */


    private static android.hardware.Camera getCameraInstance(int cameraFacing) {
        android.hardware.Camera c = null;
        try {
            c = android.hardware.Camera.open(cameraFacing); // attempt to get a Camera instance
        } catch (Exception e) {
            // Camera is not available (in use or does not exist)
        }
        return c; // returns null if camera is unavailable
    }


    public static void closeCamera() {
        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
        }

    }

    public static void autoFocus() {
        if (mCamera != null)
            try {
                mCamera.autoFocus(null);
            } catch (Exception ignored) {

            }
    }


    /**
     * focusMode: android.hardware.Camera.Parameters
     * cameraFacing: android.hardware.Camera.CameraInfo.FACING_FRONT or FACING_BACK
     */
    public static class CameraBuilder {
        private String focusMode = android.hardware.Camera.Parameters.FOCUS_MODE_AUTO;
        private int cameraFacing = android.hardware.Camera.CameraInfo.CAMERA_FACING_BACK;
        private int displayOrientation = 0;

        public CameraBuilder() {

        }

        /**
         * @param focusMode default: android.hardware.Camera.Parameters.FOCUS_MODE_AUTO
         * @return CameraBuilder
         */
        public CameraBuilder setFocusMode(String focusMode) {
            this.focusMode = focusMode;
            return this;
        }

        /**
         * @param cameraFacing default : android.hardware.Camera.CameraInfo.CAMERA_FACING_BACK
         * @return CameraBuilder
         */
        public CameraBuilder setCameraFacing(int cameraFacing) {
            this.cameraFacing = cameraFacing;
            return this;
        }


        public CameraBuilder setDisplayOrientation(int orientation) {
            this.displayOrientation = orientation;
            return this;
        }

        public android.hardware.Camera build() {
            mCamera = getCameraInstance(cameraFacing);
            if (mCamera == null) return null;
            android.hardware.Camera.Parameters params = mCamera.getParameters();
            params.setFocusMode(focusMode);
            try {
                mCamera.setParameters(params);
            } catch (Exception e) {
                // Camera is not available (in use or does not exist)
            }
            mCamera.setDisplayOrientation(displayOrientation);
            return mCamera;
        }

    }

}
