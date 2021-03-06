package edu.nu.jam.whiteboard;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NavUtils;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import edu.nu.jam.whiteboard.Adapters.ProgressiveSurveyAdapter_Professor;
import edu.nu.jam.whiteboard.Adapters.ProgressiveSurveyAdapter_Student;
import edu.nu.jam.whiteboard.Data.ProgressiveSurveyData;
import edu.nu.jam.whiteboard.Interfaces.IProgressiveSurveyAdapterOperations;
import edu.nu.jam.whiteboard.Requestsmodule.AsyncResponder;
import edu.nu.jam.whiteboard.Requestsmodule.DatabaseHelper;
import edu.nu.jam.whiteboard.Requestsmodule.WeeklyELOResultsHelper;


public class ProgressiveSurveyActivity extends AppCompatActivity implements IProgressiveSurveyAdapterOperations
{
    //Intent extras
    public static final String EXTRA_PROGRESSIVE_CON_1 = "progressiveConfidence_1";
    public static final String EXTRA_PROGRESSIVE_CON_2 = "progressiveConfidence_2";
    public static final String EXTRA_PROGRESSIVE_CON_3 = "progressiveConfidence_3";
    public static final String EXTRA_PROGRESSIVE_CON_4 = "progressiveConfidence_4";

    private List<ProgressiveSurveyData> progressiveSurveyDataList = new LinkedList<>();
    private RecyclerView recyclerView;
    private int currentIndex;
    private FloatingActionButton submitSurveyFAB;

    private DatabaseHelper databaseHelper = new DatabaseHelper();

    private final String responseDescriptionTag = "Class Response";
    private enum UserType {PROFESSOR, STUDENT};
    UserType userType;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progressive_survey);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        bindControls();
        // check to see if the Intent's passed extra is professor or student:
        if (databaseHelper.GetUserTypeFromSharedPreferences(this).toLowerCase().startsWith("p"))
        {
            // professor-type logic:
            userType = UserType.PROFESSOR;
            configureRecyclerView();
        }
        else
        {
            // student-type logic:
            userType = UserType.STUDENT;
            configureRecyclerView();
        }

        submitSurvey();

    }

    private void submitSurvey()
    {
        final Intent intentReturn = new Intent();
        submitSurveyFAB.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                ArrayList<HashMap<String,String>> ELOAnswers = new ArrayList<>();
                for(int i = 0; i < progressiveSurveyDataList.size();i++) {
                    String questionid = progressiveSurveyDataList.get(i).getQuestionid();
                    String confidence = String.valueOf(progressiveSurveyDataList.get(i).getStudentConfidencePercentage());
                    intentReturn.putExtra("QuestionID_" + i, questionid);
                    intentReturn.putExtra("Confidence_"+i,confidence);

                }
                intentReturn.putExtra("NumberOfQuestions", progressiveSurveyDataList.size());

                setResult(Activity.RESULT_OK, intentReturn);
                finish();
            }
        });
    }

    private void bindControls()
    {
        recyclerView = findViewById(R.id.progressiveSurveyRecyclerView);
        submitSurveyFAB = findViewById(R.id.submitSurveyFAB);
        getWeeklyELOQuestions();
    }

    private void configureRecyclerView()
    {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        switch(userType) {
            case PROFESSOR:
                recyclerView.setAdapter(new ProgressiveSurveyAdapter_Professor(this));
                break;
            case STUDENT:
                recyclerView.setAdapter(new ProgressiveSurveyAdapter_Student(this));
                break;
        }
    }

    @Override
    public List<ProgressiveSurveyData> onGetLearningObjectives()
    {
        return progressiveSurveyDataList;
    }

    @Override
    public void onItemSelected(int position, View view)
    {

    }

    @Override
    public void setStudentConfidence(int position, double confidence)
    {
        progressiveSurveyDataList.get(position).setStudentConfidencePercentage(confidence);
    }

    @Override
    public void setClassConfidence(int position)
    {

    }

    private void resetEloValueInAdapter()
    {
        if (userType == UserType.PROFESSOR)
            ProgressiveSurveyAdapter_Professor.eloNumber = 1;
        else
            ProgressiveSurveyAdapter_Student.eloNumber = 1;
    }

    /**
     * Used for the toolbar
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
        if(item.getItemId() == android.R.id.home)
        {
            resetEloValueInAdapter();
            NavUtils.navigateUpFromSameTask(this);
        }
        return super.onOptionsItemSelected(item);
    }

    private void getWeeklyELOQuestions()
    {
        String sessionid = databaseHelper.GetSessionIdFromSharedPreferences(ProgressiveSurveyActivity.this);
        String weeklyid = "1"; // Prototype for week 1
        new WeeklyELOResultsHelper(new AsyncResponder() {
            @Override
            public void processFinish(String output) {
                databaseHelper.onGetWeeklyELOResultsFinished(output);
                ArrayList<HashMap<String,String>> weeklyELOResults = databaseHelper.getResultList();
                for(int i = 0; i < weeklyELOResults.size();i++) {
                    String id = weeklyELOResults.get(i).get("id");
                    String question = weeklyELOResults.get(i).get("question");
                    double average = Double.parseDouble(weeklyELOResults.get(i).get("average"));
                    String totalresponses = weeklyELOResults.get(i).get("totalresponse");

                    ProgressiveSurveyData eloData = new ProgressiveSurveyData(question,
                                                    average,
                                                    average,
                                                    id);

                    progressiveSurveyDataList.add(eloData);
                }
                recyclerView.getAdapter().notifyDataSetChanged();
            }
        },ProgressiveSurveyActivity.this, sessionid, weeklyid).execute();

    }
}
