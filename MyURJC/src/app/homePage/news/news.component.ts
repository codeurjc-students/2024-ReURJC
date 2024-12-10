import { Component, OnInit } from '@angular/core';
import { CardInfo } from 'src/app/homePage/components/Card/card/CardInfo';
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
  disableNextPage: boolean = false;

  constructor(private newsService: NewsServiceService) { }

  ngOnInit() {
    this.loadNews();
  }

  loadNews() {
    this.newsService.getAll(this.pageNumber).subscribe((data) => {
      this.newsList = data;
      this.newsService.getAll(this.pageNumber + 1).subscribe((data => {
        this.disableNextPage = (data.length === 0)
      }
      ))
    });
  }

  getCardInfo(news: NewsInfo): CardInfo {
    return new CardInfo(
      news.title,
      news.date,
      `${news.description}`,
      "" // No necesitas apiCaller en este caso
    );
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
