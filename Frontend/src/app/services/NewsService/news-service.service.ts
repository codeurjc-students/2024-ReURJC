import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { NewsInfo } from './NewsInfo';

@Injectable({
  providedIn: 'root'
})
export class NewsServiceService {

  private readonly API_BASE_URL = '/api/v1/news';

  constructor(private http: HttpClient) { }

  getAll(pageNumber: number): Observable<NewsInfo[]> {
    const params = new HttpParams().set('pageNumber', pageNumber.toString());
    return this.http.get<NewsInfo[]>(`${this.API_BASE_URL}`, { withCredentials: false, params });
  }
}