package com.bhavesh.trivia.model;

import androidx.annotation.NonNull;

public class Question {
    private String statement;
    private boolean answer;

    public Question() {}

    public Question(String statement, boolean answer) {
        this.statement = statement;
        this.answer = answer;
    }

    public String getStatement() {
        return statement;
    }

    public boolean isAnswer() {
        return answer;
    }

    public void setStatement(String statement) {
        this.statement = statement;
    }

    public void setAnswer(boolean answer) {
        this.answer = answer;
    }

    @Override
    public String toString() {
        return "Question{" +
                "statement = " + statement +
                ", answer = " + answer +
                '}';
    }
}
