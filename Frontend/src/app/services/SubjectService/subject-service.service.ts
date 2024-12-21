import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { SubjectScheduleResponse } from './ResponseInfo/SubjectScheduleResponse';
import { SubjectInfo } from '../UserService/SubjectInfo';

@Injectable({
  providedIn: 'root'
})
export class SubjectServiceService {

  private readonly API_BASE_URL = '/api/v1/subjects';

  constructor(private http: HttpClient) { }

  getSchedule(): Observable<SubjectScheduleResponse[]> {
    return this.http.get<SubjectScheduleResponse[]>(`${this.API_BASE_URL}/schedule`, { withCredentials: true });
  }

  getSubjects(): Observable<SubjectInfo[]> {
    return this.http.get<SubjectInfo[]>(`${this.API_BASE_URL}/`, { withCredentials: true });
  }
}