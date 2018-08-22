package org.yale.convene.beansClassSetQuestion;



public interface CallApis
{
    void blockApi(int needToCall,boolean flag);
    void languageBlock(int needToCall,boolean flag);
    void questionApi(int needToCall,boolean flag);
    void languageQuestionApi(int needToCall,boolean flag);
    void assessmentApi(int needToCall,boolean flag);
    void languageAssessmentApi(int needToCall,boolean flag);
    void skipMandatoryApi(int needToCall,boolean flag);
    void skipRulesApi(int needToCall,boolean flag);
    void optionsApi(int needToCall,boolean flag);
    void languageLabelsApi(int needToCall,boolean flag);
    void languageOptions(int needToCall,boolean flag);
}
