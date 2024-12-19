import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { SubjectInfo } from './SubjectInfo';
import { ConvocatoryInfo } from './ConvocatoryInfo';
import { User } from './user.model';
import { SubjectMark } from './SubjectMark';
import { SportReservation } from './SportReservation';

@Injectable({
  providedIn: 'root'
})
export class ApiUserService {

  private readonly API_BASE_URL = '/api/v1';

  constructor(private http: HttpClient) { }

  // Obtener todos los elementos
  getAllUserSubjects(): Observable<SubjectInfo[]> {
    return this.http.get<SubjectInfo[]>(`${this.API_BASE_URL}/users/me/subjects`, { withCredentials: true }).pipe(
      map(response => this.transformToSubjectInfo(response)),
    )
  }

  private transformToSubjectInfo(data: any[]): SubjectInfo[] {
    return data.map(item => {
      const convocatories = item.convocatories.map((convocatory: { convocatoriaId: number; date: string; convocatory: number; classroom: string; }) =>
        new ConvocatoryInfo(
          convocatory.convocatoriaId,
          convocatory.date,
          convocatory.convocatory,
          convocatory.classroom
        )
      );
      return new SubjectInfo(item.id, item.title, convocatories);
    });
  }

  reqIsDelegateCandidate(): Observable<boolean> {
    return this.http.get<boolean>(`${this.API_BASE_URL}/users/me/isDelegate`, { withCredentials: true });
  }

  becomeCandidate() {
    return this.http.put(`${this.API_BASE_URL}/users/me/becomeDelegate`, {}, { withCredentials: true });
  }

  cancelCandidacy() {
    return this.http.put(`${this.API_BASE_URL}/users/me/cancelCandidacy`, {}, { withCredentials: true });
  }

  getCandidates(): Observable<User[]> {
    return this.http.get<User[]>(`${this.API_BASE_URL}/users/candidates`, { withCredentials: false })
  }

  vote(candidate: number): Observable<string> {
    return this.http.post<string>(`${this.API_BASE_URL}/events/vote`, candidate, { withCredentials: true })
  }

  hasVoted(): Observable<boolean> {
    return this.http.get<boolean>(`${this.API_BASE_URL}/users/me/hasVoted`, { withCredentials: true });
  }

  getGrades(): Observable<SubjectMark[]> {
    return this.http.get<SubjectMark[]>(`${this.API_BASE_URL}/users/me/grades`, { withCredentials: true });
  }

  reqIsUserWithReservation(): Observable<boolean> {
    return this.http.get<boolean>(`${this.API_BASE_URL}/users/hasReservation`, { withCredentials: true });
  }

  newSportReservation(reservationData: any): Observable<boolean> {
    return this.http.post<boolean>(`${this.API_BASE_URL}/users/newReservation`, reservationData);
  }

  getSportReservations(pista: number, reservationInfo: any): Observable<SportReservation[]> {
    const params = new HttpParams({
      fromObject: {
        pista: pista.toString(),
        año: reservationInfo.año.toString(),
        mes: reservationInfo.mes.toString(),
        dia: reservationInfo.dia.toString()
      }
    });
    return this.http.get<SportReservation[]>(`${this.API_BASE_URL}/users/getReservations`, {
      withCredentials: true,
      params: params
    });
  }

  hasSportReservation(): Observable<boolean> {
    return this.http.get<boolean>(`${this.API_BASE_URL}/users/hasReservation`, { withCredentials: true });
  }

  getMyReservation(): Observable<SportReservation> {
    return this.http.get<SportReservation>(`${this.API_BASE_URL}/users/me/getReservation`, { withCredentials: true });
  }

  cancelReservation(): Observable<boolean> {
    return this.http.delete<boolean>(`${this.API_BASE_URL}/users/me/deleteReservation`, { withCredentials: true });
  }

  getUserCarnet(): Observable<Blob> {
    return this.http.get(`${this.API_BASE_URL}/users/me/carnet`, { responseType: 'blob', withCredentials: true });
  }

  joinAttendance(code: string): Observable<any> {
    return this.http.post(`${this.API_BASE_URL}/users/newAttendance?code=${code}`, {}, { withCredentials: true });
  }
}