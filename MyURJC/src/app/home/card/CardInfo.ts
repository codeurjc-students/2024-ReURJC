export class CardInfo  {
    private _subtitle : string;
    private _title: string;
    private _description: string;
    private _apiCaller?: string;
  
    constructor(subtitle: string, title: string, description: string, apiCaller: string) {
      this._subtitle = subtitle;
      this._title = title;
      this._description = description;
      this._apiCaller = apiCaller;
      
      }
    
      get subtitle(): string {
          return this._subtitle;
      }
  
      get apiCaller(): string | undefined {
        return this._apiCaller;
    }
  
      get title(): string {
          return this._title;
      }
  
      get description(): string {
          return this._description;
      }
  
  
    
    }