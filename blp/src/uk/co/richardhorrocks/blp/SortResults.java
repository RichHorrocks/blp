package uk.co.richardhorrocks.blp;

import java.util.Comparator;

public class SortResults implements Comparator<Times> {

    private boolean ascending;
    private int sortType;

    public SortResults(boolean ascending, int sortType) {
        this.ascending = ascending;
        this.sortType = sortType;
    }

    /*
     * TODO There must be a more efficient way to do this?
     * Until then, also ignore the multiple returns...
     * 
     * (non-Javadoc)
     * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
     */
    @Override
    public int compare(Times lhs, Times rhs) {
                
        if (sortType == 1) {
            if (ascending) {
                return lhs.getDate().compareTo(rhs.getDate());    
            } else {
                return rhs.getDate().compareTo(lhs.getDate());                      
            }                      
        } else if (sortType == 2) {
            if (ascending) {
                return lhs.getName().compareTo(rhs.getName());    
            } else {
                return rhs.getName().compareTo(lhs.getName());                      
            }                      
        } else if (sortType == 3) {
            if (ascending) {
                return lhs.getLevel().compareTo(rhs.getLevel());    
            } else {
                return rhs.getLevel().compareTo(lhs.getLevel());                      
            }                      
        } else if (sortType == 4) {
            if (ascending) {
                return lhs.getTime().compareTo(rhs.getTime());    
            } else {
                return rhs.getTime().compareTo(lhs.getTime());                      
            }                      
        } else if (sortType == 5) {
            if (ascending) {
                return lhs.getVo2() - rhs.getVo2();    
            } else {
                return rhs.getVo2() - lhs.getVo2();                      
            }                      
        } else {
            return 0;
        }
    }
}
