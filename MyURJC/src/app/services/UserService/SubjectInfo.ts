import { ConvocatoryInfo } from "./ConvocatoryInfo";

export class SubjectInfo {
    private title: string = "";
    private convocatory: ConvocatoryInfo[] = [];

    constructor(title: string, convocatory: ConvocatoryInfo[] ) {
        this.title = title;
        this.convocatory = convocatory;
    }

    public  getTitle():string {
        return this.title;
    }

    public  getConvocatory():ConvocatoryInfo[] {
        return this.convocatory;
    }
}