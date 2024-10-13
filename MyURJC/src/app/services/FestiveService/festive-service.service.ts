import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { map, Observable, tap } from 'rxjs';
import { FestiveInfo } from './FestiveInfo';

@Injectable({
  providedIn: 'root'
})
export class FestiveServiceService {

  constructor(private http: HttpClient) {}

  getAll(): Observable<FestiveInfo[]> {
    return this.http.get<any[]>(`/api/festives`, { withCredentials: false }).pipe(
      map(response => this.transformToFestiveInfo(response)),
      tap({
        next: festives => {
          // Aquí puedes realizar alguna acción con los datos obtenidos si es necesario
        },
        error: err => {
          console.error('Error al obtener los festivos:', err); // Mostrar solo errores
        }
      })
    );
  }

  private transformToFestiveInfo(data: any[]): FestiveInfo[] {
    // Mapeamos directamente la lista de objetos con day, month y year
    return data.map((festive: { day: number; month: number; year: number; color: string; startedXDaysAgo: number; local: string}) => 
      new FestiveInfo(festive.day, festive.month, festive.year, festive.color, festive.startedXDaysAgo, festive.local)
    );
  }
}
