package org.smartregister.tbr.helper.view;

import android.content.Context;
import android.os.Handler;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.TextView;

import org.apache.commons.lang3.text.WordUtils;
import org.smartregister.tbr.R;
import org.smartregister.tbr.repository.ResultDetailsRepository;
import org.smartregister.tbr.util.Constants;
import org.smartregister.tbr.util.Utils;

import java.util.Map;

import util.TbrConstants;
import util.TbrSpannableStringBuilder;

import static org.smartregister.tbr.util.Constants.KEY.LAST_INTERACTED_WITH;

/**
 * Created by ndegwamartin on 23/11/2017.
 */

public class RenderPositiveResultsCardHelper extends BaseRenderHelper {
    private static final String DETECTED = "detected";

    public RenderPositiveResultsCardHelper(Context context, ResultDetailsRepository detailsRepository) {
        super(context, detailsRepository);
    }


    @Override
    public void renderView(String baseEntityId, View view) {
        //Inherited
    }

    @Override
    public void renderView(final String baseEntityId, final View view, final Map<String, String> extra) {
        new Handler().post(new Runnable() {

            @Override
            public void run() {
                String dateString = context.getString(R.string.first_ecnounter);
                if (extra.containsKey(LAST_INTERACTED_WITH) && !extra.get(LAST_INTERACTED_WITH).isEmpty()) {
                    dateString += Constants.CHAR.SPACE + Utils.formatDateFromLong(Long.valueOf(extra.get(LAST_INTERACTED_WITH)).intValue(), "dd MMM yyyy");
                }
                view.setTag(baseEntityId);
                TextView firstEncouterDateView = (TextView) view.findViewById(R.id.firstEncounterDateTextView);
                firstEncouterDateView.setText(dateString);
                TextView results = (TextView) view.findViewById(R.id.result_details);
                Map<String, String> testResults = ((ResultDetailsRepository) repository).getClientResultDetails(baseEntityId);
                ForegroundColorSpan redForegroundColorSpan = new ForegroundColorSpan(
                        context.getResources().getColor(android.R.color.holo_red_dark));
                ForegroundColorSpan blackForegroundColorSpan = new ForegroundColorSpan(
                        context.getResources().getColor(android.R.color.black));
                TbrSpannableStringBuilder stringBuilder = new TbrSpannableStringBuilder();
                if (testResults.containsKey(TbrConstants.RESULT.MTB_RESULT)) {
                    stringBuilder.append("Xpe ");
                    if (testResults.get(TbrConstants.RESULT.MTB_RESULT).equals(DETECTED))
                        stringBuilder.append("+ve", redForegroundColorSpan);
                    else
                        stringBuilder.append("-ve", redForegroundColorSpan);
                    stringBuilder.append("/");
                    if (testResults.containsKey(TbrConstants.RESULT.RIF_RESULT) && testResults.get(TbrConstants.RESULT.RIF_RESULT).equals(DETECTED))
                        stringBuilder.append("+ve", redForegroundColorSpan);
                    else
                        stringBuilder.append("-ve", redForegroundColorSpan);
                }
                if (testResults.containsKey(TbrConstants.RESULT.TEST_RESULT)) {
                    if (stringBuilder.length() > 0)
                        stringBuilder.append(",\t");
                    stringBuilder.append("Smr ");
                    switch (testResults.get(TbrConstants.RESULT.TEST_RESULT)) {
                        case "one_plus":
                            stringBuilder.append("1+", redForegroundColorSpan);
                            break;
                        case "two_plus":
                            stringBuilder.append("2+", redForegroundColorSpan);
                            break;
                        case "three_plus":
                            stringBuilder.append("3+", redForegroundColorSpan);
                            break;
                        default:
                            stringBuilder.append(WordUtils.capitalize(testResults.get(TbrConstants.RESULT.TEST_RESULT).substring(0, 2)), redForegroundColorSpan);
                            break;
                    }
                }

                if (testResults.containsKey(TbrConstants.RESULT.CULTURE_RESULT)) {
                    if (stringBuilder.length() > 0)
                        stringBuilder.append(", ");
                    stringBuilder.append("Cul ");
                    stringBuilder.append(WordUtils.capitalizeFully(testResults.get(TbrConstants.RESULT.CULTURE_RESULT).substring(0, 3)), blackForegroundColorSpan);
                }
                if (testResults.containsKey(TbrConstants.RESULT.XRAY_RESULT)) {
                    if (stringBuilder.length() > 0)
                        stringBuilder.append(",\t");
                    stringBuilder.append("CXR ");
                    if (testResults.get(TbrConstants.RESULT.XRAY_RESULT).equals("indicative"))
                        stringBuilder.append("Ind", blackForegroundColorSpan);
                    else
                        stringBuilder.append("Non", blackForegroundColorSpan);

                }
                if (stringBuilder.length() > 0) {
                    results.setVisibility(View.VISIBLE);
                    view.findViewById(R.id.noResultsTextView).setVisibility(View.GONE);
                    results.setText(stringBuilder);
                } else {
                    results.setVisibility(View.GONE);
                    view.findViewById(R.id.noResultsTextView).setVisibility(View.VISIBLE);
                }
            }

        });

    }
}