import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map, tap } from 'rxjs/operators';
import { SubjectInfo } from './SubjectInfo';
import { ConvocatoryInfo } from './ConvocatoryInfo';
import { User } from './user.model';

@Injectable({
  providedIn: 'root'
})
export class ApiUserService {
  
  
  constructor(private http: HttpClient) {}

  // Obtener todos los elementos
  getAllUserSubjects(): Observable<SubjectInfo[]> {
    return this.http.get<SubjectInfo[]>(`/api/users/me/subjects`,{ withCredentials: true }).pipe(
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
      return new SubjectInfo(item.title, convocatories);
    });
  }

  reqIsDelegateCandidate(): Observable<boolean> {
    return this.http.get<boolean>("/api/users/me/isDelegate", { withCredentials: true });
  }

  becomeCandidate() {
    return this.http.post("/api/users/me/becomeDelegate", { withCredentials: true });
  }

  cancelCandidacy() {
    return this.http.post("/api/users/me/cancelCandidacy", { withCredentials: true });
  }

  getCandidates(): Observable<User[]> {
    return this.http.get<User[]>("/api/users/candidates",{ withCredentials: false })
  }

  vote(candidate: number) {
    this.http.post<boolean>("/api/events/vote",candidate,{ withCredentials: true }).subscribe(data => {
      console.log(data);
    })
    
  }

  hasVoted(): Observable<boolean> {
    return this.http.get<boolean>('/api/users/me/hasVoted', { withCredentials: true });
  }
}
