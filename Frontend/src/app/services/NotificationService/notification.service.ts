import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { NotificationInfo } from './NotificationInfo';

@Injectable({
  providedIn: 'root'
})
export class NotificationService {

  private readonly API_BASE_URL = '/api/v1/notifications';

  constructor(private http: HttpClient) { }

  getNotifications(): Observable<NotificationInfo[]> {
    return this.http.get<NotificationInfo[]>(`${this.API_BASE_URL}/`, { withCredentials: true });
  }
}