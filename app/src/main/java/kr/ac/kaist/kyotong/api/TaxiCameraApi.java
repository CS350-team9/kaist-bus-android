package kr.ac.kaist.kyotong.api;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import kr.ac.kaist.kyotong.utils.RequestManager;

/**
 * Created by yearnning on 15. 2. 6..
 */
public class TaxiCameraApi {

    public static final String API_NAME = "TaxiCameraApi";

    private Bitmap mBitmap = null;

    public TaxiCameraApi() {

        /**
         *
         */
        RequestManager rm = new RequestManager("/example", "GET", "https");
        rm.setBaseUrl("dl.dropboxusercontent.com/u/106104942");
        rm.doRequest();

        /**
         *
         */
        String strBase64 = rm.getResponse_body();
        byte[] decodedString = Base64.decode(strBase64, Base64.DEFAULT);
        this.mBitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

    }


    public Bitmap getResult() {
        return this.mBitmap;
    }

}
