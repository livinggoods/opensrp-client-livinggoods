package org.smartregister.tbr.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Menu;

import org.smartregister.enketo.listener.DisplayFormListener;
import org.smartregister.tbr.fragment.InTreatmentPatientDetailsFragment;
import org.smartregister.tbr.fragment.SampleFormFragment;
import org.smartregister.tbr.util.Constants;

import static util.TbrConstants.ENKETO_FORMS.SAMPLE_FORM;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import util.TbrConstants;

import static util.TbrConstants.ENKETO_FORMS.SAMPLE_FORM;
import static util.TbrConstants.VIEW_CONFIGS.COMMON_REGISTER_HEADER;
import static util.TbrConstants.VIEW_CONFIGS.COMMON_REGISTER_ROW;
import static util.TbrConstants.VIEW_CONFIGS.SAMPLE_FORM_HEADER;
import static util.TbrConstants.VIEW_CONFIGS.SAMPLE_FORM_ROW;

/**
 * Created by kevinkorir on 12/10/2018.
 */

public class SampleFormActivity extends BaseRegisterActivity implements DisplayFormListener {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //startFormActivity(FOLLOWUP_VISIT, null, null);

    }

    @Override
    protected Fragment getRegisterFragment() {

        SampleFormFragment fragment = new SampleFormFragment();
        return fragment;
    }

    @Override
    public List<String> getViewIdentifiers() {
        return Arrays.asList(SAMPLE_FORM_ROW, TbrConstants.VIEW_CONFIGS.SAMPLE_FORM, SAMPLE_FORM_HEADER, COMMON_REGISTER_HEADER, COMMON_REGISTER_ROW);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    protected List<String> buildFormNameList() {
        formNames = super.buildFormNameList();
        formNames.add(0, SAMPLE_FORM);
        return formNames;
    }




}
