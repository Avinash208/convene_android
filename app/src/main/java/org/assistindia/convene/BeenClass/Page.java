package org.assistindia.convene.BeenClass;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class Page {
    private ArrayList<String> answersList;
    public static final int NUM_PAGES = 105;
    private static final SimpleDateFormat FORMATTER = new SimpleDateFormat("HH:mm:ss, dd/MM/yyyy");
    private static long firstId;
    private static long lastId;
    private int skip_from;
    private int questionId;
    private int questionNumber;
    private int answerType;
    private String question;
    private String answer;
    private boolean multiple_entry;
    private String lang;
    private int blockId;
    private String subQuestion;
    private String typologyId;
    private String helpText;
    private String toolTip;
    private String mandatory;
    private String validation;
    private String locationLevels;
    private  String partnerId;

    public static long getFirstId() {
        return firstId;
    }

    public static void setFirstId(long firstId) {
        Page.firstId = firstId;
    }

    public static long getLastId() {
        return lastId;
    }

    public static void setLastId(long lastId) {
        Page.lastId = lastId;
    }

    public void setSkip_from(int skip_from) {
        this.skip_from = skip_from;
    }

    public boolean isMultiple_entry() {
        return multiple_entry;
    }

    public void setMultiple_entry(boolean multiple_entry) {
        this.multiple_entry = multiple_entry;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public void setBlockId(int blockId) {
        this.blockId = blockId;
    }

    public void setSubQuestion(String subQuestion) {
        this.subQuestion = subQuestion;
    }

    public void setHelpText(String helpText) {
        this.helpText = helpText;
    }

    public void setToolTip(String toolTip) {
        this.toolTip = toolTip;
    }

    public void setMandatory(String mandatory) {
        this.mandatory = mandatory;
    }

    public void setQuestionId(int questionId) {
        this.questionId = questionId;
    }

    public void setQuestionNumber(int questionNumber) {
        this.questionNumber = questionNumber;
    }

    public void setAnswerType(int answerType) {
        this.answerType = answerType;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getValidation() {
        return validation;
    }

    public void setValidation(String validation) {
        this.validation = validation;
    }

    public String getTypologyId() {
        return typologyId;
    }

    public void setTypologyId(String typologyId) {
        this.typologyId = typologyId;
    }

    public static void setFirst(long id) {
        firstId = id;
    }
    
    public static void setLast(long id) {
        lastId = id;
    }
    public void setAnswersList(ArrayList<String> list) {
    	answersList = list;
    }
    public Page()
    {
    	
    }

    public String getHelpText() {
        System.out.println("Page.getHelpText--testing"+helpText);
        return helpText;
    }

    public String getToolTip() {
        return toolTip;
    }

    public Page(int skip_from)
    {
    	this.skip_from = skip_from;
    }
    public int getSkip_from()
    {
    	return skip_from;
    }
    
    public Page(int id, int questionNumber, int answerType, String question, String answer,
                boolean multiple_entry, ArrayList<String> answersList, int blockId, String subQuestion, String tid, String ht, String tt, String mandat, String validation) {
        this.questionId = id;
        this.questionNumber = questionNumber;
        this.answerType = answerType;
        this.question = question;
        this.answer=answer;
        this.multiple_entry = multiple_entry;
        this.answersList = answersList;
        this.blockId = blockId;
        this.subQuestion = subQuestion;
        this.typologyId=tid;
        this.helpText=ht;
        this.toolTip=tt;
        this.mandatory=mandat;
        this.validation=validation;
    }
    
    public String getLang() {
        return lang;
    }
    public int getQuestionId() {
        return questionId;
    }
    
    public int getQuestionNumber() {
        return questionNumber;
    }
    
    public int getAnswerType() {
        return answerType;
    }
   
    public String getQuestion() {
        return question;
    }
    
    public String getAnswer() {
        return answer;
    }
    
    public boolean getMultipleEntry() {
        return multiple_entry;
    }
    
    public ArrayList<String> getAnswersList() {
        return answersList;
    }
    public int getBlockId()
    {
    	return blockId;
    }
    
    public boolean isFirst() {
        return questionId == firstId;
    }
    
    public boolean isLast() {
        return questionId == lastId;
    }

    public String getSubquestioncode()
    {
    	return subQuestion;
    }

    public String getMandatory() {
        return mandatory;
    }

    public String getSubQuestion() {
        return subQuestion;
    }

    public String getLocationLevels() {
        return locationLevels;
    }

    public void setLocationLevels(String locationLevels) {
        this.locationLevels = locationLevels;
    }

    public String getPartnerId() {
        return partnerId;
    }

    public void setPartnerId(String partnerId) {
        this.partnerId = partnerId;
    }
}