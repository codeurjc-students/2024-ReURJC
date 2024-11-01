import { ConvocatoryInfo } from "./ConvocatoryInfo";

export class SubjectInfo {
     title: string = "";
     convocatory: ConvocatoryInfo[] = [];

    constructor(title: string, convocatory: ConvocatoryInfo[] ) {
        this.title = title;
        this.convocatory = convocatory;
    }
}