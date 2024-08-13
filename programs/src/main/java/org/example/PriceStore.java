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

            //three cases:
            //case 1: handle new item ---> diff <0
            //case 2: handle exact macthes --> if diff == 0
            //case 3: handle single overlap --> diff == 1
            //case 4: handle multiple overlap --> diff >1 // this one is yet to be implemented

            // the diff if-else blocks look big but most of the code are repeatable and can be refactored into separate function


            int diff = en_inx - st_inx -1;

           // System.out.println(diff);



            if(diff < 0){
                //enter new item if start time matches or end time matches
               if(st_match){

                   int index = find_index_start(inputItemStartDate);

                   RangePriceStore r1 = makePriceObject(index, inputItemEndDate);


                   rangePrice.remove(rangePrice.get(index));
                   rangePrice.add(r1);
                   rangePrice.add(input_item);

               } else if (en_match) {

                   int index = find_index_end(inputItemEndDate);
                   RangePriceStore r1 = makePriceObject2(index, inputItemStartDate);

                   rangePrice.remove(rangePrice.get(index));
                   rangePrice.add(r1);
                   rangePrice.add(input_item);

               } else if (inputItemStartDate < nearestStartDate &&  inputItemEndDate > nearestEndDate && diff != -2) {

                   int index1 = find_index_end(nearestEndDate);
                   int index2 = find_index_start(nearestStartDate);
                   int index = 0;
                   if(index1 == -1){
                       index = index2;
                   }else {

                       index = index1;
                   }

                   RangePriceStore existing_ele = rangePrice.get(index);
                   long num_st_ele = existing_ele.getStartDateInEpoch();
                   long num_en_ele = existing_ele.getEndDateInEpoch();

                   if(inputItemStartDate<num_st_ele){
                       RangePriceStore r1 = makePriceObject(index, inputItemEndDate);

                       rangePrice.remove(rangePrice.get(index));
                       rangePrice.add(input_item);
                       rangePrice.add(r1);
                   }else{
                       RangePriceStore r1 = makePriceObject2(index, inputItemStartDate);


                       rangePrice.remove(rangePrice.get(index));
                       rangePrice.add(input_item);
                       rangePrice.add(r1);
                   }



               } else  {
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
                       RangePriceStore r1 = makePriceObject2(index, inputItemStartDate);


                       RangePriceStore r2 = makePriceObject(index, inputItemEndDate);



                       rangePrice.remove(rangePrice.get(index));

                       rangePrice.add(r1);
                       rangePrice.add(r2);

                   }



                    rangePrice.add(input_item);

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
                }else if(en_match){

                    int index = find_index_end(inputItemEndDate);

                    rangePrice.remove(rangePrice.get(index));
                    rangePrice.add(input_item);
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
                    }else {
                        index  = find_index_end(nearestStartDate);
                        RangePriceStore r1 = makePriceObject2(index, inputItemStartDate);



                        RangePriceStore r2 = makePriceObject(index+1, inputItemStartDate);

                        rangePrice.remove(rangePrice.get(index));
                        rangePrice.remove(rangePrice.get(index));
                        rangePrice.add(r1);
                        rangePrice.add(r2);
                        rangePrice.add(input_item);


                    }
                }
            }else if(diff == 1){

                if(st_match) {

                    int index = find_index_start(nearestStartDate);

                    rangePrice.remove(rangePrice.get(index));

                    RangePriceStore r1 = makePriceObject(index, inputItemEndDate);

                    rangePrice.remove(rangePrice.get(index));

                    rangePrice.add(r1);
                    rangePrice.add(input_item);
                } else if (en_match) {
                    int index = find_index_end(nearestEndDate);

                    rangePrice.remove(rangePrice.get(index));
                    RangePriceStore r1 = makePriceObject2(index-1, inputItemStartDate);


                    rangePrice.remove(rangePrice.get(index-1));
                    rangePrice.add(r1);
                    rangePrice.add(input_item);

                } else if (inputItemStartDate < nearestStartDate && inputItemEndDate < nearestEndDate) {
                    int index = find_index_start(nearestStartDate);
                    System.out.println("this block");
                    rangePrice.remove(rangePrice.get(index));
                    RangePriceStore r1 = makePriceObject(index, inputItemEndDate);

                    rangePrice.remove(rangePrice.get(index));

                    rangePrice.add(r1);
                    rangePrice.add(input_item);

                }else {
                    int index = find_index_end(nearestEndDate);

                    rangePrice.remove(rangePrice.get(index));

                    RangePriceStore r1 = makePriceObject2(index-1, inputItemStartDate);

                    rangePrice.remove(rangePrice.get(index-1));
                    rangePrice.add(r1);
                    rangePrice.add(input_item);
                }

            }else {
                // for more than single overlap use this block
                System.out.println("working on this part");
            }

            Collections.sort(rangePrice, Comparator.comparingLong(RangePriceStore::getStartDateInEpoch).thenComparing(RangePriceStore::getEndDateInEpoch));
            updateTimeStamps();
        }

    }

    private void updateTimeStamps() {
        timeStamp.clear();
        for (RangePriceStore r : rangePrice) {
            timeStamp.add(r.getStartDateInEpoch());
            timeStamp.add(r.getEndDateInEpoch());
        }
    }

    private RangePriceStore makePriceObject(int index, long inputItemEndDate){
        RangePriceStore r1 = new RangePriceStore();
        r1.setSalePrice(rangePrice.get(index).getSalePrice());
        r1.setStartDate(DateTimeUtil.longToDate(inputItemEndDate+incDay()));
        r1.setEndDate(rangePrice.get(index).getEndDate());
        return r1;
    }

    private RangePriceStore makePriceObject2(int index, long inputItemStartDate){
        RangePriceStore r1 = new RangePriceStore();
        r1.setSalePrice(rangePrice.get(index).getSalePrice());
        r1.setStartDate(rangePrice.get(index).getStartDate());
        r1.setEndDate(DateTimeUtil.longToDate(inputItemStartDate-1));
        return r1;
    }

    /////////

   
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
