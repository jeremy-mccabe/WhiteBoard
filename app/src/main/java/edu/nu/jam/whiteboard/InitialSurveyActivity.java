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

import edu.nu.jam.whiteboard.Adapters.InitialSurveyAdapter_Professor;
import edu.nu.jam.whiteboard.Adapters.InitialSurveyAdapter_Student;
import edu.nu.jam.whiteboard.Data.InitialSurveyData;
import edu.nu.jam.whiteboard.Interfaces.IInitialSurveyAdapterOperations;
import edu.nu.jam.whiteboard.Requestsmodule.AsyncResponder;
import edu.nu.jam.whiteboard.Requestsmodule.DatabaseHelper;
import edu.nu.jam.whiteboard.Requestsmodule.InitialSurveyResultsHelper;

public class InitialSurveyActivity extends AppCompatActivity implements IInitialSurveyAdapterOperations
{
	//return intent extras
	public static final String EXTRA_CONFIDENCE_1 = "confidence_1";
	public static final String EXTRA_CONFIDENCE_2 = "confidence_2";
	public static final String EXTRA_CONFIDENCE_3 = "confidence_3";
	public static final String EXTRA_CONFIDENCE_4 = "confidence_4";
	private List<InitialSurveyData> initialSurveyDataList = new LinkedList<>();
	private RecyclerView recyclerView;
	private int currentIndex;
	private DatabaseHelper dbHelper = new DatabaseHelper();
	private FloatingActionButton submitSurveyFAB;
	private ArrayList<HashMap<String,String>> initialsurveylist;


	private final String responseDescriptionTag = "Class Response";
	private enum UserType {PROFESSOR, STUDENT};
	UserType userType;


	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_initial_survey);
		Toolbar toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		bindControls();
		// check to see if the Intent's passed extra is professor or student:
		if (dbHelper.GetUserTypeFromSharedPreferences(this).toLowerCase().startsWith("p"))
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
		returnIntent();
	}

	private void returnIntent()
	{
		final Intent intentReturn = new Intent();
		submitSurveyFAB.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				intentReturn.putExtra(EXTRA_CONFIDENCE_1, initialSurveyDataList.get(0).getResponsePercentage());
				intentReturn.putExtra(EXTRA_CONFIDENCE_2, initialSurveyDataList.get(1).getResponsePercentage());
				intentReturn.putExtra(EXTRA_CONFIDENCE_3, initialSurveyDataList.get(2).getResponsePercentage());
				intentReturn.putExtra(EXTRA_CONFIDENCE_4, initialSurveyDataList.get(3).getResponsePercentage());
				setResult(Activity.RESULT_OK, intentReturn);
				finish();
			}
		});
	}

	private void bindControls()
	{
		recyclerView = findViewById(R.id.initialSurveyRecyclerView);
		submitSurveyFAB = findViewById(R.id.submitInitialFAB);
		getInitialSurveyResults();
	}

	private void configureRecyclerView()
	{
		recyclerView.setHasFixedSize(true);
		recyclerView.setLayoutManager(new LinearLayoutManager(this));
		switch(userType) {
			case PROFESSOR:
				recyclerView.setAdapter(new InitialSurveyAdapter_Professor(this));
				break;
			case STUDENT:
				recyclerView.setAdapter(new InitialSurveyAdapter_Student(this));
				break;
		}
	}

	@Override
	public List<InitialSurveyData> onGetInitialSurveyQuestions()
	{
		return initialSurveyDataList;
	}

	@Override
	public void onItemSelected(int position, View view)
	{

	}

	@Override
	public void setStudentResponsePercentage(int position, double response)
	{
		initialSurveyDataList.get(position).setResponsePercentage(response);
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
			NavUtils.navigateUpFromSameTask(this);
		}
		return super.onOptionsItemSelected(item);
	}

	private void getInitialSurveyResults()
	{
		new InitialSurveyResultsHelper(new AsyncResponder() {
			@Override
			public void processFinish(String output) {
				dbHelper.onGetInitialSurveyesultsFinished(output);
				initialsurveylist = dbHelper.getInitialResultsList();
				for(int i = 0;i < initialsurveylist.size();i++)
				{
					String question = initialsurveylist.get(i).get("question");
					String id = initialsurveylist.get(i).get("questionid");
					double percentage = Double.parseDouble(initialsurveylist.get(i).get("average"));

					InitialSurveyData data = new InitialSurveyData(question, responseDescriptionTag, percentage);

					initialSurveyDataList.add(data);
				}
				recyclerView.getAdapter().notifyDataSetChanged();
			}
		}, InitialSurveyActivity.this).execute();
	}
}