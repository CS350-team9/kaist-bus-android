package kr.ac.kaist.kyotong.model;

import android.content.Context;

import java.util.ArrayList;

/**
 * Created by yearnning on 15. 1. 22..
 */
public class UniversityModel {

    public int id = -1;
    public String name = "";
    public ArrayList<ShuttleModel> shuttleModels = new ArrayList<>();

    public static UniversityModel newInstance(Context context, int id) {

        if (id == 1) {
            return newInstance(1, "KAIST")
                    .addShuttleModel(ShuttleModel.newInstance(context, 1))
                    .addShuttleModel(ShuttleModel.newInstance(context, 2))
                    .addShuttleModel(ShuttleModel.newInstance(context, 3))
                    .addShuttleModel(ShuttleModel.newInstance(context, 4))
                    .addShuttleModel(ShuttleModel.newInstance(context, 5));
        } else if (id == 2) {
            return newInstance(2, "공주대학교")
                    .addShuttleModel(ShuttleModel.newInstance(context, 6));
        } /*else if (id == 2) {

            return newInstance(2, "충남대학교")
                    .addShuttleModel(Shuttle.newInstance(Shuttle.Type.BUS, R.string.tab_cnu_a, R.string.tab_cnu_a_explain))
                    .addShuttleModel(Shuttle.newInstance(Shuttle.Type.BUS, R.string.tab_cnu_b, R.string.tab_cnu_b_explain))
                    //.addShuttleModel(Shuttle.newInstance(Shuttle.Type.BUS, R.string.tab_cnu_c, R.string.tab_cnu_c_explain))
                    //.addShuttleModel(Shuttle.newInstance(Shuttle.Type.BUS, R.string.tab_cnu_yuseong, R.string.tab_cnu_yuseong_explain))
                    .addShuttleModel(Shuttle.newInstance(Shuttle.Type.BUS, R.string.tab_cnu_a_night, R.string.tab_cnu_a_night_explain))
                    .addShuttleModel(Shuttle.newInstance(Shuttle.Type.BUS, R.string.tab_cnu_b_night, R.string.tab_cnu_b_night_explain))
                    .addShuttleModel(Shuttle.newInstance(Shuttle.Type.TAXI, R.string.tab_cnu_taxi, R.string.tab_cnu_taxi_explain));
        } */ else {
            return null;
        }
    }

    private static UniversityModel newInstance(int id, String name) {
        UniversityModel universityModel = new UniversityModel();
        universityModel.id = id;
        universityModel.name = name;
        return universityModel;
    }

    /**
     *
     */
    private UniversityModel addShuttleModel(ShuttleModel shuttleModel) {

        int i;
        for (i = 0; i < shuttleModels.size(); i++) {
            if (shuttleModels.get(i).weight < shuttleModel.weight) {
                break;
            }
        }

        this.shuttleModels.add(i, shuttleModel);
        return this;
    }


}
