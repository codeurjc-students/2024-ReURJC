import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { FestiveInfo } from './FestiveInfo';

@Injectable({
  providedIn: 'root'
})
export class FestiveServiceService {

  constructor(private http: HttpClient) { }

  getAllFestives(): Observable<FestiveInfo[]> {
    return this.http.get<FestiveInfo[]>(`/api/festives`, { withCredentials: false })

  }

}
