import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { map, Observable } from 'rxjs';
import { NewsInfo } from './NewsInfo';

@Injectable({
  providedIn: 'root'
})
export class NewsServiceService {

  constructor(private http: HttpClient) {}

  getAll(pageNumber: number) : Observable<NewsInfo[]> {
    return this.http.get<any[]>(`/api/news?pageNumber=${pageNumber}`,{ withCredentials: false }).pipe(
      map(response => this.transformToNewsInfo(response))
    )
  }
  transformToNewsInfo(data: any[]) : NewsInfo[] {
    return data.map((news: {newsId: number; title: string; description: string; date: string}) =>
      new NewsInfo(news.newsId,news.title,news.description,news.date)

    );
  }
}
