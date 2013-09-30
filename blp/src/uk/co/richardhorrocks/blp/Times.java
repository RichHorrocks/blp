package uk.co.richardhorrocks.blp;

public class Times {
    private long id;
    private String date;
    private String name;
    private String time;
    private String level;
    private int vo2;

    public Times () {
        
    }
    
    public Times (long id, String date, String name, String time, String level, int vo2) {
        this.id = id;
        this.date = date;
        this.name = name;
        this.time = time;
        this.level = level;
        this.vo2 = vo2;
    }
    
    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDate() {
        return this.date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTime() {
        return this.time;
    }

    public void setTime(String time) {
        this.time = time;
    }
  
    public String getLevel() {
        return this.level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public int getVo2() {
        return this.vo2;
    }

    public void setVo2(int vo2) {
        this.vo2 = vo2;
    }    
} 
