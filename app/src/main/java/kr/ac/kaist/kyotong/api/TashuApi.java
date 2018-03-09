package kr.ac.kaist.kyotong.api;

import android.app.Application;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import kr.ac.kaist.kyotong.model.TashuStationModel;
import kr.ac.kaist.kyotong.utils.RequestManager;


public class TashuApi extends ApiBase {
    private final static String TAG = "TashuApi";

    private TashuStationModel createTashu(String name, int id) {
        TashuStationModel tashuStationModel = new TashuStationModel();
        tashuStationModel.name = name;
        tashuStationModel.id = id;
        return tashuStationModel;
    }


    /**
     * Init
     */

    public TashuApi(Application application) {


        RequestManager rc = new RequestManager("/mapAction.do", "GET", "https");
        rc.setBaseUrl("www.tashu.or.kr");
        rc.addBodyValue("process", "statusMapView");
        rc.doRequest();
        response = rc.getResponse_body();

        Log.d(TAG, response);
    }

    /**
     * 결과를 반환합니다.
     */

    public ArrayList<TashuStationModel> getResult() {
        ArrayList<TashuStationModel> tashuStationModels = new ArrayList<TashuStationModel>();

        tashuStationModels.add(createTashu("카이마루(학부식당) 앞", 21));
        tashuStationModels.add(createTashu("창의학습관 앞", 22));
        tashuStationModels.add(createTashu("정문", 23));
        tashuStationModels.add(createTashu("후문", 25));
        tashuStationModels.add(createTashu("서쪽 쪽문", 105));
        tashuStationModels.add(createTashu("다솜관(신축)", 106));
        tashuStationModels.add(createTashu("세종관", 107));
        tashuStationModels.add(createTashu("유성구청", 31));

        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray jsonArray = jsonObject.getJSONArray("markers");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject tashuObject = jsonArray.getJSONObject(i);
                int id = tashuObject.getInt("kiosk_no");
                int cnt_rentable = tashuObject.getInt("cntRentable");
                int cnt_total = tashuObject.getInt("cntRackTotal");

                for (TashuStationModel tashuStationModel : tashuStationModels) {

                    if (tashuStationModel.id == id) {
                        tashuStationModel.cnt_rentable = cnt_rentable;
                        tashuStationModel.cnt_total = cnt_total;
                        break;
                    }

                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        /**
         *
         */
        for (TashuStationModel tashuStationModel : tashuStationModels) {
            if (tashuStationModel.cnt_rentable == -1 || tashuStationModel.cnt_total == -1) {
                return null;
            }
        }

        return tashuStationModels;
    }
}
