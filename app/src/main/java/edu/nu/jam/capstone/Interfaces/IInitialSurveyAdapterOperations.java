package edu.nu.jam.capstone.Interfaces;

import android.view.View;

import java.util.List;

import edu.nu.jam.capstone.Data.InitialSurveyData;

public interface IInitialSurveyAdapterOperations
{
	List<InitialSurveyData> onGetInitialSurveyQuestions();
	void onItemSelected(int position, View view);
}
