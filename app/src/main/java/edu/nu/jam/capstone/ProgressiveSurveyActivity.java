package edu.nu.jam.capstone;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.LinkedList;
import java.util.List;

import edu.nu.jam.capstone.Adapters.ProgressiveSurveyAdapter_Professor;
import edu.nu.jam.capstone.Adapters.ProgressiveSurveyAdapter_Student;
import edu.nu.jam.capstone.Data.ProgressiveSurveyData;
import edu.nu.jam.capstone.Interfaces.IProgressiveSurveyAdapterOperations;

public class ProgressiveSurveyActivity extends AppCompatActivity implements IProgressiveSurveyAdapterOperations
{
    private List<ProgressiveSurveyData> progressiveSurveyDataList = new LinkedList<>();
    private RecyclerView recyclerView;
    private int currentIndex;

    private final String responseDescriptionTag = "Class Response";
    private enum UserType {PROFESSOR, STUDENT};
    UserType userType;

    // TODO:  identify whether professor or student type and assign to Enum value.

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progressive_survey);

        // TODO:  delete this dummy data after we're connected to the backend:
        progressiveSurveyDataList.add( new ProgressiveSurveyData(
                "Explain issues related to variable binding, scope, and parameter passing.",
                0,
                0));
        progressiveSurveyDataList.add( new ProgressiveSurveyData(
                "Describe major programming language paradigms (procedural, object-oriented, functional, logic, concurrent).",
                0,
                0));
        progressiveSurveyDataList.add( new ProgressiveSurveyData(
                "Discuss factors in programming language theory such as context free grammars and parse trees.",
                0,
                0));
        progressiveSurveyDataList.add( new ProgressiveSurveyData(
                "Demonstrate working knowledge of compiler design in modern programming languages.",
                0,
                0));

        bindControls();
        Intent intent = getIntent();
        // check to see if the Intent's passed extra is professor or student:
        if (intent.getStringExtra("userType").toLowerCase().startsWith("p"))
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
    }

    private void bindControls()
    {
        recyclerView = findViewById(R.id.progressiveSurveyRecyclerView);
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
        // previously card coloring...undefined in this class.
    }
}
