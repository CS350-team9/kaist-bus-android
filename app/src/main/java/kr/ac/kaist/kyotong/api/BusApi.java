package kr.ac.kaist.kyotong.api;

import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import kr.ac.kaist.kyotong.R;
import kr.ac.kaist.kyotong.model.BusModel;
import kr.ac.kaist.kyotong.model.BusStationModel;
import kr.ac.kaist.kyotong.model.BusTimeModel;
import kr.ac.kaist.kyotong.utils.DateUtils;


/**
 * Created by yearnning on 14. 12. 29..
 */
public class BusApi extends ApiBase {

    private final static String TAG = "BusApi";

    private ArrayList<BusStationModel> busStationModels;
    private ArrayList<BusModel> buses = new ArrayList<BusModel>();


    /**
     * Init
     */
    public BusApi(int title_id) {

        /**
         * init busStations
         */
        busStationModels = createStations(title_id);

        /**
         * create Today Buses.
         */
        ArrayList<BusModel> todayBuses = createTodayBuses(title_id, busStationModels);
        addBusTimeInBusStations(busStationModels, todayBuses, true);
        buses.addAll(todayBuses);

        /**
         * create Tomorrow Buses.
         */
        ArrayList<BusModel> tomorrowBuses = createTomorrowBuses(title_id, busStationModels);
        addBusTimeInBusStations(busStationModels, tomorrowBuses, false);

        /**
         * SORT
         */
        sort(buses, busStationModels);

        /**
         * addHeader Info
         */
        addHeaderInBusStations(busStationModels);

    }

    private ArrayList<BusModel> createTodayBuses(int title_id, ArrayList<BusStationModel> busStationModels) {

        boolean holiday;
        if (DateUtils.beforeFourAM()) {
            holiday = DateUtils.isHoliday(-1);
            Log.d(TAG, "Today Bus by Yesterday");
        } else {
            holiday = DateUtils.isHoliday(0);
            Log.d(TAG, "Today Bus by Today");
        }

        if (holiday) {
            return createHolidayBuses(title_id, busStationModels);
        } else {
            return createWeekdayBuses(title_id, busStationModels);
        }
    }

    private ArrayList<BusModel> createTomorrowBuses(int title_id, ArrayList<BusStationModel> busStationModels) {

        boolean holiday;
        if (DateUtils.beforeFourAM()) {
            holiday = DateUtils.isHoliday(0);
        } else {
            holiday = DateUtils.isHoliday(1);
        }

        if (holiday) {
            return createHolidayBuses(title_id, busStationModels);
        } else {
            return createWeekdayBuses(title_id, busStationModels);
        }
    }

    private void addBusTimeInBusStations(ArrayList<BusStationModel> busStationModels, ArrayList<BusModel> buses, boolean today) {
        if (buses.size() > 0) {
            for (BusModel busModel : buses) {
                for (int i = 0; i < busModel.busDepartureTimes.size(); i++) {
                    if (!today) {
                        busModel.getDepartureTime(i).makeTomorrowBusTime();
                    }
                    busModel.getBusDepartureStation(i).addDepartureTime(busModel.getDepartureTime(i));
                }
            }
        } else {
            for (BusStationModel busStationModel : busStationModels) {
                BusTimeModel busTimeModel = new BusTimeModel();
                busTimeModel.indicator = "주말 및 공휴일은 운행하지 않습니다";
                if (today)
                    busTimeModel.setTime(24, 0);
                else
                    busTimeModel.setTime(48, 0);

                busStationModel.addDepartureTime(busTimeModel);
            }
        }

    }

    private void addHeaderInBusStations(ArrayList<BusStationModel> busStaions) {
        for (BusStationModel busStationModel : busStaions) {
            busStationModel.addHeader();
        }
    }

    private ArrayList<BusStationModel> createStations(int title_id) {

        ArrayList<BusStationModel> busStationModels = new ArrayList<>();

        switch (title_id) {

            /**
             * KAIST
             */
            case R.string.tab_kaist_olev:
                busStationModels.add(BusStationModel.newInstance("카이마루", 0, BusStationModel.newLocationInstance(36.373428, 127.359221)));
                busStationModels.add(BusStationModel.newInstance("스컴", 30, BusStationModel.newLocationInstance(36.372784, 127.361855)));
                busStationModels.add(BusStationModel.newInstance("창의관", 60, BusStationModel.newLocationInstance(36.370849, 127.362381)));
                busStationModels.add(BusStationModel.newInstance("의과학센터", 110, BusStationModel.newLocationInstance(36.370193, 127.365932)));
                busStationModels.add(BusStationModel.newInstance("파팔라도", 140, BusStationModel.newLocationInstance(36.369545, 127.369612)));
                busStationModels.add(BusStationModel.newInstance("정문", 200, BusStationModel.newLocationInstance(36.366357, 127.363614)));
                busStationModels.add(BusStationModel.newInstance("오리연못", 230, BusStationModel.newLocationInstance(36.367420, 127.362574)));
                busStationModels.add(BusStationModel.newInstance("교육지원동", 270, BusStationModel.newLocationInstance(36.370020, 127.360728)));
                busStationModels.add(BusStationModel.newInstance("아름관(간이)", 310, BusStationModel.newLocationInstance(36.373484, 127.356651)));
                busStationModels.add(BusStationModel.newInstance("카이마루", 360, null));
                break;

            case R.string.tab_kaist_wolpyeong:
                busStationModels.add(BusStationModel.newInstance("강당", 0, null));
                busStationModels.add(BusStationModel.newInstance("본관", 30, null));
                busStationModels.add(BusStationModel.newInstance("오리연못", 60, null));
                busStationModels.add(BusStationModel.newInstance("충남대앞(일미식당)", 97, BusStationModel.newLocationInstance(36.361533, 127.345736), R.drawable.station_kaist_wolpyeong_3));
                busStationModels.add(BusStationModel.newInstance("월평역(1번출구)", 128, BusStationModel.newLocationInstance(36.358109, 127.364356), R.drawable.station_kaist_wolpyeong_4));
                busStationModels.add(BusStationModel.newInstance("갤러리아(대일빌딩)", 189, BusStationModel.newLocationInstance(36.352054, 127.376309), R.drawable.station_kaist_wolpyeong_5));
                busStationModels.add(BusStationModel.newInstance("청사시외(택시승강장)", 232, BusStationModel.newLocationInstance(36.361140, 127.379472), R.drawable.station_kaist_wolpyeong_6));
                busStationModels.add(BusStationModel.newInstance("월평역(3번출구)", 282, BusStationModel.newLocationInstance(36.358587, 127.363199), R.drawable.station_kaist_wolpyeong_7));
                busStationModels.add(BusStationModel.newInstance("오리연못", 300, null));
                busStationModels.add(BusStationModel.newInstance("본관", 330, null));
                busStationModels.add(BusStationModel.newInstance("강당", 360, null));
                break;

            case R.string.tab_kaist_sunhwan:
                busStationModels.add(BusStationModel.newInstance("문지캠퍼스(화암방향)", 0, null));
                busStationModels.add(BusStationModel.newInstance("화암기숙사", 90, null));
                busStationModels.add(BusStationModel.newInstance("문지캠퍼스(본원방향)", 180, null));
                busStationModels.add(BusStationModel.newInstance("로덴하우스", 225, null));
                busStationModels.add(BusStationModel.newInstance("본원(대덕캠퍼스)", 270, null));
                busStationModels.add(BusStationModel.newInstance("교수아파트", 315, null));
                busStationModels.add(BusStationModel.newInstance("문지", 360, null));
                break;

            /**
             * CNU
             */
            case R.string.tab_cnu_a:
                busStationModels.add(BusStationModel.newInstance("공대2호관", 0, BusStationModel.newLocationInstance(36.363867, 127.345156)));
                busStationModels.add(BusStationModel.newInstance("경상대학", 30, BusStationModel.newLocationInstance(36.367479, 127.345628)));
                busStationModels.add(BusStationModel.newInstance("중앙도서관(시내버스)", 60, BusStationModel.newLocationInstance(36.369578, 127.346916)));
                busStationModels.add(BusStationModel.newInstance("학생생활관", 90, BusStationModel.newLocationInstance(36.372558, 127.346165)));
                busStationModels.add(BusStationModel.newInstance("농생대", 120, BusStationModel.newLocationInstance(36.369016, 127.352055)));
                busStationModels.add(BusStationModel.newInstance("중앙도서관(서문방향)", 150, BusStationModel.newLocationInstance(36.369448, 127.345725)));
                busStationModels.add(BusStationModel.newInstance("예술대", 180, BusStationModel.newLocationInstance(36.370537, 127.343558)));
                busStationModels.add(BusStationModel.newInstance("음악2호관", 210, BusStationModel.newLocationInstance(36.374001, 127.343804)));
                busStationModels.add(BusStationModel.newInstance("수의대", 240, BusStationModel.newLocationInstance(36.376757, 127.343558)));
                busStationModels.add(BusStationModel.newInstance("체육관입구", 270, BusStationModel.newLocationInstance(36.371902, 127.342957)));
                busStationModels.add(BusStationModel.newInstance("공동실습관", 300, BusStationModel.newLocationInstance(36.368904, 127.341444)));
                busStationModels.add(BusStationModel.newInstance("사회대(한누리회관)", 330, BusStationModel.newLocationInstance(36.366865, 127.342635)));
                busStationModels.add(BusStationModel.newInstance("공대2호관", 360, null));
                break;

            case R.string.tab_cnu_b:
                busStationModels.add(BusStationModel.newInstance("공대2호관", 0, BusStationModel.newLocationInstance(36.363867, 127.345156)));
                busStationModels.add(BusStationModel.newInstance("사회대(한누리회관)", 30, BusStationModel.newLocationInstance(36.366865, 127.342635)));
                busStationModels.add(BusStationModel.newInstance("공동실습관", 60, BusStationModel.newLocationInstance(36.368904, 127.341444)));
                busStationModels.add(BusStationModel.newInstance("음악2호관", 90, BusStationModel.newLocationInstance(36.374001, 127.343804)));
                busStationModels.add(BusStationModel.newInstance("수의대", 120, BusStationModel.newLocationInstance(36.376757, 127.343558)));
                busStationModels.add(BusStationModel.newInstance("체육관입구", 150, BusStationModel.newLocationInstance(36.371902, 127.342957)));
                busStationModels.add(BusStationModel.newInstance("예술대(사범대학)", 180, BusStationModel.newLocationInstance(36.370537, 127.343558)));
                busStationModels.add(BusStationModel.newInstance("중앙도서관(시내버스)", 210, BusStationModel.newLocationInstance(36.369578, 127.346916)));
                busStationModels.add(BusStationModel.newInstance("농생대", 240, BusStationModel.newLocationInstance(36.369016, 127.352055)));
                busStationModels.add(BusStationModel.newInstance("학생생활관", 270, BusStationModel.newLocationInstance(36.372558, 127.346165)));
                busStationModels.add(BusStationModel.newInstance("중앙도서관(정문방향)", 300, BusStationModel.newLocationInstance(36.369094, 127.345757)));
                busStationModels.add(BusStationModel.newInstance("공대1호관", 330, BusStationModel.newLocationInstance(36.367487, 127.345521)));
                busStationModels.add(BusStationModel.newInstance("공대2호관", 360, null));
                break;

            case R.string.tab_cnu_c:
                busStationModels.add(BusStationModel.newInstance("영탑지(승강장)", 0, BusStationModel.newLocationInstance(36.368826, 127.345725)));
                busStationModels.add(BusStationModel.newInstance("인문대(약대서문)", 25, BusStationModel.newLocationInstance(36.368749, 127.343096)));
                busStationModels.add(BusStationModel.newInstance("동물병원", 50, BusStationModel.newLocationInstance(36.377197, 127.342592)));
                busStationModels.add(BusStationModel.newInstance("고속터미널(새마을금고)", 75, BusStationModel.newLocationInstance(36.360782, 127.335710)));
                busStationModels.add(BusStationModel.newInstance("구암역(1번출구)", 110, BusStationModel.newLocationInstance(36.356375, 127.331629)));
                busStationModels.add(BusStationModel.newInstance("유성시외(버스터미널)", 135, BusStationModel.newLocationInstance(36.355263, 127.335957)));
                busStationModels.add(BusStationModel.newInstance("유성온천역(7번출구)", 160, BusStationModel.newLocationInstance(36.354069, 127.341843)));
                busStationModels.add(BusStationModel.newInstance("연래춘", 185, BusStationModel.newLocationInstance(36.360520, 127.344165)));
                busStationModels.add(BusStationModel.newInstance("공대2호관", 210, BusStationModel.newLocationInstance(36.363867, 127.345156)));
                busStationModels.add(BusStationModel.newInstance("사회대(한누리회관)", 235, BusStationModel.newLocationInstance(36.366865, 127.342635)));
                busStationModels.add(BusStationModel.newInstance("공동실습관", 260, BusStationModel.newLocationInstance(36.368904, 127.341444)));
                busStationModels.add(BusStationModel.newInstance("예술대(사범대학)", 285, BusStationModel.newLocationInstance(36.370537, 127.343558)));
                busStationModels.add(BusStationModel.newInstance("중앙도서관(시내버스)", 310, BusStationModel.newLocationInstance(36.369446, 127.346387)));
                busStationModels.add(BusStationModel.newInstance("골프장(주차장)", 335, BusStationModel.newLocationInstance(36.371492, 127.347690)));
                busStationModels.add(BusStationModel.newInstance("-", 360, null));
                break;

            case R.string.tab_cnu_yuseong:
                busStationModels.add(BusStationModel.newInstance("중앙도서관(서문방향)", 0, BusStationModel.newLocationInstance(36.369421, 127.345735)));
                busStationModels.add(BusStationModel.newInstance("예술대", 25, BusStationModel.newLocationInstance(36.370537, 127.343558)));
                busStationModels.add(BusStationModel.newInstance("고속터미널(에덴베이커리)", 50, BusStationModel.newLocationInstance(36.359872, 127.336016)));
                busStationModels.add(BusStationModel.newInstance("리베라호텔(시내버스)", 75, BusStationModel.newLocationInstance(36.354639, 127.338025)));
                busStationModels.add(BusStationModel.newInstance("학사(진터벌주유소)", 100, BusStationModel.newLocationInstance(36.350385, 127.337702)));
                busStationModels.add(BusStationModel.newInstance("유성온천역(7번출구)", 125, BusStationModel.newLocationInstance(36.354069, 127.341843)));
                busStationModels.add(BusStationModel.newInstance("다솔아파트(시내버스)", 150, BusStationModel.newLocationInstance(36.361333, 127.346621)));
                busStationModels.add(BusStationModel.newInstance("한빛아파트(시내버스)", 180, BusStationModel.newLocationInstance(36.363045, 127.353095)));
                busStationModels.add(BusStationModel.newInstance("농생대", 210, BusStationModel.newLocationInstance(36.368994, 127.352137)));
                busStationModels.add(BusStationModel.newInstance("중앙도서관(서문방향)", 240, BusStationModel.newLocationInstance(36.369421, 127.345735)));
                busStationModels.add(BusStationModel.newInstance("예술대", 270, BusStationModel.newLocationInstance(36.370537, 127.343558)));
                busStationModels.add(BusStationModel.newInstance("음악2호관", 300, BusStationModel.newLocationInstance(36.374001, 127.343804)));
                busStationModels.add(BusStationModel.newInstance("수의대", 330, BusStationModel.newLocationInstance(36.376757, 127.343558)));
                busStationModels.add(BusStationModel.newInstance("-", 360, null));
                break;

            case R.string.tab_cnu_a_night:
                busStationModels.add(BusStationModel.newInstance("공대2호관", 0, BusStationModel.newLocationInstance(36.363867, 127.345156)));
                busStationModels.add(BusStationModel.newInstance("중앙도서관", 90, BusStationModel.newLocationInstance(36.369578, 127.346916)));
                busStationModels.add(BusStationModel.newInstance("학생생활관", 180, BusStationModel.newLocationInstance(36.372558, 127.346165)));
                busStationModels.add(BusStationModel.newInstance("중앙도서관", 270, BusStationModel.newLocationInstance(36.369578, 127.346916)));
                busStationModels.add(BusStationModel.newInstance("-", 360, null));
                break;

            case R.string.tab_cnu_b_night:
                busStationModels.add(BusStationModel.newInstance("공대2호관", 0, BusStationModel.newLocationInstance(36.363867, 127.345156)));
                busStationModels.add(BusStationModel.newInstance("중앙도서관", 60, BusStationModel.newLocationInstance(36.369094, 127.345757)));
                busStationModels.add(BusStationModel.newInstance("농생대", 120, BusStationModel.newLocationInstance(36.368994, 127.352137)));
                busStationModels.add(BusStationModel.newInstance("중앙도서관(서문방향)", 180, BusStationModel.newLocationInstance(36.369448, 127.345725)));
                busStationModels.add(BusStationModel.newInstance("수의대", 240, BusStationModel.newLocationInstance(36.376757, 127.343558)));
                busStationModels.add(BusStationModel.newInstance("인문대(약대서문)", 300, BusStationModel.newLocationInstance(36.368749, 127.343096)));
                busStationModels.add(BusStationModel.newInstance("-", 360, null));
                break;
        }
        return busStationModels;
    }

    /**
     * @param title_id
     * @param busStationModels
     * @return
     */
    private ArrayList<BusModel> createHolidayBuses(int title_id, ArrayList<BusStationModel> busStationModels) {

        ArrayList<BusModel> buses = new ArrayList<>();

        if (title_id == R.string.tab_kaist_sunhwan) {

            ArrayList<String> times_str;

            for (int h = 7; h < 26; h += 3) {

                times_str = new ArrayList<>();
                times_str.add(String.format("%02d:50", h));
                times_str.add(String.format("%02d:00", h + 1));
                times_str.add(String.format("%02d:10", h + 1));
                times_str.add(String.format("%02d:14", h + 1));
                times_str.add(String.format("%02d:30", h + 1));
                times_str.add(String.format("%02d:45", h + 1));
                times_str.add(String.format("%02d:50", h + 1));
                buses.add(createBus(times_str, 0));
            }

            for (int h = 9; h < 25; h += 3) {
                times_str = new ArrayList<>();
                times_str.add(String.format("%02d:20", h));
                times_str.add(String.format("%02d:30", h));
                times_str.add(String.format("%02d:40", h));
                times_str.add(String.format("%02d:44", h));
                times_str.add(String.format("%02d:00", h + 1));
                times_str.add(String.format("%02d:15", h + 1));
                times_str.add(String.format("%02d:20", h + 1));
                buses.add(createBus(times_str, 0));
            }
        }

        return buses;
    }

    /**
     * @param title_id
     * @param busStationModels
     * @return
     */
    private ArrayList<BusModel> createWeekdayBuses(int title_id, ArrayList<BusStationModel> busStationModels) {

        ArrayList<BusModel> buses = new ArrayList<>();

        switch (title_id) {

            /**
             * KAIST
             */
            case R.string.tab_kaist_olev:
                for (int h = 8; h <= 17; h++) {
                    int[] ms = {0, 15, 30, 45};
                    for (int m : ms) {
                        if ((h != 8 || m >= 30) && (h != 17 || m == 0) && (h != 12)) {
                            BusModel busModel = new BusModel();
                            addPathInBus(busModel, 0, String.format("%02d:%02d", h, m), 1, String.format("%02d:%02d", h, m + 1));
                            addPathInBus(busModel, 1, String.format("%02d:%02d", h, m + 1), 2, String.format("%02d:%02d", h, m + 2));
                            addPathInBus(busModel, 2, String.format("%02d:%02d", h, m + 2), 3, String.format("%02d:%02d", h, m + 3));
                            addPathInBus(busModel, 3, String.format("%02d:%02d", h, m + 3), 4, String.format("%02d:%02d", h, m + 4));
                            addPathInBus(busModel, 4, String.format("%02d:%02d", h, m + 4), 5, String.format("%02d:%02d", h, m + 6));
                            addPathInBus(busModel, 5, String.format("%02d:%02d", h, m + 6), 6, String.format("%02d:%02d", h, m + 7));
                            addPathInBus(busModel, 6, String.format("%02d:%02d", h, m + 7), 7, String.format("%02d:%02d", h, m + 8));
                            addPathInBus(busModel, 7, String.format("%02d:%02d", h, m + 8), 8, String.format("%02d:%02d", h, m + 9));
                            addPathInBus(busModel, 8, String.format("%02d:%02d", h, m + 9), 9, String.format("%02d:%02d", h, m + 11));
                            buses.add(busModel);
                        }
                    }
                }
                break;

            case R.string.tab_kaist_wolpyeong:
                for (int h = 9; h <= 17; h++) {
                    if (h != 12) {
                        BusModel busModel = new BusModel();
                        addPathInBus(busModel, 0, String.format("%02d:%02d", h, 0), 1, String.format("%02d:%02d", h, 2));
                        addPathInBus(busModel, 1, String.format("%02d:%02d", h, 2), 2, String.format("%02d:%02d", h, 4));
                        addPathInBus(busModel, 2, String.format("%02d:%02d", h, 4), 3, String.format("%02d:%02d", h, 10));
                        addPathInBus(busModel, 3, String.format("%02d:%02d", h, 10), 4, String.format("%02d:%02d", h, 15));
                        addPathInBus(busModel, 4, String.format("%02d:%02d", h, 15), 5, String.format("%02d:%02d", h, 25));
                        addPathInBus(busModel, 5, String.format("%02d:%02d", h, 25), 6, String.format("%02d:%02d", h, 32));
                        addPathInBus(busModel, 6, String.format("%02d:%02d", h, 32), 7, String.format("%02d:%02d", h, 40));
                        addPathInBus(busModel, 7, String.format("%02d:%02d", h, 40), 8, String.format("%02d:%02d", h, 43));
                        addPathInBus(busModel, 8, String.format("%02d:%02d", h, 43), 9, String.format("%02d:%02d", h, 44));
                        addPathInBus(busModel, 9, String.format("%02d:%02d", h, 44), 10, String.format("%02d:%02d", h, 45));
                        buses.add(busModel);
                    }
                }
                break;

            case R.string.tab_kaist_sunhwan:
                ArrayList<String> times_str;
                int[] hs = {7, 8};
                for (int h : hs) {
                    times_str = new ArrayList<>();
                    times_str.add(String.format("%02d:10", h));
                    times_str.add(String.format("%02d:20", h));
                    times_str.add(String.format("%02d:30", h));
                    times_str.add(String.format("%02d:34", h));
                    times_str.add(String.format("%02d:50", h));
                    times_str.add(String.format("%02d:05", h + 1));
                    times_str.add(String.format("%02d:10", h + 1));
                    buses.add(createBus(times_str, 0));
                }
                for (int h = 7; h < 9; h++) {
                    times_str = new ArrayList<String>();
                    times_str.add(String.format("%02d:40", h));
                    times_str.add(String.format("%02d:50", h));
                    times_str.add(String.format("%02d:00", h + 1));
                    times_str.add(String.format("%02d:04", h + 1));
                    times_str.add(String.format("%02d:20", h + 1));
                    times_str.add(String.format("%02d:35", h + 1));
                    times_str.add(String.format("%02d:40", h + 1));
                    buses.add(createBus(times_str, 0));
                }
                for (int h = 9; h < 19; h++) {
                    if ((h < 11) || (h > 15)) {
                        times_str = new ArrayList<String>();
                        times_str.add(String.format("%02d:50", h));
                        times_str.add(String.format("%02d:00", h + 1));
                        times_str.add(String.format("%02d:10", h + 1));
                        times_str.add(String.format("%02d:14", h + 1));
                        times_str.add(String.format("%02d:30", h + 1));
                        times_str.add(String.format("%02d:45", h + 1));
                        times_str.add(String.format("%02d:50", h + 1));
                        buses.add(createBus(times_str, 0));
                    }
                }
                for (int h = 9; h < 27; h++) {
                    if ((h < 11) || (h > 12)) {
                        times_str = new ArrayList<String>();
                        times_str.add(String.format("%02d:20", h));
                        times_str.add(String.format("%02d:30", h));
                        times_str.add(String.format("%02d:40", h));
                        times_str.add(String.format("%02d:44", h));
                        times_str.add(String.format("%02d:00", h + 1));
                        times_str.add(String.format("%02d:15", h + 1));
                        times_str.add(String.format("%02d:20", h + 1));
                        buses.add(createBus(times_str, 0));
                    }
                }
                int[] hs_2 = {11, 27};
                for (int h : hs_2) {
                    times_str = new ArrayList<String>();
                    times_str.add(String.format("%02d:20", h));
                    times_str.add(String.format("%02d:30", h));
                    times_str.add(String.format("%02d:40", h));
                    buses.add(createBus(times_str, 0));
                }

                //
                times_str = new ArrayList<String>();
                times_str.add("11:50");
                times_str.add("12:00");
                times_str.add("12:10");
                times_str.add("12:14");
                times_str.add("12:30");
                buses.add(createBus(times_str, 0));

                //
                int[] hs_3 = {19, 21};
                for (int h : hs_3) {
                    times_str = new ArrayList<String>();
                    times_str.add(String.format("%02d:50", h));
                    times_str.add(String.format("%02d:00", h + 1));
                    times_str.add(String.format("%02d:10", h + 1));
                    buses.add(createBus(times_str, 0));
                }

                //
                times_str = new ArrayList<String>();
                times_str.add("13:00");
                times_str.add("13:15");
                times_str.add("13:20");
                buses.add(createBus(times_str, 4));

                //
                times_str = new ArrayList<String>();
                times_str.add("21:10");
                times_str.add("21:14");
                times_str.add("21:30");
                times_str.add("21:45");
                times_str.add("21:50");
                buses.add(createBus(times_str, 2));
                break;

            /**
             * CNU
             */
            case R.string.tab_cnu_a:
                for (int h = 8; h <= 18; h++) {
                    int[] ms = {0, 15, 30, 45};
                    for (int m : ms) {
                        if ((h != 8 || m >= 30) && (h != 18 || m == 0) && (h != 12)) {
                            BusModel busModel = new BusModel();
                            addPathInBus(busModel, 0, String.format("%02d:%02d", h, m), 1, String.format("%02d:%02d", h, m + 1));
                            addPathInBus(busModel, 1, String.format("%02d:%02d", h, m + 1), 2, String.format("%02d:%02d", h, m + 2));
                            addPathInBus(busModel, 2, String.format("%02d:%02d", h, m + 2), 3, String.format("%02d:%02d", h, m + 5));
                            addPathInBus(busModel, 3, String.format("%02d:%02d", h, m + 5), 4, String.format("%02d:%02d", h, m + 8));
                            addPathInBus(busModel, 4, String.format("%02d:%02d", h, m + 8), 5, String.format("%02d:%02d", h, m + 10));
                            addPathInBus(busModel, 5, String.format("%02d:%02d", h, m + 10), 6, String.format("%02d:%02d", h, m + 11));
                            addPathInBus(busModel, 6, String.format("%02d:%02d", h, m + 11), 7, String.format("%02d:%02d", h, m + 12));
                            addPathInBus(busModel, 7, String.format("%02d:%02d", h, m + 12), 8, String.format("%02d:%02d", h, m + 14));
                            addPathInBus(busModel, 8, String.format("%02d:%02d", h, m + 14), 9, String.format("%02d:%02d", h, m + 15));
                            addPathInBus(busModel, 9, String.format("%02d:%02d", h, m + 15), 10, String.format("%02d:%02d", h, m + 16));
                            addPathInBus(busModel, 10, String.format("%02d:%02d", h, m + 16), 11, String.format("%02d:%02d", h, m + 17));
                            addPathInBus(busModel, 11, String.format("%02d:%02d", h, m + 17), 12, String.format("%02d:%02d", h, m + 19));
                            buses.add(busModel);
                        }
                    }
                }
                break;

            case R.string.tab_cnu_b:
                for (int h = 8; h <= 18; h++) {
                    int[] ms = {0, 10, 20, 30, 40, 50};
                    for (int m : ms) {
                        if ((h != 8 || m >= 30) && (h != 18 || m == 0) && (h != 12)) {
                            BusModel busModel = new BusModel();
                            addPathInBus(busModel, 0, String.format("%02d:%02d", h, m), 1, String.format("%02d:%02d", h, m + 2));
                            addPathInBus(busModel, 1, String.format("%02d:%02d", h, m + 2), 2, String.format("%02d:%02d", h, m + 4));
                            addPathInBus(busModel, 2, String.format("%02d:%02d", h, m + 4), 3, String.format("%02d:%02d", h, m + 6));
                            addPathInBus(busModel, 3, String.format("%02d:%02d", h, m + 6), 4, String.format("%02d:%02d", h, m + 7));
                            addPathInBus(busModel, 4, String.format("%02d:%02d", h, m + 7), 5, String.format("%02d:%02d", h, m + 8));
                            addPathInBus(busModel, 5, String.format("%02d:%02d", h, m + 8), 6, String.format("%02d:%02d", h, m + 9));
                            addPathInBus(busModel, 6, String.format("%02d:%02d", h, m + 9), 7, String.format("%02d:%02d", h, m + 10));
                            addPathInBus(busModel, 7, String.format("%02d:%02d", h, m + 10), 8, String.format("%02d:%02d", h, m + 13));
                            addPathInBus(busModel, 8, String.format("%02d:%02d", h, m + 13), 9, String.format("%02d:%02d", h, m + 19));
                            addPathInBus(busModel, 9, String.format("%02d:%02d", h, m + 19), 10, String.format("%02d:%02d", h, m + 20));
                            addPathInBus(busModel, 10, String.format("%02d:%02d", h, m + 20), 11, String.format("%02d:%02d", h, m + 21));
                            addPathInBus(busModel, 11, String.format("%02d:%02d", h, m + 21), 12, String.format("%02d:%02d", h, m + 22));
                            buses.add(busModel);
                        }
                    }
                }
                break;

            case R.string.tab_cnu_c:

                for (int h = 8; h <= 18; h++) {

                    int m = 30;
                    if (h == 8) {
                        m = 20;
                    }

                    BusModel busModel = new BusModel();
                    addPathInBus(busModel, 0, String.format("%02d:%02d", h, m), 1, String.format("%02d:%02d", h, m + 1));
                    addPathInBus(busModel, 1, String.format("%02d:%02d", h, m + 1), 2, String.format("%02d:%02d", h, m + 3));
                    addPathInBus(busModel, 2, String.format("%02d:%02d", h, m + 3), 3, String.format("%02d:%02d", h, m + 11));
                    addPathInBus(busModel, 3, String.format("%02d:%02d", h, m + 11), 4, String.format("%02d:%02d", h, m + 17));
                    addPathInBus(busModel, 4, String.format("%02d:%02d", h, m + 17), 5, String.format("%02d:%02d", h, m + 19));
                    addPathInBus(busModel, 5, String.format("%02d:%02d", h, m + 19), 6, String.format("%02d:%02d", h, m + 21));
                    addPathInBus(busModel, 6, String.format("%02d:%02d", h, m + 21), 7, String.format("%02d:%02d", h, m + 24));
                    addPathInBus(busModel, 7, String.format("%02d:%02d", h, m + 24), 8, String.format("%02d:%02d", h, m + 26));
                    addPathInBus(busModel, 8, String.format("%02d:%02d", h, m + 26), 9, String.format("%02d:%02d", h, m + 28));
                    addPathInBus(busModel, 9, String.format("%02d:%02d", h, m + 28), 10, String.format("%02d:%02d", h, m + 29));
                    addPathInBus(busModel, 10, String.format("%02d:%02d", h, m + 29), 11, String.format("%02d:%02d", h, m + 30));
                    addPathInBus(busModel, 11, String.format("%02d:%02d", h, m + 30), 12, String.format("%02d:%02d", h, m + 31));
                    addPathInBus(busModel, 12, String.format("%02d:%02d", h, m + 31), 13, String.format("%02d:%02d", h, m + 32));
                    buses.add(busModel);
                }
                break;

            case R.string.tab_cnu_yuseong:
                for (int h = 8; h <= 18; h++) {
                    int m = 0;
                    if (h == 8 || h == 9 || h == 18) {
                        BusModel busModel = new BusModel();
                        addPathInBus(busModel, 0, String.format("%02d:%02d", h, m), 1, String.format("%02d:%02d", h, m + 1));
                        addPathInBus(busModel, 1, String.format("%02d:%02d", h, m + 1), 2, String.format("%02d:%02d", h, m + 5));
                        addPathInBus(busModel, 2, String.format("%02d:%02d", h, m + 5), 3, String.format("%02d:%02d", h, m + 8));
                        addPathInBus(busModel, 3, String.format("%02d:%02d", h, m + 8), 4, String.format("%02d:%02d", h, m + 10));
                        addPathInBus(busModel, 4, String.format("%02d:%02d", h, m + 10), 5, String.format("%02d:%02d", h, m + 14));
                        addPathInBus(busModel, 5, String.format("%02d:%02d", h, m + 14), 6, String.format("%02d:%02d", h, m + 20));
                        addPathInBus(busModel, 6, String.format("%02d:%02d", h, m + 20), 7, String.format("%02d:%02d", h, m + 23));
                        addPathInBus(busModel, 7, String.format("%02d:%02d", h, m + 23), 8, String.format("%02d:%02d", h, m + 25));
                        addPathInBus(busModel, 8, String.format("%02d:%02d", h, m + 25), 9, String.format("%02d:%02d", h, m + 27));
                        addPathInBus(busModel, 9, String.format("%02d:%02d", h, m + 27), 10, String.format("%02d:%02d", h, m + 28));
                        addPathInBus(busModel, 10, String.format("%02d:%02d", h, m + 28), 11, String.format("%02d:%02d", h, m + 29));
                        addPathInBus(busModel, 11, String.format("%02d:%02d", h, m + 29), 12, String.format("%02d:%02d", h, m + 31));
                        buses.add(busModel);
                    } else {
                        BusModel busModel = new BusModel();
                        addPathInBus(busModel, 0, String.format("%02d:%02d", h, m), 1, String.format("%02d:%02d", h, m + 1));
                        addPathInBus(busModel, 1, String.format("%02d:%02d", h, m + 1), 2, String.format("%02d:%02d", h, m + 5));
                        addPathInBus(busModel, 2, String.format("%02d:%02d", h, m + 5), 3, String.format("%02d:%02d", h, m + 8));
                        addPathInBus(busModel, 3, String.format("%02d:%02d", h, m + 8), 5, String.format("%02d:%02d", h, m + 10));
                        addPathInBus(busModel, 5, String.format("%02d:%02d", h, m + 10), 6, String.format("%02d:%02d", h, m + 16));
                        addPathInBus(busModel, 6, String.format("%02d:%02d", h, m + 16), 7, String.format("%02d:%02d", h, m + 19));
                        addPathInBus(busModel, 7, String.format("%02d:%02d", h, m + 19), 8, String.format("%02d:%02d", h, m + 21));
                        addPathInBus(busModel, 8, String.format("%02d:%02d", h, m + 21), 9, String.format("%02d:%02d", h, m + 23));
                        addPathInBus(busModel, 9, String.format("%02d:%02d", h, m + 23), 10, String.format("%02d:%02d", h, m + 24));
                        addPathInBus(busModel, 10, String.format("%02d:%02d", h, m + 24), 11, String.format("%02d:%02d", h, m + 25));
                        addPathInBus(busModel, 11, String.format("%02d:%02d", h, m + 25), 12, String.format("%02d:%02d", h, m + 27));
                        buses.add(busModel);
                    }
                }
                break;
            case R.string.tab_cnu_a_night: {
                for (int h = 19; h <= 23; h++) {
                    int[] ms = {0, 30};
                    for (int m : ms) {
                        BusModel busModel = new BusModel();
                        addPathInBus(busModel, 0, String.format("%02d:%02d", h, m), 1, String.format("%02d:%02d", h, m + 5));
                        addPathInBus(busModel, 1, String.format("%02d:%02d", h, m + 5), 2, String.format("%02d:%02d", h, m + 8));
                        addPathInBus(busModel, 2, String.format("%02d:%02d", h, m + 15), 3, String.format("%02d:%02d", h, m + 18));
                        addPathInBus(busModel, 3, String.format("%02d:%02d", h, m + 18), 4, String.format("%02d:%02d", h, m + 23));
                        buses.add(busModel);
                    }
                }
                break;
            }
            case R.string.tab_cnu_b_night: {
                for (int h = 19; h <= 23; h++) {
                    int[] ms = {0, 30};
                    for (int m : ms) {
                        BusModel busModel = new BusModel();
                        addPathInBus(busModel, 0, String.format("%02d:%02d", h, m), 1, String.format("%02d:%02d", h, m + 5));
                        addPathInBus(busModel, 1, String.format("%02d:%02d", h, m + 5), 2, String.format("%02d:%02d", h, m + 10));
                        addPathInBus(busModel, 2, String.format("%02d:%02d", h, m + 10), 3, String.format("%02d:%02d", h, m + 15));
                        addPathInBus(busModel, 3, String.format("%02d:%02d", h, m + 15), 4, String.format("%02d:%02d", h, m + 20));
                        addPathInBus(busModel, 4, String.format("%02d:%02d", h, m + 20), 5, String.format("%02d:%02d", h, m + 25));
                        addPathInBus(busModel, 5, String.format("%02d:%02d", h, m + 25), 6, String.format("%02d:%02d", h, m + 30));
                        buses.add(busModel);
                    }
                }
                break;
            }
        }

        return buses;
    }

    private void sort(ArrayList<BusModel> buses, ArrayList<BusStationModel> busStationModels) {
        Collections.sort(buses, new Comparator<BusModel>() {
            @Override
            public int compare(BusModel lhs, BusModel rhs) {
                return lhs.getDepartureTime(0).getAbsoluteSecond() > rhs.getDepartureTime(0).getAbsoluteSecond() ? 1 : -1;
            }
        });

        for (BusStationModel busStationModel : busStationModels) {
            Collections.sort(busStationModel.departureTimes, new Comparator<BusTimeModel>() {
                @Override
                public int compare(BusTimeModel lhs, BusTimeModel rhs) {
                    return lhs.getAbsoluteSecond() - rhs.getAbsoluteSecond();
                }
            });
        }
    }

    private BusModel createBus(ArrayList<String> times_str, int offset) {
        BusModel busModel = new BusModel();
        for (int i = 0; i < (times_str.size() - 1); i++) {
            addPathInBus(busModel, i + offset, times_str.get(i), (i + 1 + offset) % busStationModels.size(), times_str.get(i + 1));
        }
        return busModel;
    }

    /**
     * @param busModel
     * @param departureStationIndex
     * @param departureTime
     * @param arrivalStationIndex
     * @param arrivalTime
     */
    private void addPathInBus(BusModel busModel, int departureStationIndex, String departureTime, int arrivalStationIndex, String arrivalTime) {

        BusTimeModel busDepartureTime = new BusTimeModel();
        busDepartureTime.setTime(departureTime);

        BusTimeModel busArrivalTime = new BusTimeModel();
        busArrivalTime.setTime(arrivalTime);

        busModel.addPath(busStationModels.get(departureStationIndex), busDepartureTime, busStationModels.get(arrivalStationIndex), busArrivalTime);
    }

    /**
     * 결과를 반환합니다.
     */

    public HashMap<String, Object> getResult() {

        HashMap<String, Object> map = new HashMap<String, Object>();

        map.put("busStations", busStationModels);
        map.put("buses", buses);

        return map;
    }
}
