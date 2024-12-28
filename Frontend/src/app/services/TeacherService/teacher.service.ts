import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Attendance } from '../UserService/Attendance';

@Injectable({
  providedIn: 'root'
})
export class TeacherService {

  private readonly API_BASE_URL = '/api/v1/teacher';

  constructor(private http: HttpClient) { }

  createAttendance(subjectId: number): Observable<any> {
    return this.http.post(`${this.API_BASE_URL}/newAttendance?subjectId=${subjectId}`, null, { withCredentials: true });
  }

  getAllAttendances(): Observable<Attendance[]> {
    return this.http.get<Attendance[]>(`${this.API_BASE_URL}/attendances`, { withCredentials: true });
  }

  addTimeToAttendance(attendanceId: number): Observable<boolean> {
    return this.http.put<boolean>(`${this.API_BASE_URL}/attendances/${attendanceId}/add-time`, null, { withCredentials: true });
  }
}