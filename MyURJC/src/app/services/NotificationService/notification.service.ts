import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { SubjectMark } from '../UserService/SubjectMark';
import { NotificationInfo } from './NotificationInfo';

@Injectable({
  providedIn: 'root'
})
export class NotificationService {

  constructor(private http: HttpClient) { }

  getNotifications(): Observable<NotificationInfo[]> {
    return this.http.get<NotificationInfo[]>(`/api/notifications/`, { withCredentials: true });
  }
}
