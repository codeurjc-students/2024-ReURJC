import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { SubjectScheduleResponse } from './ResponseInfo/SubjectScheduleResponse';
import { SubjectInfo } from '../UserService/SubjectInfo';

@Injectable({
  providedIn: 'root'
})
export class SubjectServiceService {

  constructor(private http: HttpClient) { }

  getSchedule(): Observable<SubjectScheduleResponse[]> {
    return this.http.get<SubjectScheduleResponse[]>(`/api/subjects/schedule`, { withCredentials: true });

  }

  getSubjects(): Observable<SubjectInfo[]> {
    return this.http.get<SubjectInfo[]>(`/api/subjects/`, { withCredentials: true });
  }
}
