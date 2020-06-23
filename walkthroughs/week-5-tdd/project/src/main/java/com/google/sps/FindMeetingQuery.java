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

    int firstEventStartTime;
    int firstEventEndTime;
    int secondEventStartTime;
    int secondEventEndTime;

  // The query function returns free time ranges that attendees might have imbetween meetings or 
  // after meetings.
  public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
    Collection<TimeRange> avaliableTime;

    List<TimeRange> listTransfer = new ArrayList<TimeRange>();
    Event[] eventArray = events.toArray(new Event[events.size()]);

    int requestDuration = (int)request.getDuration();

    SetTimes(eventArray);

    if(events.size() == 1){

        // sameAttendeeAdder is a flag that is checked to see if the person creating
        // a request is already involved in another meeting. If not they have the whole
        // day to request meeting since they do not have any meetings.
        int sameAttendeeAdder = 0;

        Iterator<String> i = request.getAttendees().iterator();
        while(i.hasNext()){
            if(eventArray[0].getAttendees().contains(i.next())){
                sameAttendeeAdder = sameAttendeeAdder + 1;
            }
        }

        // If sameAttendeeAdder is 1 the person has a meeting and must be given
        // the time range before and after the meeting.
        if(sameAttendeeAdder == 1){
            listTransfer.add(TimeRange.fromStartEnd(TimeRange.START_OF_DAY, firstEventStartTime, false));
            listTransfer.add(TimeRange.fromStartEnd(firstEventEndTime, TimeRange.END_OF_DAY, true));
        }

        // If the adder is 0 then the person has no other meetings to worry about and has
        // the whole day to create a meeting.
        else{
            listTransfer.add(TimeRange.WHOLE_DAY);
        }
        avaliableTime = listTransfer;
    }

    // If a person tries to create a meeting that takes longer than a day return an empty
    // range since it is not possible to go beyond a day meeting. If the person has no events
    // on their schedule, the avaliable timerange to create a meeting is the whole day.
    else if (events.size() == 0){
        if(requestDuration > TimeRange.WHOLE_DAY.duration()){
            listTransfer.clear();
        }
        else{
            listTransfer.add(TimeRange.WHOLE_DAY);
        }
        avaliableTime = listTransfer;
    }

    // If there are 2 or more events and the number of attendees are more than just one 
    // check for overlap and return avaliable timerange 
    else if(events.size() >= 2 && request.getAttendees().size() > 1){
        if(isOverlapped(eventArray) && eventTakeWholeDay(eventArray) == 0){
            
            // Check which event ends the latest since they overlap to find when both 
            // attendees are free. 
            int finalEndTime = secondEventEndTime;
            if(firstEventEndTime > secondEventEndTime){
                finalEndTime = firstEventEndTime;
            }

            listTransfer.add(TimeRange.fromStartEnd(TimeRange.START_OF_DAY, firstEventStartTime, false));
            listTransfer.add(TimeRange.fromStartEnd(finalEndTime, TimeRange.END_OF_DAY, true));
        }
        else{
            // No overlap means a middle free space.
            listTransfer.add(TimeRange.fromStartEnd(TimeRange.START_OF_DAY, firstEventStartTime, false));
            listTransfer.add(TimeRange.fromStartEnd(firstEventEndTime, secondEventStartTime, false));
            listTransfer.add(TimeRange.fromStartEnd(secondEventEndTime, TimeRange.END_OF_DAY, true));
        }
        avaliableTime = listTransfer;
    }

    // This else statements mostly applies to meeting requests that only
    // specifies for one person.
    else{
        if(isOverlapped(eventArray)){
            
            // If the one person has a meeting thats lasts the whole day
            // they have no free space.
            if(eventTakeWholeDay(eventArray) == events.size()){
                listTransfer.clear();
            }
            else{
                listTransfer.add(TimeRange.fromStartEnd(TimeRange.START_OF_DAY, firstEventStartTime, false));
                listTransfer.add(TimeRange.fromStartEnd(secondEventEndTime, TimeRange.END_OF_DAY, true));
            }
        }
        else if(!isOverlapped(eventArray)){

            long freeSpace = ((secondEventStartTime) - (firstEventEndTime));

            // If the request duration is greater than avaliable free time the 
            // program returns no timerange avaliable.
            if (request.getDuration() > freeSpace){
                listTransfer.clear();
            }
            // If its not greater than the avaliable free time then return
            // duration left after event.
            else{
                listTransfer.add(TimeRange.fromStartDuration(firstEventEndTime, requestDuration));
            }
        }
        avaliableTime = listTransfer;
    }
    return avaliableTime;
  }

  // Sets times of earliest event and latest events. If event
  // takes more than a day then ignore.
  void SetTimes(Event [] eventArray){
    for(int j = 0; j < eventArray.length; j++){
        if(j == 0){
            firstEventStartTime = eventArray[0].getWhen().start();
            firstEventEndTime = eventArray[0].getWhen().end();
            secondEventStartTime = eventArray[0].getWhen().start();
        }
        else if(eventArray[j].getWhen().duration() == TimeRange.WHOLE_DAY.duration()){}
        else if(eventArray[j].getWhen().start() < firstEventStartTime){
            firstEventStartTime = eventArray[j].getWhen().start();
            firstEventEndTime = eventArray[j].getWhen().end();
        }
        else if(eventArray[j].getWhen().start() > secondEventStartTime){
            secondEventStartTime = eventArray[j].getWhen().start();
            secondEventEndTime = eventArray[j].getWhen().end();
        }
    }
  }
  // Check if any events overlap with each other.
  Boolean isOverlapped(Event [] eventArray){
      Boolean overlap;
      int overlapFlag = 0;
      for(int j = 0; j< eventArray.length; j++){
          int nextEvent = j + 1;
          if(nextEvent == eventArray.length){
              nextEvent = j - (eventArray.length - 1);
          }
          overlap = eventArray[j].getWhen().overlaps(eventArray[nextEvent].getWhen());
          if(overlap){
              overlapFlag++;
          }
      }
      if(overlapFlag >= 1){
          return true;
      }
      else{
          return false;
      }
  }
  // Checks if any events take the whole day.
  int eventTakeWholeDay(Event [] eventArray){
      int wholedayCounter = 0;
      for(int i = 0; i < eventArray.length; i++){
            if(eventArray[i].getWhen().duration() ==  TimeRange.WHOLE_DAY.duration()){
                wholedayCounter++;
            }
        }
        return wholedayCounter;
  }
}
