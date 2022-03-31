package com.jhn.googlemaptest.model;

import java.io.Serializable;
import java.util.List;

public class ImageRes {
    private String error;
    private String answer;
    private int Number;

    public int getNumber() {
        return Number;
    }

    public void setNumber(int number) {
        Number = number;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}