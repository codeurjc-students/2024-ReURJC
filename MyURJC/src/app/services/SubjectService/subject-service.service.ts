import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { SubjectScheduleResponse } from './ResponseInfo/SubjectScheduleResponse';

@Injectable({
  providedIn: 'root'
})
export class SubjectServiceService {

  constructor(private http: HttpClient) { }

  getSchedule(): Observable<SubjectScheduleResponse[]> {
    return this.http.get<SubjectScheduleResponse[]>(`/api/subjects/schedule`, { withCredentials: true });

  }
}
