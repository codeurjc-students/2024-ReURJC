export class NewsInfo {
    private newsId: number;
    private title: string;
    private description: string;
    private date: string;

    constructor(newsId: number, title: string, description: string, date: string) {
        this.newsId = newsId;
        this.title = title;
        this.description = description;
        this.date = date;
    }

    // Getters
    public getNewsId(): number {
        return this.newsId;
    }

    public getTitle(): string {
        return this.title;
    }

    public getDescription(): string {
        return this.description;
    }


    public getDate(): string {
        return this.date;
    }
}
