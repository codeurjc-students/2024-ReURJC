import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { CardEvent } from './cardEvent';

@Injectable({
  providedIn: 'root'
})
export class EventServiceService {
  

  constructor(private http: HttpClient) {}

  getAllEvents(): Observable<CardEvent[]> {
    return this.http.get<any[]>('/api/events', { withCredentials: false })
      .pipe(
        map(events => events.map(event => 
          new CardEvent(
            event.subtitle, 
            event.title, 
            event.description, 
            event.apiCaller, 
            event.tabsDisplay, 
            event.isValid,
            event.eventId
          )
        ))
      );
  }

  isBecomeDelegateActivated() : Observable<boolean> {
    return this.http.get<boolean>("/api/events/isDelegateActivated");
  }

  isVoteDelegateActivated() : Observable<boolean> {
    return this.http.get<boolean>("/api/events/isVoteDelegateActivated");
  }
}
