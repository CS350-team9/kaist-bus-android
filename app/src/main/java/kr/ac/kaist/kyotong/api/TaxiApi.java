package kr.ac.kaist.kyotong.api;

import java.util.ArrayList;
import java.util.HashMap;

import kr.ac.kaist.kyotong.R;
import kr.ac.kaist.kyotong.model.TaxiCompanyModel;

/**
 * Created by yearnning on 15. 1. 2..
 */
public class TaxiApi extends ApiBase {

    private final static String TAG = "TaxiApi";
    private ArrayList<TaxiCompanyModel> taxiCompanies;
    private String title;

    /**
     * Init
     */
    public TaxiApi(int title_id) {

        taxiCompanies = new ArrayList<TaxiCompanyModel>();

        /**
         *
         */
        switch (title_id) {

            case R.string.tab_kaist_taxi:
                title = "기계공학동 택시승강장";
                taxiCompanies.add(createTaxi("국토교통부 전국택시콜", "1333"));
                taxiCompanies.add(createTaxi("양반콜", "042-586-8000"));
                taxiCompanies.add(createTaxi("한빛콜", "042-540-8282"));
                taxiCompanies.add(createTaxi("한밭콜", "042-242-8800"));
                taxiCompanies.add(createTaxi("대전나비콜", "042-333-1000"));
                taxiCompanies.add(createTaxi("노란지붕콜", "042-585-8282"));
                taxiCompanies.add(createTaxi("운불련호출", "042-633-5757"));
                taxiCompanies.add(createTaxi("찬양호출", "042-524-9333"));
                taxiCompanies.add(createTaxi("고급콜", "042-583-3000"));
                taxiCompanies.add(createTaxi("신탄콜", "042-936-6000"));
                taxiCompanies.add(createTaxi("송강천사콜", "042-936-1004"));
                taxiCompanies.add(createTaxi("(장애인)사랑나눔콜", "042-226-0533"));
                break;

            case R.string.tab_korea_taxi:
            case R.string.tab_yonsei_taxi:
                title = "서울지역 콜택시";
                taxiCompanies.add(createTaxi("국토교통부 전국택시콜", "1333"));
                taxiCompanies.add(createTaxi("서울통합브랜드택시 엔콜", "02-999-9999"));
                break;

            case R.string.tab_cnu_taxi:
            case R.string.tab_hannam_taxi:
                title = "대전지역 콜택시";
                taxiCompanies.add(createTaxi("국토교통부 전국택시콜", "1333"));
                taxiCompanies.add(createTaxi("양반콜", "042-586-8000"));
                taxiCompanies.add(createTaxi("한빛콜", "042-540-8282"));
                taxiCompanies.add(createTaxi("한밭콜", "042-242-8800"));
                taxiCompanies.add(createTaxi("대전나비콜", "042-333-1000"));
                taxiCompanies.add(createTaxi("노란지붕콜", "042-585-8282"));
                taxiCompanies.add(createTaxi("운불련호출", "042-633-5757"));
                taxiCompanies.add(createTaxi("찬양호출", "042-524-9333"));
                taxiCompanies.add(createTaxi("고급콜", "042-583-3000"));
                taxiCompanies.add(createTaxi("신탄콜", "042-936-6000"));
                taxiCompanies.add(createTaxi("송강천사콜", "042-936-1004"));
                taxiCompanies.add(createTaxi("(장애인)사랑나눔콜", "042-226-0533"));
                break;
        }


    }

    private TaxiCompanyModel createTaxi(String name, String phone) {
        TaxiCompanyModel taxiCompanyModel = new TaxiCompanyModel();
        taxiCompanyModel.name = name;
        taxiCompanyModel.phone = phone;
        return taxiCompanyModel;
    }

    public HashMap<String, Object> getResult() {

        HashMap<String, Object> map = new HashMap<String, Object>();

        map.put("title", title);
        map.put("taxiCompanies", taxiCompanies);

        return map;
    }
}
