package uk.co.richardhorrocks.blp;

public class Times {
    private long id;
    private String date;
    private String name;
    private String time;
    private String level;
    private String distance;
    private int vo2;
    private boolean stopped;
    private boolean saved;
    
    public Times (long id, 
    		      String date, 
    		      String name, 
    		      String time, 
    		      String level, 
    		      String distance, 
    		      int vo2,
    		      boolean stopped,
    		      boolean saved) {
        this.id = id;
        this.date = date;
        this.name = name;
        this.time = time;
        this.level = level;
        this.distance = distance;
        this.vo2 = vo2;
        this.stopped = stopped;
        this.saved = saved;
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

    public String getDistance() {
        return this.distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }
    
    public int getVo2() {
        return this.vo2;
    }

    public void setVo2(int vo2) {
        this.vo2 = vo2;
    }

    public boolean getStopped() {
        return this.stopped;
    }

    public void setStopped(boolean stopped) {
        this.stopped = stopped;
    }    

    public boolean getSaved() {
        return this.saved;
    }

    public void setSaved(boolean saved) {
        this.saved = saved;
    }    
} 
