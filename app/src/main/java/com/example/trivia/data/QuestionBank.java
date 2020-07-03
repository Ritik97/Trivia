package com.example.trivia.data;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.example.trivia.controller.MySingleton;
import com.example.trivia.model.Question;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

/**
 * This class will be responsible for fetching the questions for the API and create the questions
 * "https://raw.githubusercontent.com/curiousily/simple-quiz/master/script/statements-data.json" is the API we
 * gonna be using in this application to get the data.
 * Here, the root of our data is a JSON Array and it also has arrays as its children; which 2 elements
 * i.e. the question and its answer at 1st and 2nd indices respectively
 */

public class QuestionBank {
    /**
     * In this class, we will create an ArrayList of Question class type, which will hold all the Question
     * objects and whenever we need to fetch the questions, we can invoke the QuestionBank class and call
     * getQuestions(), which will return back the ArrayList of questions
     */

    private String url = "https://raw.githubusercontent.com/curiousily/simple-quiz/master/script/statements-data.json";

    ArrayList<Question> questionArrayList = new ArrayList<>();
    private Context ctx;

    public QuestionBank(Context context) {
        this.ctx = context;
    }


    public List<Question> getQuestions(final AnswerListAsyncResponse callback) {

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                url,
                (JSONArray) null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {

                        Log.d("Json Stuff", "onResponse: " + response);

                        for (int i = 0; i < response.length(); i++) {

                            try {

                                Question question = new Question();
                                question.setQuestion(response.getJSONArray(i).get(0).toString());
                                question.setAnswerTrue(response.getJSONArray(i).getBoolean(1));

                                //add Question objects to ArrayList
                                questionArrayList.add(question);
                                Log.d(TAG, "questionObject: " + question);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                        if (callback != null)
                            callback.ProcessFinished(questionArrayList);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        MySingleton.getInstance(ctx.getApplicationContext()).addToRequestQueue(jsonArrayRequest);
        return questionArrayList; //return the list
    }

}
