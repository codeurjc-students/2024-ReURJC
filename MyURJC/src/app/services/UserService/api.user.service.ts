import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map, tap } from 'rxjs/operators';
import { SubjectInfo } from './SubjectInfo';
import { ConvocatoryInfo } from './ConvocatoryInfo';

@Injectable({
  providedIn: 'root'
})
export class ApiUserService {
  constructor(private http: HttpClient) {}

  // Obtener todos los elementos
  getAll(): Observable<SubjectInfo[]> {
    return this.http.get<SubjectInfo[]>(`/api/users/me/subjects`,{ withCredentials: true }).pipe(
      map(response => this.transformToSubjectInfo(response)),
      tap({
        next: subjects => {
          // Aquí se puede omitir cualquier log si no es necesario
        },
        error: err => {
          console.error('Error al obtener las asignaturas:', err); // Mostrar solo errores
        }
      })
    );
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
}
