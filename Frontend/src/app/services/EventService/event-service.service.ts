import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { CardEvent } from './cardEvent';

@Injectable({
  providedIn: 'root'
})
export class EventServiceService {

  private readonly API_BASE_URL = '/api/v1/events';

  constructor(private http: HttpClient) { }

  getAllEvents(): Observable<CardEvent[]> {
    return this.http.get<any[]>(`${this.API_BASE_URL}`, { withCredentials: false })
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

  isBecomeDelegateActivated(): Observable<boolean> {
    return this.http.get<boolean>(`${this.API_BASE_URL}/isDelegateActivated`);
  }

  isVoteDelegateActivated(): Observable<boolean> {
    return this.http.get<boolean>(`${this.API_BASE_URL}/isVoteDelegateActivated`);
  }
}