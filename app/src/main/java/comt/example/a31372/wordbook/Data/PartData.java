package comt.example.a31372.wordbook.Data;

public class PartData {
    private int part;//Part
    private String part_name;//Part的名字

    public String getPart_name() {
        return part_name;
    }

    public void setPart_name(String part_name) {
        this.part_name = part_name;
    }

    public void setPart(int str)
    {
        part = str;
    }

    public int getPart()
    {
        return part;
    }
}
