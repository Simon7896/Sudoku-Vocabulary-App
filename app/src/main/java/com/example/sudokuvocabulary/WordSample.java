package com.example.sudokuvocabulary;

public class WordSample {
    //private long listID;
    private String word;
    private String translation;

    public WordSample(String word, String translation) {
       this.word = word;
       this.translation = translation;
    }

    public WordSample() {
        this(null, null);
    }

    //public long getListID() {return listID;}

    //public void setListID(long listID) {this.listID = listID;}

    public String getWord() {return word;}

    public void setWord(String word) {
        this.word = word;
    }

    public String getTranslation() {
        return translation;
    }

    public void setTranslation(String translation) {
        this.translation = translation;
    }

    @Override
    public String toString() {
//        return "wordSample{" +
//                "word='" + word + '\'' +
//                ", translation='" + translation + '\'' +
//                '}';
        return word;
    }


}
