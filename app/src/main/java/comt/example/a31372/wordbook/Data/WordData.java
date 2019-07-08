package comt.example.a31372.wordbook.Data;

//单词数据
public class WordData {
    private int wordID;
    private String word;//单词
    private String word_translation;//单词翻译
    private int chapter;//章节

    public String getWord_translation() {
        return word_translation;
    }

    public void setWord_translation(String word_translation) {
        this.word_translation = word_translation;
    }

    public int getChapter() {
        return chapter;
    }

    public void setChapter(int chapter) {
        this.chapter = chapter;
    }

    public void setWords(String str)
    {
        word = str;
    }

    public String getWords()
    {
        return word;
    }

    public int getWordID(){ return wordID;}

    public void setWordID(int wordid){
        this.wordID = wordid;
    }
}
