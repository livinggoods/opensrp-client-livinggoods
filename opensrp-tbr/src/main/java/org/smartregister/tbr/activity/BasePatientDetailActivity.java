package org.smartregister.tbr.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONObject;
import org.smartregister.enketo.adapter.pager.EnketoRegisterPagerAdapter;
import org.smartregister.enketo.listener.DisplayFormListener;
import org.smartregister.enketo.view.fragment.DisplayFormFragment;
import org.smartregister.tbr.R;
import org.smartregister.tbr.helper.FormOverridesHelper;
import org.smartregister.tbr.model.Register;
import org.smartregister.tbr.util.Constants;
import org.smartregister.tbr.util.Utils;
import org.smartregister.view.viewpager.OpenSRPViewPager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import util.EnketoFormUtils;
import util.TbrConstants;

import static org.smartregister.tbr.activity.BaseRegisterActivity.TOOLBAR_TITLE;

/**
 * Created by ndegwamartin on 17/11/2017.
 */

public abstract class BasePatientDetailActivity extends BaseActivity implements DisplayFormListener {
    protected String[] formNames = new String[]{};
    @Bind(R.id.view_pager)
    protected OpenSRPViewPager mPager;
    protected FragmentPagerAdapter mPagerAdapter;
    protected int currentPage;
    private static final String TAG = BasePatientDetailActivity.class.getCanonicalName();
    private boolean formIsReadonly;
    protected Map<String, String> patientDetails;
    protected FormOverridesHelper formOverridesHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        ButterKnife.bind(this);

        formNames = this.buildFormNameList();
        patientDetails = (HashMap<String, String>) getIntent().getSerializableExtra(Constants.INTENT_KEY.PATIENT_DETAIL_MAP);
        formOverridesHelper = new FormOverridesHelper(patientDetails);

        initializeEnketoForms();
    }

    private void initializeEnketoForms() {

        Fragment baseFragment = getDetailFragment();
        // Instantiate a ViewPager and a PagerAdapter.
        mPagerAdapter = new EnketoRegisterPagerAdapter(getSupportFragmentManager(), formNames, baseFragment);
        mPager.setOffscreenPageLimit(formNames.length);
        mPager.setAdapter(mPagerAdapter);
        mPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                currentPage = position;
            }
        });
        initializeEnketoFormFragment(formNames[0], null, null, false, false);
    }

    protected abstract Fragment getDetailFragment();

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public void initializeEnketoFormFragment(String formName, String entityId, String metaData, boolean displayForm, boolean readonly) {
        try {
            int formIndex = getIndexForFormName(formName, formNames) + 1; // add the offset
            if (entityId != null || metaData != null) {
                String data = EnketoFormUtils.getInstance(this).generateXMLInputForFormWithEntityId(entityId, formName, metaData);
                System.out.println("Display Form:" + formIndex);
                DisplayFormFragment displayFormFragment = getDisplayFormFragmentAtIndex(formIndex);
                if (displayFormFragment != null) {
                    displayFormFragment.setFormData(data);
                    displayFormFragment.setRecordId(entityId);
                    displayFormFragment.setFieldOverides(metaData);
                    displayFormFragment.setListener(this);
                    displayFormFragment.setResize(false);
                    if (readonly)
                        displayFormFragment.displayFormAsReadonly();
                }
            }
            formIsReadonly = readonly;
            if (displayForm)
                mPager.setCurrentItem(formIndex, false);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void startFormActivity(String formName, String entityId, String metaData, boolean readonly) {
        initializeEnketoFormFragment(formName, entityId, metaData, true, readonly);
    }

    public void startFormActivity(String formName, String entityId, String metaData) {
        initializeEnketoFormFragment(formName, entityId, metaData, true, false);
    }

    private DisplayFormFragment getDisplayFormFragmentAtIndex(int index) {
        return (DisplayFormFragment) this.findFragmentByPosition(index);
    }

    private int getIndexForFormName(String formName, String[] formNames) {
        for (int i = 0; i < formNames.length; i++) {
            if (formName.equalsIgnoreCase(formNames[i])) {
                return i;
            }
        }

        return -1;
    }

    protected Fragment findFragmentByPosition(int position) {
        return getSupportFragmentManager().findFragmentByTag("android:switcher:" + mPager.getId() + ":" + mPagerAdapter.getItemId(position));
    }

    protected String[] buildFormNameList() {
        List<String> formNames = new ArrayList<String>();
        formNames.add(Constants.FORM.NEW_PATIENT_REGISTRATION);
        formNames.add(Constants.FORM.RESULT_GENE_EXPERT);
        formNames.add(Constants.FORM.RESULT_SMEAR);
        formNames.add(Constants.FORM.RESULT_CHEST_XRAY);
        formNames.add(Constants.FORM.RESULT_CULTURE);
        formNames.add(Constants.FORM.DIAGNOSIS);
        formNames.add(TbrConstants.ENKETO_FORMS.TREATMENT_INITIATION);
        formNames.add(Constants.FORM.CONTACT_SCREENING);
        formNames.add(TbrConstants.ENKETO_FORMS.FOLLOWUP_VISIT);
        formNames.add(TbrConstants.ENKETO_FORMS.ADD_TB_CONTACT);
        formNames.add(Constants.FORM.REMOVE_PATIENT);
        formNames.add(Constants.FORM.TREATMENT_OUTCOME);
        formNames.add(TbrConstants.ENKETO_FORMS.ADD_POSITIVE_PATIENT);
        formNames.add(TbrConstants.ENKETO_FORMS.SAMPLE_FORM);
        formNames.add(TbrConstants.ENKETO_FORMS.EMNCH_FORM);
        return formNames.toArray(new String[formNames.size()]);
    }

    private void switchToBaseFragment() {
        final int prevPageIndex = currentPage;
        formIsReadonly = false;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mPager.setCurrentItem(0, false);
                DisplayFormFragment displayFormFragment = getDisplayFormFragmentAtIndex(prevPageIndex);
                if (displayFormFragment != null) {
                    displayFormFragment.hideTranslucentProgressDialog();
                    displayFormFragment.setFormData(null);
                }

                displayFormFragment.setRecordId(null);
            }
        });

    }

    @Override
    public void onBackPressed() {
        if (currentPage != 0 && !formIsReadonly) {
            new AlertDialog.Builder(this, R.style.TbrAlertDialog)
                    .setMessage(R.string.form_back_confirm_dialog_message)
                    .setTitle(R.string.form_back_confirm_dialog_title)
                    .setCancelable(false)
                    .setPositiveButton(R.string.no_button_label,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    //Do nothing, remain on Enketo Form Fragment
                                }
                            })
                    .setNegativeButton(R.string.yes_button_label,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    switchToBaseFragment();
                                }
                            })
                    .show();
        } else if (currentPage != 0 && formIsReadonly) {
            switchToBaseFragment();
        } else {
            super.onBackPressed(); // allow back key only if we are
        }
    }

    @Override
    public void saveFormSubmission(String formSubmision, String id, String formName, JSONObject fieldOverrides) {
        try {
            EnketoFormUtils enketoFormUtils = EnketoFormUtils.getInstance(this);
            enketoFormUtils.generateFormSubmisionFromXMLString(id, formSubmision, formName, fieldOverrides);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
        switchToBaseFragment();
    }

    @Override
    public void savePartialFormData(String formData, String id, String formName, JSONObject fieldOverrides) {
        Toast.makeText(this, formName + " partially submitted", Toast.LENGTH_SHORT).show();
    }


    public void goToPatientDetailActivity(Constants.ScreenStage viewConfigurationIdentifier, Map<String, String> patientDetails) {
        Intent intent = null;
        String registerToken = "";
        switch (viewConfigurationIdentifier) {
            case PRESUMPTIVE:
                intent = new Intent(this, PresumptivePatientDetailActivity.class);
                registerToken = Register.PRESUMPTIVE_PATIENTS;
                break;
            case POSITIVE:
                intent = new Intent(this, PositivePatientDetailActivity.class);
                registerToken = Register.POSITIVE_PATIENTS;
                break;
            case SAMPLE_FORM:
                intent = new Intent(this, PositivePatientDetailActivity.class);
                registerToken = Register.SAMPLE_FORM;
                break;
            case IN_TREATMENT:
                intent = new Intent(this, InTreatmentPatientDetailActivity.class);
                registerToken = Register.IN_TREATMENT_PATIENTS;
                break;
            case SCREENED:
                intent = new Intent(this, ScreenedPatientDetailActivity.class);
                break;
            default:
                break;

        }
        String registerTitle = Utils.readPrefString(this, TOOLBAR_TITLE + registerToken, "");
        intent.putExtra(Constants.INTENT_KEY.REGISTER_TITLE, registerTitle);
        intent.putExtra(Constants.INTENT_KEY.PATIENT_DETAIL_MAP, (HashMap) patientDetails);
        intent.putExtra(Constants.KEY.TBREACH_ID, patientDetails.get(TbrConstants.KEY.PARTICIPANT_ID));
        startActivity(intent);

    }

    @Override
    public void onFormClosed(String recordId, String formName) {
        Toast.makeText(this, formName + " closed", Toast.LENGTH_SHORT).show();
        switchToBaseFragment();
    }
}
