package org.example;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

public class RangePriceStore {
    private int salePrice;
    private long startDate;
    private long endDate;


    public RangePriceStore(){

    }

    public int getSalePrice() {
        return salePrice;
    }

    public String getEndDate() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

        Date en_date = new Date(this.endDate);
        String date = simpleDateFormat.format(en_date);
        return date;
    }

    public long getEndDateInEpoch(){
        return endDate;
    }

    public String getStartDate() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

        Date st_date = new Date(this.startDate);
        String date = simpleDateFormat.format(st_date);
        return date;
    }

    public long getStartDateInEpoch(){
        return startDate;
    }

    public void setSalePrice(int salePrice) {
        this.salePrice = salePrice;
    }

    public void setStartDate(String st) {

        this.startDate = DateTimeUtil.dateToLong(st);
    }

    public void setEndDate(String ed) {
        this.endDate =  DateTimeUtil.dateToLong(ed);
    }


    @Override
    public String toString() {
        return "RangePriceStore { " +
                "salePrice=" + getSalePrice() +
                ", startDate=" + getStartDate() +
                ", endDate=" + getEndDate() + " "+
                '}' + "\n";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RangePriceStore input_obj = (RangePriceStore) o;
        return salePrice == input_obj.salePrice &&
                Objects.equals(startDate, input_obj.startDate) &&
                Objects.equals(endDate, input_obj.endDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(salePrice, startDate, endDate);
    }
}