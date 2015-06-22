package com.SOOHP;

public class Question {
	private String QuestionName;
	private String QuestionText;
	private int successful;
	private boolean asked;

	public Question() {

	}

	public Question(String newQuestionName, String newQuestionText) {
		this.QuestionName = newQuestionName;
		this.QuestionText = newQuestionText;
		successful = 0;
		asked = false;
	}

	public String getQuestionName() {
		return QuestionName;
	}

	public String getQuestionText() {
		return QuestionText;
	}

	public void incrementSuccessful() {
		successful++;
	}

	public void decrementSuccessful() {
		successful--;
	}

	public void setAsked() {
		asked = true;
	}

	public boolean getAsked() {
		return asked;
	}

}