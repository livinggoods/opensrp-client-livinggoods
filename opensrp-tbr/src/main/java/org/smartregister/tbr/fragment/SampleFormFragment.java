package org.smartregister.tbr.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.greenrobot.eventbus.EventBus;
import org.smartregister.tbr.R;
import org.smartregister.tbr.activity.BasePatientDetailActivity;
import org.smartregister.tbr.activity.SampleFormActivity;
import org.smartregister.tbr.helper.DBQueryHelper;
import org.smartregister.tbr.util.Constants;

import java.util.Map;

import util.TbrConstants;

import static org.smartregister.tbr.util.Constants.INTENT_KEY.REGISTER_TITLE;
import static org.smartregister.util.JsonFormUtils.generateRandomUUIDString;
import static util.TbrConstants.VIEW_CONFIGS.PRESUMPTIVE_REGISTER_HEADER;
import static util.TbrConstants.VIEW_CONFIGS.SAMPLE_FORM;

/**
 * Created by ndegwamartin on 24/11/2017.
 */


public class SampleFormFragment extends BaseRegisterFragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_base_register, container, false);
//        Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        AppCompatActivity activity = ((AppCompatActivity) getActivity());
//        activity.setSupportActionBar(toolbar);
//        activity.getSupportActionBar().setTitle(activity.getIntent().getStringExtra(Constants.FORM.SAMPLE_FORM));
//        rootView.setTag(R.id.VIEW_CONFIGURATION_ID, getViewConfigurationIdentifier());
//        if (patientDetails != null && patientDetails.containsKey(Constants.KEY._ID)) {
//            rootView.setTag(R.id.BASE_ENTITY_ID, patientDetails.get(Constants.KEY._ID));
//        }
        //setupViews(rootView);
        return rootView;
    }
    @Override
    public String getAggregateCondition(boolean b) {
        return "";
    }

    @Override
    protected void populateClientListHeaderView(View view) {
        View headerLayout = getLayoutInflater(null).inflate(R.layout.register_list_header, null);
        populateClientListHeaderView(view, headerLayout, SAMPLE_FORM);
    }

    @Override
    protected String getMainCondition() {
        return DBQueryHelper.getPresumptivePatientRegisterCondition();
    }

    @Override
    protected String[] getAdditionalColumns(String tableName) {
        return new String[]{};
    }


    @Override
    protected void onCreation() {


//        resultMenuListener = new ResultMenuListener(patientDetails.get(Constants.KEY._ID));
//        formOverridesHelper = new FormOverridesHelper(patientDetails);

    }

    @Override
    protected String getViewConfigurationIdentifier() {
        return Constants.FORM.SAMPLE_FORM;
    }


    @Override
    public void onStart() {
        super.onStart();
//        EventBus.getDefault().register(this);
        String entityId = generateRandomUUIDString();
        ((SampleFormActivity)getActivity()).startFormActivity(TbrConstants.ENKETO_FORMS.SAMPLE_FORM, entityId, null);
    }

    @Override
    public void onStop() {
    //    EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    protected void onResumption() {
        //Overridden method
    }


}
