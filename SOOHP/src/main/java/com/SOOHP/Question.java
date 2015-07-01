package com.SOOHP;

public class Question {
	
	private String QuestionName;
	private String QuestionText;
	private String QuestionType;
	private boolean asked;

	public Question() {

	}

	public Question(String newQuestionType, String newQuestionName, String newQuestionText) {
		this.QuestionType = newQuestionType;
		this.QuestionName = newQuestionName;
		this.QuestionText = newQuestionText;
		asked = false;
	}

	public String getQuestionType() {
		return QuestionType;
	}
	
	public String getQuestionName() {
		return QuestionName;
	}

	public String getQuestionText() {
		return QuestionText;
	}

	public void setAsked() {
		asked = true;
	}

	public boolean getAsked() {
		return asked;
	}

}