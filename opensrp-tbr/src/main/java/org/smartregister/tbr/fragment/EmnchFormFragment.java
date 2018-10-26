package org.smartregister.tbr.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.smartregister.tbr.R;
import org.smartregister.tbr.activity.EmnchFormActivity;
import org.smartregister.tbr.activity.SampleFormActivity;
import org.smartregister.tbr.helper.DBQueryHelper;
import org.smartregister.tbr.util.Constants;

import util.TbrConstants;

import static org.smartregister.util.JsonFormUtils.generateRandomUUIDString;
import static util.TbrConstants.VIEW_CONFIGS.EMNCH_FORM;

/**
 * Created by ndegwamartin on 24/11/2017.
 */


public class EmnchFormFragment extends BaseRegisterFragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_base_register, container, false);
        AppCompatActivity activity = ((AppCompatActivity) getActivity());
        return rootView;
    }
    @Override
    protected void populateClientListHeaderView(View view) {
        View headerLayout = getLayoutInflater(null).inflate(R.layout.register_list_header, null);
        populateClientListHeaderView(view, headerLayout, EMNCH_FORM);
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
        return Constants.FORM.EMNCH_FORM;
    }


    @Override
    public void onStart() {
        super.onStart();
//        EventBus.getDefault().register(this);
        String entityId = generateRandomUUIDString();
        ((EmnchFormActivity)getActivity()).startFormActivity(TbrConstants.ENKETO_FORMS.EMNCH_FORM, entityId, null);
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
