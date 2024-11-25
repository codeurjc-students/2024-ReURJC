import { CardInfo } from "src/app/home/card/CardInfo";

export class CardEvent extends CardInfo {
    private _tabsDisplay: number;
    private _isValid: boolean;
    private _eventId: boolean;

    constructor(subtitle: string, title: string, description: string, apiCaller: string, tabDisplay: number, isValid: boolean, eventId: boolean) {
        super(subtitle, title, description, apiCaller);
        this._isValid = isValid;
        this._tabsDisplay = tabDisplay;
        this._eventId = eventId;
    }


    get tabDisplay():number {
        return this._tabsDisplay
    }

    get isValid():boolean {
        return this._isValid
    }

    get eventId():boolean {
        return this._eventId
    }
}