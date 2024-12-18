import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { SportReservation } from '../UserService/SportReservation';
import { User } from '../UserService/user.model';
import { VoteDelegateEvent } from '../UserService/VoteDelegateEvent';

@Injectable({
  providedIn: 'root'
})
export class AdminService {

  constructor(private http: HttpClient) { }

  getAllReservations(): Observable<SportReservation[]> {
    return this.http.get<SportReservation[]>('/api/admin/reservations', { withCredentials: true });
  }

  getAllVotes(eventId: number): Observable<Object[]> {
    const params = { eventId: eventId.toString() };
    return this.http.get<Object[]>('/api/admin/votes', { withCredentials: true, params });
  }

  getAllEvents(): Observable<VoteDelegateEvent[]> {
    return this.http.get<VoteDelegateEvent[]>('/api/admin/events', { withCredentials: true });
  }

  getUser(userId: number): Observable<User> {
    const params = { userId: userId.toString() };
    return this.http.get<User>('/api/admin/user', { withCredentials: true, params });
  }
}