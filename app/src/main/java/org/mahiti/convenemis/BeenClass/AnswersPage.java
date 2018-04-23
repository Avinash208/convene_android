package org.mahiti.convenemis.BeenClass;

public class AnswersPage {
    private String ansCode;
	private String answer;
	private String validation;
	private int maxValidation;
	private int id;

	public AnswersPage(String ansCode, String answer, int ansFlag,
					   String validation, int id, int minValidation, int maxValidation,
					   String errorMsg, String innerValidation) {
        int ansFlag1 = ansFlag;
		this.ansCode = ansCode;
		this.answer = answer;
		this.id = id;
		int minValidation1 = minValidation;
		this.maxValidation = maxValidation;
		String errorMsg1 = errorMsg;
		this.validation = validation;
		String innerValidation1 = innerValidation;
	}

	public String getAnswer() {
		return answer;
	}

	public String getAnswerCode() {
		return ansCode;
	}

	public String getValidation() {
		return validation;
	}

	public int getId() {
		return id;
	}

	public int getMax_validation() {
		return maxValidation;
	}

	public  String toString(){
		return answer;
	}

}
