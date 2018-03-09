package kr.ac.kaist.kyotong.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import kr.ac.kaist.kyotong.R;
import kr.ac.kaist.kyotong.api.ApiBase;
import kr.ac.kaist.kyotong.api.TaxiApi;
import kr.ac.kaist.kyotong.api.TaxiCameraApi;

import kr.ac.kaist.kyotong.model.TaxiCompanyModel;
import kr.ac.kaist.kyotong.utils.SizeUtils;


public class TaxiFragment extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String TAG = "TaxiFragment";
    private static final String ARG_POSITION = "arg_position";
    private static final String ARG_TITLE = "arg_title";

    /**
     *
     */
    private View mPb;
    private ListView mLv;
    private LvAdapter mLvAdapter;
    private SlidingUpPanelLayout mLayout;
    private ImageView mIv;
    private View mShadowView;

    private TextView mNameTv;

    private View mErrorView;
    private TextView mErrorTv;
    private ProgressBar mErrorPb;

    /**
     *
     */
    private int mTitle_id = -1;
    private int mPosition = -1;

    /**
     *
     */
    private TaxiApiTask mTaxiApiTask = null;
    private TaxiCameraApiTask mTaxiCameraApiTask = null;
    private TimerTask mTimerTask;
    private Timer mTimer;

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static TaxiFragment newInstance(int position, int title) {
        TaxiFragment fragment = new TaxiFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_POSITION, position);
        args.putInt(ARG_TITLE, title);
        fragment.setArguments(args);
        return fragment;
    }

    public TaxiFragment() {
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
        View rootView = inflater.inflate(R.layout.taxi_fragment, container, false);
        View headerView = inflater.inflate(R.layout.base_fragment_lv_header, null, false);
        View footerView = inflater.inflate(R.layout.base_fragment_lv_footer, null, false);

        /**
         *
         */
        Bundle bundle = getArguments();
        mPosition = bundle.getInt(ARG_POSITION);
        mTitle_id = bundle.getInt(ARG_TITLE);

        /**
         *
         */
        mLv = (ListView) rootView.findViewById(R.id.lv);
        mLayout = (SlidingUpPanelLayout) rootView.findViewById(R.id.sliding_layout);
        View mMainLayout = rootView.findViewById(R.id.main_layout);
        mNameTv = (TextView) rootView.findViewById(R.id.name_tv);
        mIv = (ImageView) rootView.findViewById(R.id.iv);
        mShadowView = rootView.findViewById(R.id.shadow_view);
        mErrorView = rootView.findViewById(R.id.error_view);
        mErrorTv = (TextView) mErrorView.findViewById(R.id.error_tv);
        mErrorPb = (ProgressBar) mErrorView.findViewById(R.id.error_pb);
        mPb = rootView.findViewById(R.id.pb);

        /**
         *
         */
        mIv.setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        /**
         *
         */
        mMainLayout.setLayoutParams(new LinearLayout.LayoutParams(SizeUtils.windowWidth(getActivity()),
                SizeUtils.getMainContentHeight(getActivity())
                        + SizeUtils.getAdContainerHeight(getActivity())));

        /**
         *
         */
        mLv.addHeaderView(headerView);
        mLv.addFooterView(footerView);

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
        ArrayList<TaxiCompanyModel> taxiCompanies = new ArrayList<>();
        mLvAdapter = new LvAdapter(getActivity(), R.layout.taxi_fragment_lv, taxiCompanies);
        mLv.setAdapter(mLvAdapter);

        /**
         *
         *//*
        mTimerTask = new TimerTask() {
            Handler handler = new Handler();

            @Override
            public void run() {
                if (mTaxiCameraApiTask == null) {
                    mTaxiCameraApiTask = new TaxiCameraApiTask();
                    handler.post(new Runnable() {
                        public void run() {
                            mTaxiCameraApiTask.execute();
                        }
                    });
                }
            }
        };
        mTimer = new Timer();
        mTimer.schedule(mTimerTask, 0, 10000);

        /**
         *
         */
        mTaxiApiTask = new TaxiApiTask();
        mTaxiApiTask.execute();

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

    private class LvAdapter extends ArrayAdapter<TaxiCompanyModel> {
        private static final String TAG = "BusFragment LvAdapter";

        /**
         *
         */
        private ViewHolder viewHolder = null;
        public ArrayList<TaxiCompanyModel> taxiCompanies;
        private int textViewResourceId;

        /**
         * @param context
         * @param textViewResourceId
         * @param taxiCompanies
         */
        public LvAdapter(Activity context, int textViewResourceId,
                         ArrayList<TaxiCompanyModel> taxiCompanies) {
            super(context, textViewResourceId, taxiCompanies);

            this.textViewResourceId = textViewResourceId;
            this.taxiCompanies = taxiCompanies;

        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public int getCount() {
            return taxiCompanies.size();
        }

        @Override
        public TaxiCompanyModel getItem(int position) {
            return taxiCompanies.get(position);
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

                viewHolder.mNameTv = (TextView) convertView.findViewById(R.id.name_tv);
                viewHolder.mTelBtn = (ImageButton) convertView.findViewById(R.id.tel_btn);

                convertView.setTag(viewHolder);

            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            final TaxiCompanyModel taxiCompanyModel = this.getItem(position);

			/*
             * Data Import and export
			 */

            viewHolder.mNameTv.setText(taxiCompanyModel.name);
            viewHolder.mTelBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String uri = "tel:" + taxiCompanyModel.phone;
                    Intent intent = new Intent(Intent.ACTION_CALL);
                    intent.setData(Uri.parse(uri));
                    startActivity(intent);
                }
            });


            return convertView;
        }

        private class ViewHolder {
            TextView mNameTv;
            ImageButton mTelBtn;
        }
    }

    /**
     *
     */
    public class TaxiApiTask extends AsyncTask<Void, ArrayList<TaxiCompanyModel>, ArrayList<TaxiCompanyModel>> {
        private int request_code = ApiBase.REQUEST_CODE_UNEXPECTED;

        private String title = null;

        @Override
        protected void onPreExecute() {
            showErrorView(true, "");

        }

        /**
         * @param params
         * @return
         */
        @Override
        protected ArrayList<TaxiCompanyModel> doInBackground(Void... params) {

            ArrayList<TaxiCompanyModel> taxiCompanies = new ArrayList<>();

            try {
                TaxiApi taxiApi = new TaxiApi(mTitle_id);
                HashMap<String, Object> map = taxiApi.getResult();
                taxiCompanies = (ArrayList<TaxiCompanyModel>) map.get("taxiCompanies");
                title = (String) map.get("title");

            } catch (Exception e) {
                e.printStackTrace();
            }


            return taxiCompanies;
        }

        @Override
        protected void onPostExecute(ArrayList<TaxiCompanyModel> taxiCompanies) {

            mTaxiApiTask = null;

            if (taxiCompanies.size() > 0) {
                mNameTv.setText(this.title);
                mLvAdapter.taxiCompanies.addAll(taxiCompanies);
                mLvAdapter.notifyDataSetChanged();
                showErrorView(false, "");

            } else {
                mNameTv.setText("콜택시");
                showErrorView(true, "오류가 발생해 콜택시 정보를 불러오지 못했습니다");
            }

        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            mTaxiApiTask = null;
        }
    }

    /**
     *
     */
    public class TaxiCameraApiTask extends AsyncTask<Void, Void, Bitmap> {
        private int request_code = ApiBase.REQUEST_CODE_UNEXPECTED;

        @Override
        protected void onPreExecute() {
            mPb.setVisibility(View.VISIBLE);
            mPb.animate()
                    .setDuration(250)
                    .alpha(1)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            mPb.setVisibility(View.VISIBLE);
                        }
                    });
        }

        /**
         * @param params
         * @return
         */
        @Override
        protected Bitmap doInBackground(Void... params) {

            try {
                TaxiCameraApi taxiCameraApi = new TaxiCameraApi();
                return taxiCameraApi.getResult();

            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            mTaxiCameraApiTask = null;

            /**
             *
             */
            Drawable[] layers = new Drawable[2];
            layers[0] = mIv.getDrawable();
            layers[1] = new BitmapDrawable(getResources(), bitmap);
            TransitionDrawable transitionDrawable = new TransitionDrawable(layers);
            mIv.setImageDrawable(null);
            mIv.setImageDrawable(transitionDrawable);
            transitionDrawable.startTransition(500);

            /**
             *
             */
            mPb.setVisibility(View.VISIBLE);
            mPb.animate()
                    .setStartDelay(500)
                    .setDuration(250)
                    .alpha(0)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            mPb.setVisibility(View.GONE);
                        }
                    });
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            mTaxiApiTask = null;
        }
    }

    @Override
    public void onDestroy() {
        if (mTaxiApiTask != null) {
            mTaxiApiTask.cancel(true);
        }

        if (mTaxiCameraApiTask != null) {
            mTaxiCameraApiTask.cancel(true);
        }

        if (mTimer != null) {
            mTimer.cancel();
        }

        mIv.setImageBitmap(null);

        super.onDestroy();
    }
}

