import { Component, OnInit } from '@angular/core';
import { NewsServiceService } from 'src/app/services/NewsService/news-service.service';
import { NewsInfo } from 'src/app/services/NewsService/NewsInfo';

@Component({
  selector: 'app-news',
  templateUrl: './news.component.html',
  styleUrls: ['./news.component.scss'],
})
export class NewsComponent implements OnInit {
  newsList: NewsInfo[] = [];
  pageNumber: number = 0;
  disableNextPage : boolean = false;
  
  constructor(private newsService: NewsServiceService) {}

  ngOnInit() {
    this.loadNews();
  }

  loadNews() {
    this.newsService.getAll(this.pageNumber).subscribe((data) => {
      this.newsList = data;
    this.newsService.getAll(this.pageNumber + 1).subscribe((data =>
      {
        this.disableNextPage = (data.length === 0)
      }
      ))
    });
  }

  nextPage() {
    this.pageNumber++;
    this.loadNews();
  }

  previousPage() {
    if (this.pageNumber > 0) {
      this.pageNumber--;
      this.loadNews();
    }
  }
}
