import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { NewsInfo } from './NewsInfo';

@Injectable({
  providedIn: 'root'
})
export class NewsServiceService {

  constructor(private http: HttpClient) { }

  getAll(pageNumber: number): Observable<NewsInfo[]> {
    return this.http.get<NewsInfo[]>(`/api/news?pageNumber=${pageNumber}`, { withCredentials: false })

  }

}
