export class NewsInfo {
    newsId: number;
    title: string;
    description: string;
    date: string;

    constructor(newsId: number, title: string, description: string, date: string) {
        this.newsId = newsId;
        this.title = title;
        this.description = description;
        this.date = date;
    }

}