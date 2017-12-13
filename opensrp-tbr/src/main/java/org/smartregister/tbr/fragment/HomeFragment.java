package org.smartregister.tbr.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.HapticFeedbackConstants;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import org.smartregister.tbr.R;
import org.smartregister.tbr.activity.InTreatmentPatientRegisterActivity;
import org.smartregister.tbr.activity.PositivePatientRegisterActivity;
import org.smartregister.tbr.activity.PresumptivePatientRegisterActivity;
import org.smartregister.tbr.adapter.RegisterArrayAdapter;
import org.smartregister.tbr.application.TbrApplication;
import org.smartregister.tbr.jsonspec.model.Residence;
import org.smartregister.tbr.jsonspec.model.ViewConfiguration;
import org.smartregister.tbr.model.Register;
import org.smartregister.tbr.repository.ConfigurableViewsRepository;
import org.smartregister.tbr.util.Constants;
import org.smartregister.tbr.util.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static org.smartregister.tbr.activity.BaseRegisterActivity.TOOLBAR_TITLE;

/**
 * Created by ndegwamartin on 12/10/2017.
 */

public class HomeFragment extends ListFragment {
    private static String TAG = HomeFragment.class.getCanonicalName();

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        try {
            super.onActivityCreated(savedInstanceState);
            List<Register> values = new ArrayList<>();

            String jsonString = getConfigurableViewsRepository().getConfigurableViewJson(Constants.CONFIGURATION.HOME);
            ViewConfiguration homeViewConfig = jsonString == null ? null : TbrApplication.getJsonSpecHelper().getConfigurableView(jsonString);

            if (homeViewConfig != null) {
                List<org.smartregister.tbr.jsonspec.model.View> views = homeViewConfig.getViews();
                for (org.smartregister.tbr.jsonspec.model.View view : views) {
                    if (view.isVisible()) {
                        values.add(new Register(getActivity(), view, RegisterDataRepository.getPatientCountByRegisterType(view.getIdentifier()),
                                RegisterDataRepository.getOverduePatientCountByRegisterType(view.getIdentifier())));
                    }
                }
                if (values.size() > 0) {
                    Collections.sort(values, new Comparator<Register>() {
                        @Override
                        public int compare(Register registerA, Register registerB) {
                            return registerA.getPosition() - registerB.getPosition();
                        }
                    });
                } else {
                    values.addAll(getDefaultRegisterList());
                }
            } else {

                values.addAll(getDefaultRegisterList());
            }

            RegisterArrayAdapter adapter = new RegisterArrayAdapter(getActivity(), R.layout.row_register_view, values);
            setListAdapter(adapter);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }

    }

    @Override
    public void onListItemClick(ListView listView, View view, int position, long id) {
        TextView registerTitle = (TextView) view.findViewById(R.id.registerTitleView);
        view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
        Utils.showToast(getActivity(), registerTitle.getText().toString() + " Register!");
        Register register = (Register) this.getListAdapter().getItem(position);
        if (register.getTitleToken().equals(Register.PRESUMPTIVE_PATIENTS)) {
            initializeRegister(new Intent(this.getActivity(), PresumptivePatientRegisterActivity.class), register);
        } else if (register.getTitleToken().equals(Register.POSITIVE_PATIENTS)) {
            initializeRegister(new Intent(this.getActivity(), PositivePatientRegisterActivity.class), register);
<<<<<<< HEAD:opensrp-tbr/src/main/java/org/smartregister/tbr/fragment/HomeFragment.java
=======
        } else if (register.getTitleToken().equals(Register.IN_TREATMENT_PATIENTS)) {
            initializeRegister(new Intent(this.getActivity(), InTreatmentPatientRegisterActivity.class), register);
>>>>>>> issue40:opensrp-tbr/src/main/java/org/smartregister/tbr/fragment/RegisterFragment.java
        }
    }

    private void initializeRegister(Intent intent, Register register) {
        intent.putExtra(TOOLBAR_TITLE, register.getTitle());
        startActivity(intent);
    }

    public static class RegisterDataRepository {
        public static int getPatientCountByRegisterType(String registerType) {
            if (registerType.equals(Register.PRESUMPTIVE_PATIENTS)) {
                return 12;
            } else if (registerType.equals(Register.POSITIVE_PATIENTS)) {
                return 13;
            } else if (registerType.equals(Register.IN_TREATMENT_PATIENTS)) {
                return 4;
            } else {
                return 0;
            }
        }

        public static int getOverduePatientCountByRegisterType(String registerType) {
            if (registerType.equals(Register.PRESUMPTIVE_PATIENTS)) {
                return 0;
            } else if (registerType.equals(Register.POSITIVE_PATIENTS)) {
                return 10;
            } else if (registerType.equals(Register.IN_TREATMENT_PATIENTS)) {
                return 2;
            } else {
                return 0;
            }
        }
    }

    private ConfigurableViewsRepository getConfigurableViewsRepository() {
        return TbrApplication.getInstance().getConfigurableViewsRepository();
    }

    private List<Register> getDefaultRegisterList() {
        List<Register> values = new ArrayList<>();
        //Render Default View if no configs exist
        org.smartregister.tbr.jsonspec.model.View view = new org.smartregister.tbr.jsonspec.model.View();
        Residence residence = new Residence();


        view.setIdentifier(Register.PRESUMPTIVE_PATIENTS);
        view.setLabel(Register.PRESUMPTIVE_PATIENTS);
        residence.setPosition(0);
        view.setResidence(residence);

        values.add(new Register(getActivity(), view, RegisterDataRepository.getPatientCountByRegisterType(view.getIdentifier()),
                RegisterDataRepository.getOverduePatientCountByRegisterType(view.getIdentifier())));

        view = new org.smartregister.tbr.jsonspec.model.View();
        view.setIdentifier(Register.POSITIVE_PATIENTS);
        view.setLabel(Register.POSITIVE_PATIENTS);
        residence.setPosition(1);
        view.setResidence(residence);

        values.add(new Register(getActivity(), view, RegisterDataRepository.getPatientCountByRegisterType(view.getIdentifier()),
                RegisterDataRepository.getOverduePatientCountByRegisterType(view.getIdentifier())));

        view = new org.smartregister.tbr.jsonspec.model.View();
        view.setIdentifier(Register.IN_TREATMENT_PATIENTS);
        view.setLabel(Register.IN_TREATMENT_PATIENTS);

        residence.setPosition(2);
        view.setResidence(residence);
        values.add(new Register(getActivity(), view, RegisterDataRepository.getPatientCountByRegisterType(view.getIdentifier()),
                RegisterDataRepository.getOverduePatientCountByRegisterType(view.getIdentifier())));
        return values;
    }
}
