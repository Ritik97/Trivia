package com.example.trivia.model;

/**This is the Model class which represents a single instance of data (or) question in this case.*/

public class Question {
    private String question;
    private boolean answerTrue;

    public Question() {
    }

    public Question(String question, boolean answerTrue) {
        this.question = question;
        this.answerTrue = answerTrue;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public boolean isAnswerTrue() {
        return answerTrue;
    }

    public void setAnswerTrue(boolean answerTrue) {
        this.answerTrue = answerTrue;
    }

    @Override
    public String toString() {
        //Using toString() to simply log a Plain Question object, to see what it contains
        return "Question{" +
                "question='" + question + '\'' +
                ", answerTrue=" + answerTrue +
                '}';
    }
}
