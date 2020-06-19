// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps;

import java.util.*;

public final class FindMeetingQuery {
  public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
    Collection<TimeRange> return_value;

    List<TimeRange> listtransfer = new ArrayList<TimeRange>();
    Event[] event_array = events.toArray(new Event[events.size()]);

    int request_duration = (int)request.getDuration();

    


    if(events.size() == 1){

        int beginning_time= event_array[0].getWhen().start();
        int ending_time= event_array[0].getWhen().end();

        int same_atendee_adder = 0;

        Iterator<String> i = request.getAttendees().iterator();
        while(i.hasNext()){
            if(event_array[0].getAttendees().contains(i.next()) == true){
                same_atendee_adder = same_atendee_adder + 1;
            }
        }

        // adder = 1 go ahead but if = 0 TimeRange.WHOLE_DAY
        if(same_atendee_adder == 1){
            listtransfer.add(TimeRange.fromStartEnd(TimeRange.START_OF_DAY, beginning_time, false));
            listtransfer.add(TimeRange.fromStartEnd(ending_time, TimeRange.END_OF_DAY, true));
        }
        else{
            listtransfer.add(TimeRange.WHOLE_DAY);
        }
        
        return_value = listtransfer;
    }
    else if (events.size() == 0){
        if(request_duration > TimeRange.WHOLE_DAY.duration()){
            listtransfer.clear();
        }
        else{
            listtransfer.add(TimeRange.WHOLE_DAY);
        }
        
        return_value = listtransfer;
    }
    else if(events.size() >= 2 && request.getAttendees().size() > 1){
        if(event_array[0].getWhen().overlaps(event_array[1].getWhen()) == true){
            
            int beginning_time;
            int ending_time;
            
            if(event_array[0].getWhen().start() < event_array[1].getWhen().start()){
                beginning_time = event_array[0].getWhen().start();
            }
            else{
                beginning_time = event_array[1].getWhen().start();
            }
            
            if(event_array[0].getWhen().end() > event_array[1].getWhen().end()){
                ending_time = event_array[0].getWhen().end();
            }
            else{
                ending_time = event_array[1].getWhen().end();
            }

            listtransfer.add(TimeRange.fromStartEnd(TimeRange.START_OF_DAY, beginning_time, false));
            listtransfer.add(TimeRange.fromStartEnd(ending_time, TimeRange.END_OF_DAY, true));
        }
        else if(event_array[0].getWhen().overlaps(event_array[1].getWhen()) == false){
            int beginning_time= event_array[0].getWhen().start();
            int begend_time = event_array[0].getWhen().end();
            int endbeg_time = event_array[1].getWhen().start();
            int ending_time = event_array[1].getWhen().end();

            listtransfer.add(TimeRange.fromStartEnd(TimeRange.START_OF_DAY, beginning_time, false));
            listtransfer.add(TimeRange.fromStartEnd(begend_time, endbeg_time, false));
            listtransfer.add(TimeRange.fromStartEnd(ending_time, TimeRange.END_OF_DAY, true));
        }
        return_value = listtransfer;
    }


    else if(events.size()>= 2 && request.getAttendees().size() <= 1){
        if(event_array[0].getWhen().overlaps(event_array[1].getWhen()) == true){
            int beginning_time = event_array[0].getWhen().start();
            int ending_time = event_array[1].getWhen().end();
            int wholeday_counter =0;

            for(int i = 0; i < events.size(); i++){
                if(event_array[i].getWhen().duration() ==  TimeRange.WHOLE_DAY.duration()){
                    wholeday_counter++;
                }
            }
            if(wholeday_counter == events.size()){
                listtransfer.clear();
            }
            else{
                listtransfer.add(TimeRange.fromStartEnd(TimeRange.START_OF_DAY, beginning_time, false));
                listtransfer.add(TimeRange.fromStartEnd(ending_time, TimeRange.END_OF_DAY, true));
            }
            
        }
        else if(event_array[0].getWhen().overlaps(event_array[1].getWhen()) == false){
            int beginning_time= event_array[0].getWhen().start();
            int begend_time = event_array[0].getWhen().end();
            int endbeg_time = event_array[1].getWhen().start();
            int ending_time = event_array[1].getWhen().end();

            long freeSpace = ((event_array[1].getWhen().start()) - (event_array[0].getWhen().end()));

            if (request.getDuration() > freeSpace){
                listtransfer.clear();
            }
            else{
                listtransfer.add(TimeRange.fromStartDuration(begend_time, request_duration));
            }
        }
        return_value = listtransfer;
    }
    

    else{
        throw new UnsupportedOperationException("TODO: Implement this method");
    }
    return return_value;
  }
}
