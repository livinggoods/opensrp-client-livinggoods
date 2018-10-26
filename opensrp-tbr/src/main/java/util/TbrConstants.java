package util;

import org.smartregister.AllConstants;
import org.smartregister.tbr.BuildConfig;

/**
 * Created by SGithengi on 09/10/17.
 */
public class
TbrConstants extends AllConstants {

    public static final long MAX_SERVER_TIME_DIFFERENCE = BuildConfig.MAX_SERVER_TIME_DIFFERENCE;
    public static final boolean TIME_CHECK = BuildConfig.TIME_CHECK;
    public static final String LAST_SYNC_TIMESTAMP = "LAST_SYNC_TIMESTAMP";
    public static final String LAST_CHECK_TIMESTAMP = "LAST_SYNC_CHECK_TIMESTAMP";
    public static final String LAST_VIEWS_SYNC_TIMESTAMP = "LAST_VIEWS_SYNC_TIMESTAMP";

    public static final String PATIENT_TABLE_NAME = "ec_patient";
    public static final String CONTACT_TABLE_NAME = "ec_contact";
    public static final String TBREACH_ID = "TBREACH ID";

    public static final String VIEW_CONFIGURATION_PREFIX = "ViewConfiguration_";

    public static final class KEY {
        public static final String FIRST_NAME = "first_name";
        public static final String LAST_NAME = "last_name";
        public static final String DOB = "dob";
        public static final String AGE = "age";
        public static final String PARTICIPANT_ID = "participant_id";
        public static final String PROGRAM_ID = "program_id";
        public static final String GENDER = "gender";
        public static final String BASE_ENTITY_ID = "base_entity_id";
        public static final String FIRST_ENCOUNTER = "first_encounter";
        public static final String LAST_INTERACTED_WITH = "last_interacted_with";
        public static final String DIAGNOSIS_DATE = "diagnosis_date";
        public static final String TREATMENT_INITIATION_DATE = "treatment_initiation_date";
        public static final String BASELINE = "baseline";
        public static final String NEXT_VISIT_DATE = "next_visit_date";
        public static final String TREATMENT_REGIMEN = "treatment_regimen";
        public static final String OTHER_REGIMEN = "regimen_oth";
        public static final String TREATMENT_PHASE = "treatment_phase";
        public static final String TREATMENT_MONTH = "month";
        public static final String SMR_NEXT_VISIT_DATE = "smear_due_date";
        public static final String PRESUMPTIVE = "presumptive";
        public static final String CONFIRMED_TB = "confirmed_tb";
        public static final String DATE_REMOVED = "date_removed";
        public static final String ATTRIBUTE_DATEREMOVED = "dateRemoved";//different
        public static final String DEATHDATE = "deathdate";
        public static final String PARENT_ENTITY_ID = "parent_entity_id";
        public static final String RELATIONAL_ID = "relational_id";
        public static final String INDEX = "_index";

    }


    public static final class RESULT {
        public static final String MTB_RESULT = "mtb_result";
        public static final String RIF_RESULT = "rif_result";
        public static final String XRAY_RESULT = "xray_result";
        public static final String CULTURE_RESULT = "culture_result";
        public static final String TEST_RESULT = "test_result";
        public static final String ERROR_CODE = "error_code";
    }

    public static final class REGISTER_COLUMNS {
        public static final String PATIENT = "patient";
        public static final String RESULTS = "results";
        public static final String DIAGNOSE = "diagnose";
        public static final String XPERT_RESULTS = "xpert_results";
        public static final String SMEAR_RESULTS = "smr_results";
        public static final String TREAT = "treat";
        public static final String DIAGNOSIS = "diagnosis";
        public static final String INTREATMENT_RESULTS = "intreatment_results";
        public static final String FOLLOWUP_SCHEDULE = "followup_schedule";
        public static final String SMEAR_SCHEDULE = "smr_schedule";
        public static final String TREATMENT = "treatment";
        public static final String FOLLOWUP = "followup";
        public static final String BASELINE = "baseline";

    }

    public static final class VIEW_CONFIGS {

        public static final String COMMON_REGISTER_HEADER = "common_register_header";
        public static final String COMMON_REGISTER_ROW = "common_register_row";


        public static final String PRESUMPTIVE_REGISTER = "presumptive_register";
        public static final String PRESUMPTIVE_REGISTER_HEADER = "presumptive_register_header";
        public static final String PRESUMPTIVE_REGISTER_ROW = "presumptive_register_row";

        public static final String POSITIVE_REGISTER = "positive_register";
        public static final String POSITIVE_REGISTER_HEADER = "positive_register_header";
        public static final String POSITIVE_REGISTER_ROW = "positive_register_row";

        public static final String INTREATMENT_REGISTER = "intreatment_register";
        public static final String INTREATMENT_REGISTER_HEADER = "intreatment_register_header";
        public static final String INTREATMENT_REGISTER_ROW = "intreatment_register_row";

        public static final String SAMPLE_FORM = "sample_form";
        public static final String SAMPLE_FORM_HEADER = "sample_form_header";
        public static final String SAMPLE_FORM_ROW = "sample_form_row";

        public static final String EMNCH_FORM = "emnch_form";
        public static final String EMNCH_FORM_HEADER = "emnch_form_header";
        public static final String EMNCH_FORM_ROW = "emnch_form_row";

    }

    public static final class ENKETO_FORMS {
        public static final String SCREENING_FORM = "add_presumptive_patient";
        public static final String GENE_XPERT = "result_gene_xpert";
        public static final String SMEAR = "result_smear";
        public static final String CHEST_XRAY = "result_chest_xray";
        public static final String CULTURE = "result_culture";
        public static final String DIAGNOSIS = "diagnosis";
        public static final String ADD_POSITIVE_PATIENT = "add_positive_patient";
        public static final String TREATMENT_INITIATION = "treatment_initiation";
        public static final String FOLLOWUP_VISIT = "followup_visit";
        public static final String ADD_TB_CONTACT = "add_tb_contact";
        public static final String ADD_IN_TREATMENT_PATIENT = "add_intreatment_patient";
        public static final String CONTACT_SCREENING = "contact_screening";
        public static final String SAMPLE_FORM = "sample_form";
        public static final String EMNCH_FORM = "emnch_form";

    }
}