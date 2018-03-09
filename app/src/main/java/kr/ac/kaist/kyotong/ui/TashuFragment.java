package kr.ac.kaist.kyotong.ui;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;


import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.ArrayList;

import kr.ac.kaist.kyotong.R;
import kr.ac.kaist.kyotong.api.ApiBase;
import kr.ac.kaist.kyotong.utils.SizeUtils;

import kr.ac.kaist.kyotong.api.TashuApi;
import kr.ac.kaist.kyotong.model.TashuStationModel;


public class TashuFragment extends Fragment {
    private static final String TAG = "TashuFragment";
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_POSITION = "arg_position";

    /**
     *
     */
    private ListView mLv;
    private LvAdapter mLvAdapter;
    private SlidingUpPanelLayout mLayout;
    private View mShadowView;

    private View mErrorView;
    private TextView mErrorTv;
    private ProgressBar mErrorPb;

    /**
     *
     */
    private int mPosition = -1;

    /**
     *
     */
    TashuApiTask mTashuApiTask = null;

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static TashuFragment newInstance(int position) {
        TashuFragment fragment = new TashuFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_POSITION, position);
        fragment.setArguments(args);
        return fragment;
    }

    public TashuFragment() {
    }

    /**
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tashu_fragment, container, false);
        View headerView = inflater.inflate(R.layout.base_fragment_lv_header, null, false);
        View footerView = inflater.inflate(R.layout.base_fragment_lv_footer, null, false);

        /**
         *
         */
        mPosition = getArguments().getInt(ARG_POSITION);

        /**
         *
         */
        mLv = (ListView) rootView.findViewById(R.id.lv);
        mLayout = (SlidingUpPanelLayout) rootView.findViewById(R.id.sliding_layout);
        View mMainLayout = rootView.findViewById(R.id.main_layout);
        mShadowView = rootView.findViewById(R.id.shadow_view);

        mErrorView = rootView.findViewById(R.id.error_view);
        mErrorTv = (TextView) mErrorView.findViewById(R.id.error_tv);
        mErrorPb = (ProgressBar) mErrorView.findViewById(R.id.error_pb);

        /**
         *
         */
        mLv.addHeaderView(headerView);
        mLv.addFooterView(footerView);

        /**
         *
         */
        mMainLayout.setLayoutParams(new LinearLayout.LayoutParams(SizeUtils.windowWidth(getActivity()),
                SizeUtils.getMainContentHeight(getActivity())
                        + SizeUtils.getAdContainerHeight(getActivity())));

        /**
         *
         */
        //mLayout.setEnableDragViewTouchEvents(true);
        mLayout.setPanelHeight(SizeUtils.getPanelHeight(getActivity()));
        mLayout.setScrollableView(mLv);
        mLayout.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                mShadowView.setAlpha(slideOffset);
            }

            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
                if (newState == SlidingUpPanelLayout.PanelState.COLLAPSED) {
                    ((MainActivity) getActivity()).notifyPanelCollpased(mPosition);
                } else if (newState == SlidingUpPanelLayout.PanelState.EXPANDED) {
                    ((MainActivity) getActivity()).notifyPanelExpand(mPosition);
                }
            }
        });

        /**
         *
         */
        ArrayList<TashuStationModel> tashuStationModels = new ArrayList<TashuStationModel>();
        mLvAdapter = new LvAdapter(getActivity(), R.layout.tashu_fragment_lv, tashuStationModels);
        mLv.setAdapter(mLvAdapter);

        /**
         *
         */
        mTashuApiTask = new TashuApiTask();
        mTashuApiTask.execute();

        return rootView;
    }

    /**
     * @param show
     */
    private void showErrorView(final boolean show, String msg) {

        if (show) {
            mLv.setVisibility(View.GONE);
            mErrorView.setVisibility(View.VISIBLE);
            mErrorTv.setText(msg);

            if (msg.equals("")) {
                mErrorPb.setVisibility(View.VISIBLE);
            } else {
                mErrorPb.setVisibility(View.GONE);
            }

        } else {
            mLv.setVisibility(View.VISIBLE);
            mErrorPb.setVisibility(View.VISIBLE);
            mErrorView.setVisibility(View.GONE);
        }
    }

    /**
     * ListView Apdater Setting
     */

    private class LvAdapter extends ArrayAdapter<TashuStationModel> {
        private static final String TAG = "BusFragment LvAdapter";

        /**
         *
         */
        private ViewHolder viewHolder = null;
        public ArrayList<TashuStationModel> tashuStationModels;
        private int textViewResourceId;

        /**
         * @param context
         * @param textViewResourceId
         * @param articles
         */
        public LvAdapter(Activity context, int textViewResourceId,
                         ArrayList<TashuStationModel> articles) {
            super(context, textViewResourceId, articles);

            this.textViewResourceId = textViewResourceId;
            this.tashuStationModels = articles;

        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public int getCount() {
            return tashuStationModels.size();
        }

        @Override
        public TashuStationModel getItem(int position) {
            return tashuStationModels.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

			/*
             * UI Initiailizing : View Holder
			 */

            if (convertView == null) {
                convertView = getActivity().getLayoutInflater()
                        .inflate(textViewResourceId, null);

                viewHolder = new ViewHolder();

                /**
                 * Find View By ID
                 */

                viewHolder.mTimeTv = (TextView) convertView.findViewById(R.id.name_tv);
                viewHolder.mLeftTv = (TextView) convertView.findViewById(R.id.left_tv);

                convertView.setTag(viewHolder);

            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            TashuStationModel tashuStationModel = this.getItem(position);

			/*
             * Data Import and export
			 */
            viewHolder.mTimeTv.setText(tashuStationModel.name);
            viewHolder.mLeftTv.setText(tashuStationModel.cnt_rentable + " / " + tashuStationModel.cnt_total);

            return convertView;
        }

        private class ViewHolder {
            TextView mTimeTv;
            TextView mLeftTv;
        }
    }


    /**
     *
     */
    public class TashuApiTask extends AsyncTask<Void, ArrayList<TashuStationModel>, ArrayList<TashuStationModel>> {
        private int request_code = ApiBase.REQUEST_CODE_UNEXPECTED;

        @Override
        protected void onPreExecute() {
            showErrorView(true, "");

        }

        /**
         * @param params
         * @return
         */
        @Override
        protected ArrayList<TashuStationModel> doInBackground(Void... params) {

            ArrayList<TashuStationModel> tashuStationModels = new ArrayList<TashuStationModel>();

            try {
                TashuApi tashuApi = new TashuApi(getActivity().getApplication());
                tashuStationModels = tashuApi.getResult();

            } catch (Exception e) {
                e.printStackTrace();
            }


            return tashuStationModels;
        }

        @Override
        protected void onPostExecute(ArrayList<TashuStationModel> tashuStationModels) {

            if (tashuStationModels != null) {
                mLvAdapter.tashuStationModels.addAll(tashuStationModels);
                mLvAdapter.notifyDataSetChanged();
                showErrorView(false, "");

            } else {
                showErrorView(true, "오류가 발생해 타슈 정보를 불러오지 못했습니다");
            }

            mTashuApiTask = null;
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            mTashuApiTask = null;
        }
    }


    @Override
    public void onDestroy() {
        if (mTashuApiTask != null) {
            mTashuApiTask.cancel(true);
        }

        super.onDestroy();
    }

}

