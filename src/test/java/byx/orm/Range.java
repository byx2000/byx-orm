package byx.orm;

public class Range {
    private Integer low;
    private Integer high;

    public Range(int low, int high) {
        this.low = low;
        this.high = high;
    }

    public void setLow(Integer low) {
        this.low = low;
    }

    public Integer getLow() {
        return low;
    }

    public void setHigh(Integer high) {
        this.high = high;
    }

    public Integer getHigh() {
        return high;
    }
}
