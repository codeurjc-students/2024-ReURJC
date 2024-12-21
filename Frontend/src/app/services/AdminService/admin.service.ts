import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { SportReservation } from '../UserService/SportReservation';
import { User } from '../UserService/user.model';
import { VoteDelegateEvent } from '../UserService/VoteDelegateEvent';

@Injectable({
  providedIn: 'root'
})
export class AdminService {

  private readonly API_BASE_URL = '/api/v1/admin';

  constructor(private http: HttpClient) { }

  getAllReservations(): Observable<SportReservation[]> {
    return this.http.get<SportReservation[]>(`${this.API_BASE_URL}/reservations`, { withCredentials: true });
  }

  getAllVotes(eventId: number): Observable<Object[]> {
    const params = new HttpParams().set('eventId', eventId.toString());
    return this.http.get<Object[]>(`${this.API_BASE_URL}/votes`, { withCredentials: true, params });
  }

  getAllEvents(): Observable<VoteDelegateEvent[]> {
    return this.http.get<VoteDelegateEvent[]>(`${this.API_BASE_URL}/events`, { withCredentials: true });
  }

  getUser(userId: number): Observable<User> {
    const params = new HttpParams().set('userId', userId.toString());
    return this.http.get<User>(`${this.API_BASE_URL}/user`, { withCredentials: true, params });
  }
}