export class CardInfo  {
  private _subtitle : string;
  private _title: string;
  private _description: string;
  private _apiController: string = '';

  constructor(subtitle: string, title: string, description: string) {
    this._subtitle = subtitle;
    this._title = title;
    this._description = description;
    
    }
  
    get subtitle(): string {
        return this._subtitle;
    }

    get title(): string {
        return this._title;
    }

    get description(): string {
        return this._description;
    }


  
  }