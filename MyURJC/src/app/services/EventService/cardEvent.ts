import { CardInfo } from "src/app/home/card/CardInfo";

export class CardEvent extends CardInfo {
    private _tabsDisplay: number;
    private _isValid: boolean;

    constructor(subtitle: string, title: string, description: string, apiCaller: string, tabDisplay: number, isValid: boolean) {
        super(subtitle, title, description, apiCaller);
        this._isValid = isValid;
        this._tabsDisplay = tabDisplay;
    }


    get tabDisplay():number {
        return this._tabsDisplay
    }

    get isValid():boolean {
        return this._isValid
    }
}