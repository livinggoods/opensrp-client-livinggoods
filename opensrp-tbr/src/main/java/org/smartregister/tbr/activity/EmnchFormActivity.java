package org.smartregister.tbr.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Menu;

import org.smartregister.enketo.listener.DisplayFormListener;
import org.smartregister.tbr.fragment.EmnchFormFragment;
import org.smartregister.tbr.fragment.SampleFormFragment;

import java.util.Arrays;
import java.util.List;

import util.TbrConstants;

import static util.TbrConstants.ENKETO_FORMS.EMNCH_FORM;
import static util.TbrConstants.VIEW_CONFIGS.COMMON_REGISTER_HEADER;
import static util.TbrConstants.VIEW_CONFIGS.COMMON_REGISTER_ROW;
import static util.TbrConstants.VIEW_CONFIGS.SAMPLE_FORM_HEADER;
import static util.TbrConstants.VIEW_CONFIGS.EMNCH_FORM_ROW;

/**
 * Created by kevinkorir on 12/10/2018.
 */

public class EmnchFormActivity extends BaseRegisterActivity implements DisplayFormListener {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected Fragment getRegisterFragment() {

        EmnchFormFragment fragment = new EmnchFormFragment();
        return fragment;
    }

    @Override
    public List<String> getViewIdentifiers() {
        return Arrays.asList(EMNCH_FORM, TbrConstants.VIEW_CONFIGS.EMNCH_FORM, EMNCH_FORM_ROW, COMMON_REGISTER_HEADER, COMMON_REGISTER_ROW);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    protected List<String> buildFormNameList() {
        formNames = super.buildFormNameList();
        formNames.add(0, EMNCH_FORM);
        return formNames;
    }




}
