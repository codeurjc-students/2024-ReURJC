import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Attendance } from '../UserService/Attendance';

@Injectable({
  providedIn: 'root'
})
export class TeacherService {

  private apiUrl = '/api/teacher';

  constructor(private http: HttpClient) {}
  
  createAttendance(subjectId: number): Observable<any> {
    return this.http.post(`${this.apiUrl}/newAttendance?subjectId=${subjectId}`, null, { withCredentials: true });
  }

  getAllAttendances(): Observable<Attendance[]> {
    return this.http.get<Attendance[]>(`${this.apiUrl}/attendances`, { withCredentials: true });
  }
}
