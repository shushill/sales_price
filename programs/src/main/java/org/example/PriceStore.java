package org.example;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PriceStore {

    private int basePrice;

    private List<RangePriceStore> rangePrice ;

    private TreeSet<Long> timeStamp;

    public PriceStore(int basePrice){
        this.basePrice = basePrice;
    }

    public int getBasePrice() {
        return basePrice;
    }

    public List<RangePriceStore> getRangePrice() {
        return rangePrice;
    }

    public void setBasePrice(int basePrice) {
        this.basePrice = basePrice;
    }

    public void setInputPrice(RangePriceStore inputPrice) {

        if(rangePrice != null){
            rangePrice.add(inputPrice);
            timeStamp.add(inputPrice.getStartDateInEpoch());
            timeStamp.add(inputPrice.getEndDateInEpoch());
        }else{
            rangePrice = new ArrayList<>();
            timeStamp = new TreeSet<>();
            timeStamp.add(inputPrice.getStartDateInEpoch());
            timeStamp.add(inputPrice.getEndDateInEpoch());
            rangePrice.add(inputPrice);
        }


    }

    public void setListOfInputRangePrice(List<RangePriceStore> rp){
        this.rangePrice = rp;
    }


    public void updateInputRangePrice(RangePriceStore input_item){

        //default match
        for(int i=0; i<rangePrice.size(); ++i){
            RangePriceStore item = rangePrice.get(i);
            if(input_item.equals(item)){
                System.out.println("Default price and date for this input already exist\n");
            }
        }



        //check if this item completely new item

        long firstItemStartDate = rangePrice.get(0).getStartDateInEpoch();
        long lastItemEndDate = rangePrice.get(rangePrice.size()-1).getEndDateInEpoch();

        long inputItemStartDate = input_item.getStartDateInEpoch();
        long inputItemEndDate = input_item.getEndDateInEpoch();

        if(inputItemEndDate < firstItemStartDate || inputItemStartDate > lastItemEndDate){
            rangePrice.add(input_item);
            System.out.println("sushil");
            Collections.sort(rangePrice, Comparator.comparingLong(RangePriceStore::getStartDateInEpoch).thenComparing(RangePriceStore::getEndDateInEpoch));
        }else{
            List<Long> list = new ArrayList<>(timeStamp);
            long nearestStartDate = 0;
            boolean st_match = false;
            if(timeStamp.contains(inputItemStartDate)){
                nearestStartDate = inputItemStartDate;
                st_match = true;
            }else{
                for(int i=0; i<list.size(); ++i){
                    if(list.get(i)>=inputItemStartDate){
                        nearestStartDate = list.get(i);
                        break;
                    }
                }
            }






            long nearestEndDate = Long.MAX_VALUE;
            boolean en_match = false;
            if(timeStamp.contains(inputItemEndDate)){
                nearestEndDate = inputItemEndDate;
                en_match = true;
            }else{
                for(int i= list.size()-1; i>=0; --i){
                    if(list.get(i)<=inputItemEndDate){
                        nearestEndDate = list.get(i);
                        break;
                    }
                }
            }




            int st_inx = list.indexOf(nearestStartDate);
            int en_inx = list.indexOf(nearestEndDate);

            // this is used to find number of timestamps between input start time and input end time;

            int diff = en_inx - st_inx -1;

            System.out.println(diff);



            if(diff < 0){
                //enter new item if start time matches or end time matches
               if(st_match){

                   int index = find_index_start(inputItemStartDate);

                   RangePriceStore r1 = new RangePriceStore();
                   r1.setSalePrice(rangePrice.get(index).getSalePrice());
                   r1.setStartDate(DateTimeUtil.longToDate(inputItemEndDate+incDay()));
                   r1.setEndDate(rangePrice.get(index).getEndDate());
                   rangePrice.remove(rangePrice.get(index));
                   rangePrice.add(r1);
                   rangePrice.add(input_item);
                   timeStamp.add(inputItemEndDate);
                   timeStamp.add(r1.getStartDateInEpoch());
                   //updateTimeStamps();

               } else if (en_match) {

                   int index = find_index_end(inputItemEndDate);

                   RangePriceStore r1 = new RangePriceStore();
                   r1.setSalePrice(rangePrice.get(index).getSalePrice());
                   r1.setStartDate(rangePrice.get(index).getStartDate());
                   r1.setEndDate(DateTimeUtil.longToDate(inputItemStartDate-1));
                   rangePrice.remove(rangePrice.get(index));
                   rangePrice.add(r1);
                   rangePrice.add(input_item);
                   timeStamp.add(inputItemStartDate);
                   timeStamp.add(r1.getEndDateInEpoch());
                   //updateTimeStamps();

               }


               else  {
                    int index = -1;
                   for(int i=0; i<rangePrice.size(); ++i){
                       long st_dt = rangePrice.get(i).getStartDateInEpoch();
                       long en_dt = rangePrice.get(i).getEndDateInEpoch();

                       if(st_dt <inputItemStartDate && inputItemEndDate < en_dt){
                           index = i;
                           break;
                       }
                   }
                   if(index != -1){
                       RangePriceStore r1 = new RangePriceStore();
                       r1.setSalePrice(rangePrice.get(index).getSalePrice());
                       r1.setStartDate(rangePrice.get(index).getStartDate());
                       r1.setEndDate(DateTimeUtil.longToDate(inputItemStartDate-1));

                       RangePriceStore r2 = new RangePriceStore();
                       r2.setSalePrice(rangePrice.get(index).getSalePrice());
                       r2.setStartDate(DateTimeUtil.longToDate(inputItemEndDate+incDay()));
                       r2.setEndDate(rangePrice.get(index).getEndDate());



                       rangePrice.remove(rangePrice.get(index));

                       rangePrice.add(r1);
                       rangePrice.add(r2);

                       timeStamp.add(r1.getStartDateInEpoch());
                       timeStamp.add(r1.getStartDateInEpoch());
                       timeStamp.add(r2.getStartDateInEpoch());
                       timeStamp.add(r2.getEndDateInEpoch());
                   }



                    rangePrice.add(input_item);
                    timeStamp.add(inputItemStartDate);
                    timeStamp.add(inputItemEndDate);
                   //updateTimeStamps();
               }
            }else if (diff == 0){

                if (st_match && en_match) {
                    int index = find_index_end(inputItemEndDate);
                    rangePrice.remove(rangePrice.get(index));
                    rangePrice.add(input_item);
                } else if(st_match){

                    int index = find_index_start(inputItemStartDate);

                    rangePrice.remove(rangePrice.get(index));
                    rangePrice.add(input_item);
                    timeStamp.add(inputItemStartDate);
                    timeStamp.add(inputItemEndDate);
                   // updateTimeStamps();
                }else if(en_match){

                    int index = find_index_end(inputItemEndDate);

                    rangePrice.remove(rangePrice.get(index));
                    rangePrice.add(input_item);
                    timeStamp.add(inputItemStartDate);
                    timeStamp.add(inputItemEndDate);
                    //updateTimeStamps();
                }else{
                    int index = -1;

                    for(int i=0; i<rangePrice.size(); ++i){
                        long st_num = rangePrice.get(i).getStartDateInEpoch();
                        long en_num = rangePrice.get(i).getEndDateInEpoch();
                        if( st_num == nearestStartDate && en_num == nearestEndDate){
                            index = i;
                            break;
                        }
                    }
                    if(index != -1){
                        rangePrice.remove(rangePrice.get(index));
                        rangePrice.add(input_item);
                        timeStamp.add(inputItemStartDate);
                        timeStamp.add(inputItemEndDate);
                        //updateTimeStamps();
                    }else {
                        index  = find_index_end(nearestStartDate);
                        RangePriceStore r1 = new RangePriceStore();
                        r1.setSalePrice(rangePrice.get(index).getSalePrice());
                        r1.setStartDate(rangePrice.get(index).getStartDate());
                        r1.setEndDate(DateTimeUtil.longToDate(inputItemStartDate-1));

                        RangePriceStore r2 = new RangePriceStore();
                        r2.setSalePrice(rangePrice.get(index+1).getSalePrice());
                        r2.setStartDate(DateTimeUtil.longToDate(inputItemStartDate+incDay()));
                        r2.setEndDate(rangePrice.get(index+1).getEndDate());

                        rangePrice.remove(rangePrice.get(index));
                        rangePrice.remove(rangePrice.get(index));
                        rangePrice.add(r1);
                        rangePrice.add(r2);
                        rangePrice.add(input_item);
                        timeStamp.add(r1.getEndDateInEpoch());
                        timeStamp.add(inputItemStartDate);
                        timeStamp.add(inputItemEndDate);
                        timeStamp.add(r2.getStartDateInEpoch());

                        //updateTimeStamps();




                    }
                }
            }else if(diff == 1){

                int index = find_index_start(nearestStartDate);

                rangePrice.remove(rangePrice.get(index));
                RangePriceStore r1 = new RangePriceStore();
                r1.setSalePrice(rangePrice.get(index).getSalePrice());
                r1.setStartDate(DateTimeUtil.longToDate(inputItemEndDate+incDay()));
                r1.setEndDate(rangePrice.get(index).getEndDate());

                rangePrice.remove(rangePrice.get(index));

                rangePrice.add(r1);
                rangePrice.add(input_item);
                timeStamp.add(inputItemStartDate);
                timeStamp.add(inputItemEndDate);
                timeStamp.add(r1.getStartDateInEpoch());
                //updateTimeStamps();


            }

            Collections.sort(rangePrice, Comparator.comparingLong(RangePriceStore::getStartDateInEpoch).thenComparing(RangePriceStore::getEndDateInEpoch));

        }










    }
//
    public int find_index_end(long num){
        int index = -1;

        for (int i = 0; i < rangePrice.size(); ++i) {
            if (rangePrice.get(i).getEndDateInEpoch() == num) {
                index = i;
                break;
            }
        }
        return index;
    }


    public int find_index_start(long num){
        int index = -1;
        for(int i=0; i<rangePrice.size(); ++i){
            if(rangePrice.get(i).getStartDateInEpoch() == num){
                index = i;
                break;
            }
        }
        return index;
    }





    public TreeSet<Long> getTimeStamp(){
        return timeStamp;
    }
//
    public long incDay(){
        return 86400000;
    }

    @Override
    public String toString() {
        return "PriceStore { " +
                "basePrice=" + getBasePrice() +
                ", \nrangePrice=" + getRangePrice() +
                '}';
    }


}
