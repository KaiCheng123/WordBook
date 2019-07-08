package comt.example.a31372.wordbook.Data;

//章节数据
public class ChapterData {
    private int chapter;
    private String chapter_name;
    private int part;

    public int getPart() {
        return part;
    }

    public void setPart(int part) {
        this.part = part;
    }

    public String getChapter_name() {
        return chapter_name;
    }

    public void setChapter_name(String chapter_name) {
        this.chapter_name = chapter_name;
    }

    public void setChapter(int chapter)
    {
        this.chapter = chapter;
    }

    public int getChapter()
    {
        return chapter;
    }
}
