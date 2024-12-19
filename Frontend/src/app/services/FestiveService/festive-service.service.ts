import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { FestiveInfo } from './FestiveInfo';

@Injectable({
  providedIn: 'root'
})
export class FestiveServiceService {

  private readonly API_BASE_URL = '/api/v1/festives';

  constructor(private http: HttpClient) { }

  getAllFestives(): Observable<FestiveInfo[]> {
    return this.http.get<FestiveInfo[]>(`${this.API_BASE_URL}`, { withCredentials: false });
  }
}