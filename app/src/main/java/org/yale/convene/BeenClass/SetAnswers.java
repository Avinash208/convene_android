package org.yale.convene.BeenClass;

public class SetAnswers {
	private String ans_code;
	private String answer;
	private String qid;

	public SetAnswers(String qid, String ans_code, String answer) {
		this.ans_code = ans_code;
		this.answer = answer;
		this.qid = qid;
	}

	public String getAnswer() {
		return answer;
	}

	public String getAns_code() {
		return ans_code;
	}
	public String getQid() {
		return qid;
	}

}
