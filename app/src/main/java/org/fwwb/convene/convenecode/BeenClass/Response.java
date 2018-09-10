package org.fwwb.convene.convenecode.BeenClass;

import android.os.Parcel;
import android.os.Parcelable;


public class Response implements Parcelable,Parcelable.Creator{
	private String ans_code;
	private String answer;
	private String q_id;
	private String q_type;
	private String sub_questionId;
	private int q_code;
	private int prime_key;
	private String typologyId;
	private int group_id;
	private int primaryID;

	public Response(){

	}

	public Response(String q_id, String answer, String ans_code, String sub_questionId, int q_code, int prime_key, String tid, int Group_id, int primaryID,String q_type) {
		this.ans_code = ans_code;
		this.answer = answer;
		this.q_id = q_id;
		this.q_type = q_type;
		this.sub_questionId = sub_questionId;
		this.q_code = q_code;
		this.prime_key = prime_key;
		this.typologyId=tid;
		this.group_id=Group_id;
		this.primaryID=primaryID;

	}

	protected Response(Parcel in) {
		ans_code = in.readString();
		answer = in.readString();
		q_id = in.readString();
		q_type = in.readString();
		sub_questionId = in.readString();
		q_code = in.readInt();
		prime_key = in.readInt();
		typologyId = in.readString();
		group_id=in.readInt();
		primaryID=in.readInt();
	}

	public static final Creator<Response> CREATOR = new Response();

	@Override
	public Response createFromParcel(Parcel in) {
		return new Response(in);
	}

	@Override
	public Response[] newArray(int size) {
		return new Response[size];
	}

	public String getTypologyId() {
		return typologyId;
	}

	public void setTypologyId(String typologyId) {
		this.typologyId = typologyId;
	}

	public String getAnswer() {
		return answer;
	}
	public void setAnswer(String answer) {
		this.answer = answer;
	}
	public String getAns_code() {
		return ans_code;
	}

	public String getQ_id() {
		return q_id;
	}

	public void setQ_id(String q_id) {
		this.q_id = q_id;
	}

	public String getSub_questionId() {
		return sub_questionId;
	}

	public void setSub_questionId(String sub_questionId) {
		this.sub_questionId = sub_questionId;
	}

	public int getQcode() {
		return q_code;
	}
	public int getPrimarykey() {
		return prime_key;
	}

	public int getGroup_id() {
		return group_id;
	}

	public void setGroup_id(int group_id) {
		this.group_id = group_id;
	}

	public int getPrimaryID() {
		return primaryID;
	}

	public void setPrimaryID(int primaryID) {
		this.primaryID = primaryID;
	}


	public String getQ_type() {
		return q_type;
	}

	public void setQ_type(String q_type) {
		this.q_type = q_type;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		/* Nothig to do in this method*/

		dest.writeString(ans_code);
		dest.writeString(answer);
		dest.writeString(q_id);
		dest.writeString(q_type);
		dest.writeString(sub_questionId);
		dest.writeInt(q_code);
		dest.writeInt(prime_key);
		dest.writeString(typologyId);
		dest.writeInt(group_id);
		dest.writeInt(primaryID);
	}
	
}
